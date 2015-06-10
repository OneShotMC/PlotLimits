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
			PlotId id = api.getPlot(block.getLocation()).getId();
			switch(blockType){
			case WOOD_BUTTON :
			case STONE_BUTTON :
			case LEVER :
				cubiclist.get(id).getList(false).clear();
				break;
			default:
				break;
			}
		}
	}
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
		int CONFIGmaxClockRepeat = redstone.getInt("maxclockrepeat");
		int CONFIGmaxredstone = redstone.getInt("maxredstonepercheck");
		ArrayList<CubicMultiplicity> lis = cubiclist.get(plot.getId()).getList(true);
		CubicMultiplicity cmu= getMultiplicity(lis, new Cubic(loc));
		cmu.addOneMultiplicity();
		int multiplicity = cmu.getMutiplicity();
		int blocksWarnBefore=redstone.getInt("blocksbeforewarn");
		if(CONFIGmaxClockRepeat<multiplicity){
			ChatUtil.sendMessage(api.getPlot(loc), ChatColor.BOLD+"Too much redstone activity in your plot!", ChatType.WARNING);
			e.setNewCurrent(e.getOldCurrent());
			return;
		}
		else if(CONFIGmaxredstone<getTotalMultiplicity(lis)){
			for(Cubic c : lis){
				Location newLocation = new Location(world,c.getX(),c.getY(),c.getZ());
				turnOffRedstone(newLocation);
			}
			ChatUtil.sendMessage(api.getPlot(loc), ChatColor.BOLD+"Too much redstone activity in your plot!", ChatType.WARNING);
			return;
		}
		else if(CONFIGmaxredstone<getTotalMultiplicity(lis)-blocksWarnBefore || CONFIGmaxClockRepeat<multiplicity - blocksWarnBefore){
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
		switch(mat){
		case DIODE_BLOCK_OFF:
		case DIODE_BLOCK_ON:
			loca.getBlock().setType(Material.DIODE_BLOCK_OFF);
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
	}
}
