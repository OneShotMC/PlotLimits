package com.oneshotmc.plotlimits.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;

public class ChatUtil {
	public static PlotAPI Plotapi = new PlotAPI();
	public ChatUtil(){}
	
	public static void sendMessage(Plot plot,String message,ChatType type){
	for(Player player : Bukkit.getOnlinePlayers()){
		if(Plotapi.getPlot(player.getLocation()).equals(plot)){
			player.sendMessage(type.getColor()+message);
		}
	}
	}
}
