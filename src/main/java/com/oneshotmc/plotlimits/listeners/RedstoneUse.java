package com.oneshotmc.plotlimits.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotId;
import com.oneshotmc.plotlimits.Cubic;
import com.oneshotmc.plotlimits.CubicMultiplicity;
import com.oneshotmc.plotlimits.PlotLimits;
import com.oneshotmc.plotlimits.files.FileLoader;
import com.oneshotmc.plotlimits.storage.CubicMultiplicityGrouper;
import com.oneshotmc.plotlimits.util.ChatType;
import com.oneshotmc.plotlimits.util.ChatUtil;

public class RedstoneUse implements Listener{
	PlotLimits plugin;
	PlotAPI api = new PlotAPI();
	public RedstoneUse(PlotLimits plugin){
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin=plugin;
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){

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
			
		}, 6, 6);
	}
	HashMap<PlotId,CubicMultiplicityGrouper> cubiclist= new HashMap<PlotId,CubicMultiplicityGrouper>();
	
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
				cubiclist.get(id).getList(false).clear();
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
	@SuppressWarnings("deprecation")
	@EventHandler (priority=EventPriority.HIGHEST,ignoreCancelled=true)
	public void redstoneActivation(BlockRedstoneEvent e){
		Block block = e.getBlock();
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

		cmg = cubiclist.get(plot.getId());
		if(cmg==null){
			cubiclist.put(plot.getId(), new CubicMultiplicityGrouper());
			cmg = cubiclist.get(plot.getId());
		}
		list = cmg.getList(true);
		cub = getMultiplicity(list, new Cubic(loc));
		mul = cub.addOneMultiplicity();
		totalm = getTotalMultiplicity(list);

		int blocksWarnBefore=redstone.getInt("blocksbeforewarn");
		if(CONFIGmaxClockRepeat<mul){
			System.out.println(Material.getMaterial(e.getOldCurrent()));
			ChatUtil.sendMessage(api.getPlot(loc), ChatColor.BOLD+"Too much redstone clock activity in your plot!", ChatType.WARNING);
			turnOffRedstone(loc);
			cub.setMultiplicty(0);
			return;
		}
		else if(CONFIGmaxredstone<totalm){
			for(CubicMultiplicity c : list){
				Location newLocation = new Location(world,c.getX(),c.getY(),c.getZ());
				turnOffRedstone(newLocation);
				c.setMultiplicty(0);
			}
			ChatUtil.sendMessage(api.getPlot(loc), ChatColor.BOLD+"Too much redstone activity in your plot!", ChatType.WARNING);
			return;
		}
		else if(CONFIGmaxredstone- blocksWarnBefore==getTotalMultiplicity(list) || CONFIGmaxClockRepeat- blocksWarnBefore==mul){
			
			ChatUtil.sendMessage(plot, "You have almost reached your max clock repeat amount!", ChatType.WARNING);
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
	public void turnOffRedstone(Location loca){
		Block block = loca.getBlock();
		Material mat = block.getType();
		block.setType(Material.COAL_ORE);
		/*switch(mat){
		case DIODE_BLOCK_ON:
			loca.getBlock().setType(Material.SAND);
			break;
		case REDSTONE_BLOCK:
			loca.getBlock().setType(Material.COAL);
			break;
		case REDSTONE_LAMP_OFF:
		case REDSTONE_LAMP_ON:
			loca.getBlock().setType(Material.REDSTONE_LAMP_OFF);
			break;
		case REDSTONE_TORCH_OFF:
		case REDSTONE_TORCH_ON:
			loca.getBlock().setType(Material.REDSTONE_TORCH_OFF);
			break;
		default:
			break;
		
		}
		*/
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
