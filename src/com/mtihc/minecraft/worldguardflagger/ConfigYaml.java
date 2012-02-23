package com.mtihc.minecraft.worldguardflagger;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.mtihc.minecraft.core1.YamlFile;

/**
 * The instance of this class represents the config.yml file.
 * 
 * @author Mitch
 *
 */
public class ConfigYaml extends YamlFile {

	private WorldGuardFlagger plugin;
	
	/**
	 * Constructor
	 * 
	 * @param plugin WorldGuardFlagger plugin
	 */
	public ConfigYaml(WorldGuardFlagger plugin) {
		super(plugin, "config");
		this.plugin = plugin;
		//this will create a default preset if there are none in the config
		presets();
	}

	/**
	 * Returns the presets configuration section.
	 * 
	 * <p>Creates default preset if the configuration section doesn't exist</p>
	 * 
	 * @return The preset configuration section
	 */
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
	
	/**
	 * Returns whether the specified preset exists in the config
	 * 
	 * @param name Name of the preset
	 * @return true if the preset exists, false otherwise
	 */
	public boolean hasPreset(String name) {
		return presets().contains(name);
	}
	
	/**
	 * Returns the value of a flag in a preset, as String.
	 * @param preset The preset
	 * @param flag The flag
	 * @return the value of the flag as String, or null if the flag doesn't exist in the preset
	 */
	public String getFlag(String preset, String flag) {
		return presets().getString(preset + "." + flag, null);
	}


	/**
	 * Returns an entire preset, as map.
	 * 
	 * @param presetName The preset to return
	 * @return The preset as map
	 */
	public Map<String, Object> getPreset(String presetName) {
		return presets().getConfigurationSection(presetName).getValues(false);
	}
	
	/**
	 * Returns all preset names
	 * 
	 * @return A set of all preset names
	 */
	public Set<String> getPresets() {
		return presets().getKeys(false);
	}
}
