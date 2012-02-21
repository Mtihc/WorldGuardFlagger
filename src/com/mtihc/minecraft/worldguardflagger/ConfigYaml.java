package com.mtihc.minecraft.worldguardflagger;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.mtihc.minecraft.core1.YamlFile;

public class ConfigYaml extends YamlFile {

	private WorldGuardFlagger plugin;
	
	public ConfigYaml(WorldGuardFlagger plugin) {
		super(plugin, "config");
		this.plugin = plugin;
		//this will create a default preset if there are none in the config
		presets();
	}

	private ConfigurationSection presets() {
		ConfigurationSection result = getConfig().getConfigurationSection("flagpresets");
		if(result == null) {
			// flagpresets does not exist
			// create default preset
			YamlConfiguration config = getConfig();
			config.options().copyDefaults(true);
			InputStream defConfigStream = plugin.getResource("supersafe.yml");
			if (defConfigStream != null) {
				YamlConfiguration defConfig = YamlConfiguration
					.loadConfiguration(defConfigStream);
				config.setDefaults(defConfig);
				save();
			}
		}
		return result;
	}
	
	public boolean hasPreset(String name) {
		return presets().contains(name);
	}
	
	public String getFlag(String preset, String flag) {
		return presets().getString(preset + "." + flag);
	}


	public Map<String, Object> getPreset(String presetName) {
		return presets().getConfigurationSection(presetName).getValues(false);
	}
	
	public Set<String> getPresets() {
		return presets().getKeys(false);
	}
}
