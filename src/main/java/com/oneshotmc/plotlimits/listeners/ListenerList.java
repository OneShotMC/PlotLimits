package com.oneshotmc.plotlimits.listeners;

import com.oneshotmc.plotlimits.PlotLimits;

public class ListenerList {
	PlotLimits plugin;
	public ListenerList(PlotLimits plugin){
		this.plugin=plugin;
	}
	public void setupListners(){
		EntitySpawn ES = new EntitySpawn(plugin);
		ES.setup();
		new RedstoneUse(plugin);
	}
}