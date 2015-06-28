package com.oneshotmc.plotlimits;

import org.bukkit.plugin.Plugin;

public final class Settings {
	private Plugin plugin;
	public static boolean debug = true;
	public Settings(Plugin plugin){
		this.plugin=plugin;
	}
	public void sendDebugMessage(String str){
		if(debug)
		plugin.getLogger().info(str);
	}
}
