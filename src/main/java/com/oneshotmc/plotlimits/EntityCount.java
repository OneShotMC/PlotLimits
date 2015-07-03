package com.oneshotmc.plotlimits;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;

public class EntityCount {
	public PlotAPI api = new PlotAPI();
	public int entitiesInPlot(Location location, EntityType type){
		int entityCount=0;
		Plot plot = api.getPlot(location);
		if(plot==null){
			return 0;
		}
		World world = location.getWorld();
		for(Entity entity : world.getEntitiesByClass(type.getEntityClass())){
			Location entityLoc=entity.getLocation();
			if(plot.equals(api.getPlot(entityLoc))){
				entityCount++;
			}
		}
		return entityCount;
	}
	public int entitiesInPlot(Location location){
		int entityCount=0;
		Plot plot = api.getPlot(location);
		if(plot==null){
			return 0;
		}
		World world = location.getWorld();
		for(Entity entity : world.getEntities()){
			Location entityLoc=entity.getLocation();
			if(plot.equals(api.getPlot(entityLoc))){
				entityCount++;
			}
		}
		return entityCount;
	}
}
