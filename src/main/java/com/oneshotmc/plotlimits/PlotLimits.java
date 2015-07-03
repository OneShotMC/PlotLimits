package com.oneshotmc.plotlimits;

import org.bukkit.plugin.java.JavaPlugin;

import com.oneshotmc.plotlimits.files.FileLoader;
import com.oneshotmc.plotlimits.listeners.ListenerList;

public class PlotLimits extends JavaPlugin{

	public FileLoader files = new FileLoader(this);
	
	@Override
	public void onEnable(){
		System.out.println("Plot limits has been enabled because it is op.");
		files.setup();
		ListenerList ll = new ListenerList(this);
		ll.setupListners();
	}
	
	@Override
	public void onDisable(){
		files.saveWork();
	}

	public FileLoader getFiles() {
		return files;
	}

	public void setFiles(FileLoader files) {
		if(files==null){
			System.out.println("HOW IN THE WORLD IS FILES NULL? :P");
		}
		this.files = files;
	}
	
}
