package com.oneshotmc.plotlimits.storage;

import java.util.ArrayList;

import com.oneshotmc.plotlimits.CubicMultiplicity;

public class CubicMultiplicityGrouper{
	private boolean hasGrown=false;
	private ArrayList<CubicMultiplicity> list = new ArrayList<CubicMultiplicity>();
	public CubicMultiplicityGrouper(){
		
	}
	public ArrayList<CubicMultiplicity> getList(boolean hasAdded){
		this.hasGrown=hasAdded;
		return list;
	}
	/**
	 * @deprecated
	 * @return list
	 */
	public ArrayList<CubicMultiplicity> getList(){
		return list;
	}
	public boolean getHasGrown(){
		return hasGrown;
	}
	public void setHasGrown(boolean hasGrown){
		this.hasGrown=hasGrown;
	}
}