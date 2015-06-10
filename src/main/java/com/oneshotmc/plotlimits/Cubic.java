package com.oneshotmc.plotlimits;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class Cubic {
	private int x;
	private int y;
	private int z;
	public Cubic(Location location){
		this.x=location.getBlockX();
		this.y=location.getBlockY();
		this.z=location.getBlockZ();
	}
	public Cubic(Block block){
		Location loc = block.getLocation();
		this.x=loc.getBlockX();
		this.y=loc.getBlockY();
		this.z=loc.getBlockZ();
	}
	public Cubic(int x,int y, int z){
		this.x=x;
		this.y=y;
		this.z=z;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getZ() {
		return z;
	}
	public void setZ(int z) {
		this.z = z;
	}
	
}
