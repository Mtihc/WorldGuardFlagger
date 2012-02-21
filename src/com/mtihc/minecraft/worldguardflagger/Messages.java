package com.mtihc.minecraft.worldguardflagger;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.minecraft.util.commands.CommandException;

public class Messages {

	public static final ChatColor CHAT_COLOR_ERROR = ChatColor.RED;
	public static final ChatColor CHAT_COLOR_MESSAGE = ChatColor.YELLOW;
	public static final ChatColor CHAT_COLOR_A = ChatColor.DARK_AQUA;
	public static final ChatColor CHAT_COLOR_B = ChatColor.GRAY;
	public static final ChatColor CHAT_COLOR_DARK = ChatColor.DARK_GRAY;
	
	private Logger logger;

	
	
	/**
	 * Constructor.
	 */
	public Messages(JavaPlugin plugin) {
		logger = plugin.getLogger();
	}
	
	// --------------------------------------------------------------
	// Console messages
	// --------------------------------------------------------------

	/**
	 * Convenience method for prefixing every log with the plugin name.
	 * 
	 * @param msg
	 *            The message to output to console.
	 */
	public void log(String msg) {
		logger.info(msg);
	}

	/**
	 * Convenience method for prefixing every severe fault with the plugin name.
	 * 
	 * @param msg
	 *            The message to output to console.
	 */
	public void severe(String msg) {
		logger.severe(msg);
	}

	/**
	 * Convenience method for prefixing every warning with the plugin name.
	 * 
	 * @param msg
	 *            The message to output to console
	 */
	public void warning(String msg) {
		logger.warning(msg);
	}

	/**
	 * Convenience method for prefixing every debug output with the plugin name.
	 * 
	 * @param msg
	 *            The message to output to console
	 */
	public void debug(Object msg) {
		logger.info(" [DEBUG]: " + msg.toString());
	}

	// --------------------------------------------------------------
	// Chat messages
	// --------------------------------------------------------------

	/**
	 * 
	 * @param sender
	 * @param expected
	 */
	public void incorrectNumberOfArguments(CommandSender sender, String expected) {
		String message = CHAT_COLOR_ERROR
				+ "Incorrect number of arguments. Expected " + expected + ".";
		sender.sendMessage(message);
	}

	/**
	 * 
	 * @param sender
	 * @param regionName
	 * @param worldName
	 */
	public void regionDoesNotExist(CommandSender sender, String regionName,
			String worldName) {
		String message = CHAT_COLOR_ERROR + "Region '" + regionName
				+ "' does not exist in world '" + worldName + "'.";
		sender.sendMessage(message);
	}

	/**
	 * 
	 * @param sender
	 * @param presetName
	 */
	public void flagPresetDoesNotExist(CommandSender sender, String presetName) {
		String message = CHAT_COLOR_ERROR + "Flag preset '" + presetName
				+ "' does not exist.";
		sender.sendMessage(message);

	}

	/**
	 * 
	 * @param sender
	 */
	public void noPermission(CommandSender sender) {
		String message = CHAT_COLOR_ERROR + "You don't have permission.";
		sender.sendMessage(message);
	}

	/**
	 * 
	 * @param sender
	 * @param presetName
	 * @param regionName
	 */
	public void flagsAppliedToRegion(CommandSender sender, String presetName,
			String regionName) {
		String message = CHAT_COLOR_MESSAGE + "Flag preset '" + presetName
				+ "' applied to region '" + regionName + "'.";
		sender.sendMessage(message);

	}

	/**
	 * 
	 * @param sender
	 * @param flagName
	 */
	public void flagNotSet(CommandSender sender, String flagName) {
		String message = CHAT_COLOR_ERROR + "Could not set flag '" + flagName
				+ "'.";
		sender.sendMessage(message);

	}

	/**
	 * 
	 * @param sender
	 * @param regionName
	 */
	public void flagsClearedOfRegion(CommandSender sender, String regionName) {
		String message = CHAT_COLOR_MESSAGE + "All flags cleared of region '"
				+ regionName + "'.";
		sender.sendMessage(message);

	}

	/**
	 * 
	 * @param sender
	 * @param page
	 */
	public void pageDoesNotExist(CommandSender sender, int page) {
		String message = CHAT_COLOR_ERROR + "Page " + page + " does not exist.";
		sender.sendMessage(message);

	}

	/**
	 * 
	 * @param sender
	 * @param worldName
	 */
	public void worldDoesNotExist(CommandSender sender, String worldName) {
		String message = CHAT_COLOR_ERROR + "World '" + worldName
				+ "' does not exist.";
		sender.sendMessage(message);
	}

	/**
	 * 
	 * @param sender
	 */
	public void reloadedConfiguration(CommandSender sender) {
		String message = CHAT_COLOR_MESSAGE + "WorldGuardFlagger configuration reloaded.";
		sender.sendMessage(message);
	}

	public void commandOnlyForPlayers(CommandSender sender) {
		String message = "This command is only for in-game players.";
		sender.sendMessage(message);
	}

	public void regionAlreadyExists(CommandSender sender, String regionName,
			String worldName) {
		String message = CHAT_COLOR_ERROR + "Region '" + regionName + 
		"' already exists in world '" + worldName + "'.";
		sender.sendMessage(message);
	}

	public void noSelection(CommandSender sender) {
		String message = CHAT_COLOR_ERROR + "Select a region first, with WorldEdit's command //wand";
		sender.sendMessage(message);
	}

	public void noWorldEdit(CommandSender sender, CommandException e) {
		String message = CHAT_COLOR_ERROR + e.getMessage();
		sender.sendMessage(message);
		
	}

	public void couldNotExpandVert(CommandSender sender) {
		String message = CHAT_COLOR_ERROR + "Could not expand region from top-to-bottom.";
		sender.sendMessage(message);
		
	}

}
