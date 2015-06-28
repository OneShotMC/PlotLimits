package com.oneshotmc.plotlimits.listeners;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;






import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import com.oneshotmc.plotlimits.EntityCount;
import com.oneshotmc.plotlimits.PlotLimits;
import com.oneshotmc.plotlimits.files.FileLoader;
import com.oneshotmc.plotlimits.util.ChatType;
import com.oneshotmc.plotlimits.util.ChatUtil;

@SuppressWarnings("unused")
public class EntitySpawn implements Listener {
	private PlotLimits plugin;
	private PlotAPI api = new PlotAPI();
	private EntityCount ec = new EntityCount();
	FileLoader files;
	YamlConfiguration config;
	public EntitySpawn(PlotLimits plugin){
		this.plugin=plugin;
	}
	public void setup(){
		if(plugin==null){
			System.out.println("WHY IS THE PLUGIN NULL!?!?! :o");
		}
		files = getPlugin().getFiles();
		config = (YamlConfiguration) files.getConfigYML();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	@EventHandler (priority=EventPriority.HIGHEST,ignoreCancelled=true)
	public void onSpawn(CreatureSpawnEvent e){
		System.out.println("EntitySpawned");
		Location loc = e.getLocation();
		World world = loc.getWorld();
		String worldName = world.getName().toString();
		try{
			if(config==null){
				System.out.println("CONFIG NULL!");
			}
			if(FileLoader.getWorldPerms(config, world)==null){
				System.out.println("FILE LOADER NULL!");
			}
		ConfigurationSection worldSettings = FileLoader.getWorldPerms(config, world).getConfigurationSection("entities");
		Plot plotIn = api.getPlot(loc);
		int maxTypeEntites=1000;
		if(worldSettings.contains("maxentities."+e.getEntityType().toString().toUpperCase())){
		maxTypeEntites=worldSettings.getInt("maxentities."+e.getEntityType().toString().toUpperCase());
		}
		int totMaxEntities = worldSettings.getInt("maxentities.total");
		
		int typeEntity = ec.entitiesInPlot(loc, e.getEntityType());
		if(typeEntity>totMaxEntities){
			ChatUtil.sendMessage(plotIn, ChatColor.BOLD+""+"You are spawning too many entites!",ChatType.WARNING);
			e.setCancelled(true);
		}
		int totEntity = ec.entitiesInPlot(loc);
		if(totEntity>totMaxEntities||typeEntity>maxTypeEntites){
			ChatUtil.sendMessage(plotIn, ChatColor.BOLD+""+"You are spawning too many entites!",ChatType.WARNING);
			e.setCancelled(true);
		}
		}
		catch(NullPointerException ex){
			System.out.println("Y Null?");
			//getWorldpermsIsNull?
		}
	}
	public PlotLimits getPlugin(){
		return plugin;
	}
}
