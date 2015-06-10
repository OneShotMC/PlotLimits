package com.oneshotmc.plotlimits.files;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class FileLoader {
	Plugin plugin;
	YamlConfiguration configYML;
	File cFile;
	/**
	 * 
	 * @param plugin
	 */
	public FileLoader(Plugin plugin){
		this.plugin=plugin;
	}
	/**Sets up the config files
	 * 
	 */
	public void setup(){
		reloadConfigYML();
		
	}
	
	/**Saves YAML files
	 * 
	 */
	public void saveWork(){
		saveConfig(configYML,cFile);
	}
	
	public void reloadConfigYML(){
		cFile = new File(plugin.getDataFolder(),"config.yml");
		configYML = makeFile(cFile);
		YamlConfiguration defaults = getDefaults("config.yml");
		if(defaults!=null && configYML !=null){
		configYML.options().copyDefaults(true);
		configYML.setDefaults(defaults);
		}
		else{
			plugin.getLogger().warning("Error reloaing configYML");
		}
	}
	public FileConfiguration getConfigYML(){
		if(configYML == null){
			reloadConfigYML();
		}
		return configYML;
	}
	
	public void saveConfig(YamlConfiguration yamlconfig, File saver){
		if(yamlconfig==null || saver== null){
			plugin.getLogger().warning("Error saving "+ saver.getName());
			return;
		}
			try {
				yamlconfig.save(saver);
			} catch (IOException e) {
				plugin.getLogger().warning("Error saving "+ saver.getName());
			}
		
	}
	public YamlConfiguration makeFile(File file){
		if(!(file.exists())){
				if(file.getParentFile().mkdirs()){
					plugin.getLogger().info("Successfuly created" + file.getPath() + ".");
				}
				return YamlConfiguration.loadConfiguration(file);
		}
		else{
			plugin.getLogger().info("Loaded " + file.getPath()+".");
			return YamlConfiguration.loadConfiguration(file);
		}
	}
	
	public YamlConfiguration getDefaults(String filename){
		Reader ymlConfigStream=null;
		try {
			ymlConfigStream = new InputStreamReader(plugin.getResource(filename),"UTF8");
		} catch (UnsupportedEncodingException e) {
			plugin.getLogger().warning("Error loading "+filename + " defaults! Report this to +"
					+ "EmeraldExplorer on Spigot immedietly! Error:"+e.getMessage());
		}
		if(ymlConfigStream!=null){
			YamlConfiguration ymlConfig = YamlConfiguration.loadConfiguration(ymlConfigStream);
			plugin.getLogger().info("Successfully loaded "+filename + " defaults.");
			System.out.println("They are:");
			return ymlConfig;
		}
		plugin.getLogger().warning("couldnt load " + filename);
		return null;
	}
	/*
	 * Setters and getters!
	 */
	public File getcFile() {
		return cFile;
	}
	
	public static ConfigurationSection getWorldPerms(YamlConfiguration c, World world){
		return c.getConfigurationSection(world.getName().toString());
	}
}
