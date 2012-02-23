package com.mtihc.minecraft.worldguardflagger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Messages {

	public static final ChatColor CHAT_COLOR_ERROR = ChatColor.RED;
	public static final ChatColor CHAT_COLOR_MESSAGE = ChatColor.YELLOW;
	public static final ChatColor CHAT_COLOR_A = ChatColor.DARK_AQUA;
	public static final ChatColor CHAT_COLOR_B = ChatColor.GRAY;
	public static final ChatColor CHAT_COLOR_DARK = ChatColor.DARK_GRAY;
	
	
	/**
	 * Constructor.
	 */
	public Messages() {
		
	}
	
	// --------------------------------------------------------------
	// Chat messages
	// --------------------------------------------------------------

	/**
	 * Incorrect number of arguments
	 * @param sender Command sender
	 * @param expected expected arguments
	 */
	public void incorrectNumberOfArguments(CommandSender sender, String expected) {
		String message = CHAT_COLOR_ERROR
				+ "Incorrect number of arguments. Expected " + expected + ".";
		sender.sendMessage(message);
	}

	/**
	 * Region does not exist
	 * @param sender Command sender
	 * @param regionName Region that doesn't exist
	 * @param worldName The world that was checked
	 */
	public void regionDoesNotExist(CommandSender sender, String regionName,
			String worldName) {
		String message = CHAT_COLOR_ERROR + "Region '" + regionName
				+ "' does not exist in world '" + worldName + "'.";
		sender.sendMessage(message);
	}

	/**
	 * Flag preset does not exist
	 * @param sender Command sender
	 * @param presetName The preset that doesn't exist
	 */
	public void flagPresetDoesNotExist(CommandSender sender, String presetName) {
		String message = CHAT_COLOR_ERROR + "Flag preset '" + presetName
				+ "' does not exist.";
		sender.sendMessage(message);

	}

	/**
	 * No permission
	 * @param sender Command sender
	 */
	public void noPermission(CommandSender sender) {
		String message = CHAT_COLOR_ERROR + "You don't have permission.";
		sender.sendMessage(message);
	}

	/**
	 * Flags applied to region
	 * @param sender Command sender
	 * @param presetName The preset
	 * @param regionName The region
	 */
	public void flagsAppliedToRegion(CommandSender sender, String presetName,
			String regionName) {
		String message = CHAT_COLOR_MESSAGE + "Flag preset '" + presetName
				+ "' applied to region '" + regionName + "'.";
		sender.sendMessage(message);

	}

	/**
	 * Some flag could not be set
	 * @param sender Command sender
	 * @param flagName Flag that wasn't set
	 */
	public void flagNotSet(CommandSender sender, String flagName) {
		String message = CHAT_COLOR_ERROR + "Could not set flag '" + flagName
				+ "'.";
		sender.sendMessage(message);

	}

	/**
	 * Flags cleared of region
	 * @param sender Command sender
	 * @param regionName The region
	 */
	public void flagsClearedOfRegion(CommandSender sender, String regionName) {
		String message = CHAT_COLOR_MESSAGE + "All flags cleared of region '"
				+ regionName + "'.";
		sender.sendMessage(message);

	}

	/**
	 * Page number does not exist
	 * @param sender Command sender
	 * @param page Page number
	 */
	public void pageDoesNotExist(CommandSender sender, int page) {
		String message = CHAT_COLOR_ERROR + "Page " + page + " does not exist.";
		sender.sendMessage(message);

	}

	/**
	 * World does not exist
	 * @param sender Command sender
	 * @param worldName World that doesn't exist
	 */
	public void worldDoesNotExist(CommandSender sender, String worldName) {
		String message = CHAT_COLOR_ERROR + "World '" + worldName
				+ "' does not exist.";
		sender.sendMessage(message);
	}

	/**
	 * Configuration reloaded
	 * @param sender Command sender
	 */
	public void reloadedConfiguration(CommandSender sender) {
		String message = CHAT_COLOR_MESSAGE + "WorldGuardFlagger configuration reloaded.";
		sender.sendMessage(message);
	}

	/**
	 * Command is only for players
	 * @param sender Command sender
	 */
	public void commandOnlyForPlayers(CommandSender sender) {
		String message = "This command is only for in-game players.";
		sender.sendMessage(message);
	}
}
