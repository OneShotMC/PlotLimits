package com.oneshotmc.plotlimits;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class CubicMultiplicity extends Cubic {
	int x;
	int y;
	int z;
	int multiplicity=0;
	public CubicMultiplicity(Block block) {
		super(block);
	}
	public CubicMultiplicity(Block block, int currentMultiplicity) {
		this(block);
		this.multiplicity=currentMultiplicity;
	}
	public CubicMultiplicity(Location location){
		super(location);
	}
	public CubicMultiplicity(Location location, int currentMultiplicity) {
		this(location);
		this.multiplicity=currentMultiplicity;
	}
	public CubicMultiplicity(int x, int y ,int z){
		super(x, y, z);
	}
	public CubicMultiplicity(int x, int y, int z, int currentMultiplicity) {
		this(x,y,z);
		this.multiplicity=currentMultiplicity;
	}
	public int getMutiplicity(){
		return multiplicity;
	}
	public void addOneMultiplicity(){
		this.multiplicity++;
	}
	public void setMultiplicty(int num){
		this.multiplicity=num;
	}
	public void addMultiplicity(int num){
		this.multiplicity=this.multiplicity+=num;
	}
	
	public void clearMultiplicity(){
		this.multiplicity=0;
	}
	
	public void subtractMultiplicity(int num){
		this.multiplicity=this.multiplicity-=num;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof Cubic))return false;
		Cubic c = (Cubic)o;
		if(c.getX()==this.x&&c.getY()==this.y&&c.getZ()==this.z)return true;
		return false;
	}
	
}
