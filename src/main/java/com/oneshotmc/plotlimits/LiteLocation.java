package com.oneshotmc.plotlimits;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LiteLocation {
	String worldname;
	int x;
	int z;
	
	public LiteLocation(String worldname , int x, int z){
		this.x=x;
		this.z=z;
	}
	public LiteLocation(World world, int x, int y, int z){
		this(world.getName(), x , z);
	}
	public LiteLocation(Location loc){
		this(loc.getWorld().getName(),loc.getBlockX(),loc.getBlockZ());
	}
	public Location getLocation(int y){
		return new Location(Bukkit.getWorld(worldname),x,y,z);
	}
	public String getWorldname() {
		return worldname;
	}
	public void setWorldname(String worldname) {
		this.worldname = worldname;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getZ() {
		return z;
	}
	public void setZ(int z) {
		this.z = z;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof LiteLocation))return false;
		LiteLocation loc = (LiteLocation) o;
		if(this.x==loc.getX()&&this.worldname.equals(loc.getWorldname())&&this.z==loc.getZ())return true;
		return false;
	}
}
