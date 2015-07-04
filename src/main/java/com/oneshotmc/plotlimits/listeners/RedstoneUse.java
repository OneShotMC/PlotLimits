package com.oneshotmc.plotlimits.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.RedstoneWire;


import org.bukkit.material.Wool;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotId;
import com.oneshotmc.plotlimits.Cubic;
import com.oneshotmc.plotlimits.CubicMultiplicity;
import com.oneshotmc.plotlimits.PlotLimits;
import com.oneshotmc.plotlimits.Settings;
import com.oneshotmc.plotlimits.files.FileLoader;
import com.oneshotmc.plotlimits.storage.CubicMultiplicityGrouper;
import com.oneshotmc.plotlimits.util.ChatType;
import com.oneshotmc.plotlimits.util.ChatUtil;

public class RedstoneUse implements Listener{
	BlockFace[] blockFaceValues;
	int bfvLENGTH;
	PlotLimits plugin;
	PlotAPI api = new PlotAPI();
	Settings s;
	public RedstoneUse(PlotLimits plugin){
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin=plugin;
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
			
			//
			public void run() {
				for(CubicMultiplicityGrouper cmul : cubiclist.values()){
					if(cmul.getHasGrown()==false){
						cmul.getList(false).clear();
					}
					else{
						cmul.setHasGrown(false);
					}
				}
			}
			
		}, 60, 60);
		this.s = new Settings(plugin);
		this.blockFaceValues = BlockFace.values();
		this.bfvLENGTH = blockFaceValues.length;
	}
	HashMap<PlotId,CubicMultiplicityGrouper> cubiclist= new HashMap<PlotId,CubicMultiplicityGrouper>();
	
	//Prevent the program from thinking a player rapidly flicking/pressing a button or lever is a clock
	@EventHandler (priority=EventPriority.HIGHEST,ignoreCancelled=false)
	public void onClick(PlayerInteractEvent e){
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			Block block = e.getClickedBlock();
			Material blockType = block.getType();
			PlotId id = null;
			try{
			id = api.getPlot(block.getLocation()).getId();
			}
			catch(NullPointerException ex){}
			switch(blockType){
			case WOOD_BUTTON :
			case STONE_BUTTON :
			case LEVER :
				try{
				CubicMultiplicityGrouper cubList = cubiclist.get(id);
				cubList.getList(false).clear();
				cubList.setMainMessageSent(false);
				cubList.setWarnMessageSent(false);
				}
				catch(NullPointerException exception){
					//No need to clear
					}
				break;
			default:
				break;
			}
		}
	}
	@EventHandler (priority=EventPriority.HIGHEST,ignoreCancelled=false)
	public void redstoneActivation(BlockRedstoneEvent e){
		Block block = e.getBlock();
		Material type = block.getType();
		//possible fix
		switch(type){
			case REDSTONE_COMPARATOR:
			case REDSTONE_COMPARATOR_OFF:
			case REDSTONE_COMPARATOR_ON:
				return;
			default:
		}
		int oldCurrent = e.getOldCurrent();
		int newCurrent = e.getNewCurrent();
		if(oldCurrent!=0/*||newCurrent==0*/)return;
		Location loc = block.getLocation();
		World world = loc.getWorld();
		ConfigurationSection worldconfig =  FileLoader.getWorldPerms((YamlConfiguration) plugin.getConfig(), world);
		if(worldconfig==null)return;
		ConfigurationSection redstone = worldconfig.getConfigurationSection("redstone");
		if(redstone==null)return;
		Plot plot = api.getPlot(loc);
		if(plot.getId()==null||plot==null){
			plugin.getLogger().info("couldn't find plot");
			return;
		}
		int CONFIGmaxClockRepeat = redstone.getInt("maxclockrepeat");
		int CONFIGmaxredstone = redstone.getInt("maxredstonepercheck");
		int mul;
		int totalm;
		CubicMultiplicity cub;
		CubicMultiplicityGrouper cmg;
		ArrayList<CubicMultiplicity> list;
		final PlotId plotId;
		plotId= plot.getId();
		cmg = cubiclist.get(plotId);
		if(cmg==null){
			cubiclist.put(plotId, new CubicMultiplicityGrouper());
			cmg = cubiclist.get(plotId);
		}
		list = cmg.getList(true);
		cub = getMultiplicity(list, new Cubic(loc));
		cub.addOneMultiplicity();
		mul=cub.getMutiplicity();
		//totalm = getTotalMultiplicity(list);

		int CLOCKblocksWarnBefore=redstone.getInt("blocksclockbeforewarn");
		int MAINblocksWarnBefore = redstone.getInt("blocksbeforewarn");
		//If the multiplicity (how many times the redstone has been triggered in a row) is greater than the max amount set in the config
		if(CONFIGmaxClockRepeat<=mul){
			//See if the main message has been sent already. isMainMessageSent is manually changed to false after 20 game ticks to prevent spamming
			if(!(cmg.isMainMessageSent())&&mul==CONFIGmaxClockRepeat){
			ChatUtil.sendMessage(api.getPlot(loc), ChatColor.BOLD+"Too much redstone clock activity in your plot!", ChatType.WARNING);
			//Tell the object that holds information about the plots redstone events that a message has been sent.  
			cmg.setMainMessageSent(true);
			//Schedule an event to take place every 20 game ticks (10 redstone ticks) to prevent spamming chat
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
				//Make it so isMainMessageSent is false so the message can be called again. The delay is to prevent spamming chat
				public void run() {
					CubicMultiplicityGrouper oldCMG = cubiclist.get(plotId);
					//Who killed it? :o
					if(oldCMG==null)return;
					//Mark messages as not sent so the program can send them again.
					oldCMG.setMainMessageSent(false);
					oldCMG.setWarnMessageSent(false);
				}
				
			}, 20);
			}
			//Clear the current of the redstone so it can't power anything else
			e.setNewCurrent(0);
			return;
		}
		//If the config amount for blocksbeforewarn is less than the TOTAL amount of redstone used continuously stop all redstone in the plot.
		/*
		else if(CONFIGmaxredstone<totalm){
			for(CubicMultiplicity c : list){
				Location newLocation = new Location(world,c.getX(),c.getY(),c.getZ());
				turnOffRedstone(newLocation);
				c.setMultiplicty(0);
				System.out.println("mul was:"+c.getMutiplicity());
			}
			if(!(cmg.isMainMessageSent())){
			cmg.setMainMessageSent(true);
			ChatUtil.sendMessage(api.getPlot(loc), ChatColor.BOLD+"Too much redstone activity in your plot!", ChatType.WARNING);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
				//Make it so isMainMessageSent is false so the message can be called again. The delay is to prevent spamming chat
				public void run() {
					CubicMultiplicityGrouper oldCMG = cubiclist.get(plotId);
					if(oldCMG==null)return;
					oldCMG.setMainMessageSent(false);
					oldCMG.setWarnMessageSent(false);
				}
				
			}, 20);
			}
			return;
		}
		*/
		else if(!(cmg.isWarnMessageSent())&&(/*CONFIGmaxredstone- CLOCKblocksWarnBefore==getTotalMultiplicity(list) || */CONFIGmaxClockRepeat- MAINblocksWarnBefore==mul)){
			
			ChatUtil.sendMessage(plot, "You have almost reached your max clock repeat amount!", ChatType.WARNING);
			cmg.setWarnMessageSent(true);
			return;
		}
	}
	public CubicMultiplicity getMultiplicity(ArrayList<CubicMultiplicity> list1, Cubic cm){
		
		int index = list1.indexOf(cm);
		if(index==-1){
			list1.add(new CubicMultiplicity(cm.getX(),cm.getY(),cm.getZ()));
			return list1.get(list1.size()-1);
		}
		return list1.get(index);
	}
	public int getTotalMultiplicity(List<CubicMultiplicity> li){
		int m=0;
		for(CubicMultiplicity c : li){
			m+=c.getMutiplicity();
		}
		return m;
	}
	@SuppressWarnings("deprecation")
	public void turnOffRedstone(Location loca){
		Block block = loca.getBlock();
		Material mat = block.getType();
		s.sendDebugMessage("m is: " + mat);
		switch(mat){
		case DIODE_BLOCK_ON:
		case DIODE_BLOCK_OFF:
			block.setType(Material.DIODE_BLOCK_OFF);
			break;
		case REDSTONE_BLOCK:
			block.setType(Material.COAL);
			break;
		case REDSTONE_LAMP_OFF:
		case REDSTONE_LAMP_ON:
			block.setType(Material.REDSTONE_LAMP_OFF);
			break;
		case REDSTONE_TORCH_OFF:
		case REDSTONE_TORCH_ON:
			block.setType(Material.REDSTONE_TORCH_OFF);
			break;
		case REDSTONE_WIRE:
			break;
			/*
			block.setType(Material.SIGN);
			Sign sign = (Sign) block.getState();
			Random random = new Random();
			int randomCase = random.nextInt(4);
			List<String> signLines = new ArrayList<String>(2);
			switch(randomCase){
			case 0:
				signLines.add(0, "Rest in Pizza:");
				signLines.add(1, "Abusers");
				break;
			case 2:
				signLines.add(0, "Y U LAG SERVER");
				signLines.add(1, "I cri everityme");
				break;
			case 3:
				signLines.add(0,"op cannon?");
				signLines.add(1,"hax confirmed");
				break;
			case 4:
				signLines.add(0,"r u trying to");
				signLines.add(1,"go to le moon?");
				break;
			}
			sign.setLine(2,signLines.get(0));
			sign.setLine(3,signLines.get(1));
			org.bukkit.material.Sign signData = (org.bukkit.material.Sign) sign.getData();
			signData.setFacingDirection(blockFaceValues[random.nextInt(this.bfvLENGTH-1)]);
		default:
			s.sendDebugMessage("WOT! Material is: " + mat);
			break;
			*/
		}
		
	}
	public boolean isPowered(Material mater){
		switch(mater){
		case DIODE_BLOCK_ON:
		case REDSTONE_BLOCK:
		case REDSTONE_LAMP_ON:
		case REDSTONE_TORCH_ON:
			return true;
		default:
			return false;
		}
	}
}
