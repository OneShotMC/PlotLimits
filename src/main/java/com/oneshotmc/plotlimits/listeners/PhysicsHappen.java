

package com.oneshotmc.plotlimits.listeners;

import java.util.HashMap;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotId;
import com.oneshotmc.plotlimits.LiteLocation;
import com.oneshotmc.plotlimits.PlotLimits;
import com.oneshotmc.plotlimits.storage.ConfigMaterialStorage;
import com.oneshotmc.plotlimits.storage.LiteLocationStorage;
import com.oneshotmc.plotlimits.util.ChatType;
import com.oneshotmc.plotlimits.util.ChatUtil;

public class PhysicsHappen implements Listener {
	PlotLimits plugin;
	ConfigurationSection cs;
	//String = worldName , ConfigMaterialStorage = Materials to search for in worldName
	private HashMap<String,ConfigMaterialStorage> hm = new HashMap<String,ConfigMaterialStorage>();
	public PhysicsHappen(PlotLimits plugin){
		this.plugin=plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){

			public void run() {
				storage.clear();
			}
			
		}, 20, 20);
		cs = plugin.files.getConfigYML().getConfigurationSection("worlds");
	}
	public void setupConfig(){
		for(String srt : cs.getKeys(false)){
			ConfigurationSection cs2 = cs.getConfigurationSection(srt);
			hm.put(srt, new ConfigMaterialStorage());
			for(String materialName : cs2.getStringList("physics.materials")){
				try{
				hm.get(srt).list.add(Material.valueOf(materialName));
				}
				catch(IllegalArgumentException e){
					plugin.getLogger().warning(materialName +" is not a valid material!");
				}
			}
		}
	}
	PlotAPI api = new PlotAPI();
	HashMap<PlotId, LiteLocationStorage> storage = new HashMap<PlotId, LiteLocationStorage>();
	
	@EventHandler (priority=EventPriority.HIGHEST,ignoreCancelled=true)
	public void onPhysics(BlockPhysicsEvent e){
		Block block = e.getBlock();
		Location loc = block.getLocation();
		World world = loc.getWorld();
		Material material = e.getChangedType();
		if(hm.get(world.getName()).list.contains(material)){
			Plot plot = api.getPlot(loc);
			if(!(storage.containsKey(plot))){
				LiteLocationStorage lls = new LiteLocationStorage();
				lls.storage.add(new LiteLocation(loc));
				storage.put(plot.getId(), lls);
			}
			else{
				if(storage.get(plot.getId()).storage.size()>20){
					e.setCancelled(true);
					ChatUtil.sendMessage(plot,ChatColor.BOLD + "There is too much physics happening in your plot!", ChatType.WARNING);
					return;
				}
				else{
				storage.get(plot.getId()).storage.add(new LiteLocation(loc));
				}
			}
		}
	}
}
