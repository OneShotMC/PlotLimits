package com.oneshotmc.plotlimits.storage;

import java.util.ArrayList;

import com.oneshotmc.plotlimits.CubicMultiplicity;

public class CubicMultiplicityGrouper{
	private boolean mainMessageSent=false;
	private boolean warnMessageSent=false;
	public boolean isWarnMessageSent() {
		return warnMessageSent;
	}
	public void setWarnMessageSent(boolean messageSent) {
		this.warnMessageSent = messageSent;
	}
	public boolean isMainMessageSent() {
		return mainMessageSent;
	}
	public void setMainMessageSent(boolean mainMessageSent) {
		this.mainMessageSent = mainMessageSent;
	}
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