package com.oneshotmc.plotlimits.util;

import net.md_5.bungee.api.ChatColor;


public enum ChatType {
	WARNING(4),
	INFORMATION(3),
	SUCCESS(2),
	ERROR(1);

public int id;

ChatType(int id){
	this.id=id;
}
public String getColor(){
	switch(this){
	case INFORMATION:
		return ""+ChatColor.BLUE;
	case WARNING:
		return ""+ChatColor.YELLOW;
	case SUCCESS:
		return ""+ChatColor.GREEN;
	case ERROR:
		return ""+ChatColor.RED;
	default:
		return null;
	}
}
public int getId(){
	return id;
}
}