package com.mtihc.minecraft.worldguardflagger;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.StateFlag;

public class WorldGuardFlagger extends JavaPlugin {

	private ConfigYaml config;
	private Messages messages;
	private WorldGuardPlugin worldGuard;
	private FlaggerCommand command;

	/**
	 * Initializes the required WorldGuard plugin.
	 * @return Whether WorldGuard was found
	 */
	private boolean setupWorldGuard() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
	
		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			worldGuard = null;
			return false;
		} else {
			worldGuard = (WorldGuardPlugin) plugin;
			return true;
		}
	}

	/**
	 * The WorldGuard plugin
	 * @return The WorldGuardPlugin object
	 */
	public WorldGuardPlugin getWorldGuard() {
		return worldGuard;
	}

	/**
	 * Returns the object that handles the config.yml file
	 * @return the config yaml object
	 */
	public ConfigYaml getConfigYaml() {
		return config;
	}

	/**
	 * Object that handles messages
	 * @return The Messages object
	 */
	public Messages getMessages() {
		return messages;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
	 */
	@Override
	public void onDisable() {
		// disabled
		getLogger().info("disabled.");
	}

	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
	 */
	@Override
	public void onEnable() {
		
		if(!setupWorldGuard()) {
			getLogger().severe("Plugin requires WorldGuard. WorldGuard was not found.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		// create config handler
		config = new ConfigYaml(this);
		config.reload();
		// create messages handler
		messages = new Messages();
		
		// create command handler
		PluginCommand cmd = getCommand("flagger");
		command = new FlaggerCommand(this, cmd.getLabel(), cmd.getAliases());
		
		// enabled
		getLogger().info(getDescription().getVersion() + " enabled.");
	}
	

	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String label, String[] args) {
		if(command.getName().equalsIgnoreCase(label) || command.getAliases().contains(label.toLowerCase())) {
			return command.execute(sender, label, args);
		}
		else {
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#getConfig()
	 */
	@Override
	public FileConfiguration getConfig() {
		return config.getConfig();
	}

	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#reloadConfig()
	 */
	@Override
	public void reloadConfig() {
		config.reload();
	}

	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#saveConfig()
	 */
	@Override
	public void saveConfig() {
		config.save();
	}

	/**
	 * Returns whether a region exists in the specified world.
	 * 
	 * @param world
	 *            The name of the world
	 * @param regionName
	 *            The name of the region
	 * @return Whether a region exists in the specified world. Also returns
	 *         false if the world is null.
	 */
	public boolean hasRegion(World world, String regionName) {
		if (world == null) {
			return false;
		}
		return worldGuard.getRegionManager(world).hasRegion(regionName);
	}

	/**
	 * Returns the value of the specified flag, as defined in the config file.
	 * 
	 * @param sender
	 *            The player that is executing the command.
	 * @param flagpreset
	 *            The set of flags to get the value from.
	 * @param flag
	 *            The flag to get the value of.
	 * @return The value of the specified flag.
	 */
	public Object parseFlagInput(CommandSender sender, String flagpreset,
			Flag<?> flag) {
		// get value of flag in config file
		String valueString = config.getFlag(flagpreset, flag.getName());
		if(valueString == null) {
			// not in the config, return null
			return null;
		}

		// find correct conversion for this flag value,
		// according to type of flag

		//
		// StateFlag: requires StateFlag.State
		//
		if (flag instanceof StateFlag) {

			Object bool = parseBoolean(valueString);
			if(bool == null) {
				// specified a value, other than allow/deny/true/false
				getLogger().severe("Invalid state in config.yml at flag '"
						+ flag.getName() + "' of preset '" + flagpreset
						+ "'. State values: allow/deny");
				messages.flagNotSet(sender, flag.getName());
				return null;
			}
			else if((Boolean)bool) {
				return StateFlag.State.ALLOW;
			}
			else {
				return StateFlag.State.DENY;
			}
		}
		//
		// BooleanFlag: requires boolean
		//
		else if (flag instanceof BooleanFlag) {
			Object bool = parseBoolean(valueString);
			if(bool == null) {
				// could not convert string to true/false
				getLogger().severe("Invalid boolean format in config.yml at flag '"
						+ flag.getName()
						+ "' of preset '"
						+ flagpreset
						+ "'. Boolean values: true/false.");
				messages.flagNotSet(sender, flag.getName());
				return null;
			}
			else if((Boolean)bool) {
				return true;
			}
			else {
				return false;
			}
			
		}
		//
		// All other flags will be parsed normally, by WorldGuard
		//
		else {
			try {
				return flag.parseInput(worldGuard, sender, valueString);
			} catch (InvalidFlagFormat e) {
				getLogger().severe("Invalid flag format in config.yml at flag '"
						+ flag.getName()
						+ "' of preset '"
						+ flagpreset
						+ "'. [WorldGuard] " + e.getMessage());
				return null;
			}
		}
	}
	
	/**
	 * This method is used for BooleanFlags and StateFlags.
	 * Since they are both, basically booleans.
	 * 
	 * <p>This method returns <code>true</code> if the string equals (ignoring case) any of these strings:</br>
	 * <code>"allow", "true", "yes", "on"</code></p>
	 * 
	 * <p>This method returns <code>false</code> if the string equals (ignoring case) any of the strings:</br>
	 * <code>"deny", "false", "no", "off"</code></p>
	 * 
	 * <p>Otherwise this method returns <code>null</code>.</p>
	 * 
	 * @param value The string to convert to boolean
	 * @return The converted string. <code>true</code>, <code>false</code> or <code>null</code>
	 */
	private Object parseBoolean(String value) {
		if (value.equalsIgnoreCase("allow")
				|| value.equalsIgnoreCase("true") 
				|| value.equalsIgnoreCase("yes") 
				|| value.equalsIgnoreCase("on")) {
			return true;
		} else if (value.equalsIgnoreCase("deny")
				|| value.equalsIgnoreCase("false") 
				|| value.equalsIgnoreCase("no") 
				|| value.equalsIgnoreCase("off")) {
			return false;
		}
		else {
			return null;
		}
	}
}
