package com.oneshotmc.plotlimits.listeners;

import com.oneshotmc.plotlimits.PlotLimits;

public class ListenerList {
	PlotLimits plugin;
	public ListenerList(PlotLimits plugin){
		this.plugin=plugin;
	}
	public void setupListners(){
		new EntitySpawn(plugin);
		new RedstoneUse(plugin);
		PhysicsHappen ph = new PhysicsHappen(plugin);
		ph.setupConfig();
	}
}
