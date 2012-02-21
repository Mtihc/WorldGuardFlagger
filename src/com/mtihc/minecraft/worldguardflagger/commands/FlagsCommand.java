package com.mtihc.minecraft.worldguardflagger.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.core1.BukkitCommand;
import com.mtihc.minecraft.worldguardflagger.Messages;
import com.mtihc.minecraft.worldguardflagger.Permission;
import com.mtihc.minecraft.worldguardflagger.WorldGuardFlagger;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.CreatureTypeFlag;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.RegionGroupFlag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.VectorFlag;

public class FlagsCommand extends BukkitCommand {

	private WorldGuardFlagger plugin;

	public FlagsCommand(WorldGuardFlagger plugin, String name, List<String> aliases) {
		super(name, "Show info about all of WorldGuard's flags, or all flags in a preset", "[page] | <flagpreset> [page]", aliases);
		this.plugin = plugin;
		ArrayList<String> desc = new ArrayList<String>();
		desc.add(ChatColor.GRAY + "This command can be used in 2 ways.");
		desc.add("");
		desc.add("  To show info about all flags in a preset:");
		desc.add("/flagger " + name + " <flagpreset> [page]");
		desc.add("");
		desc.add("  To show info about all flags in WorldGuard,");
		desc.add("/flagger " + name + " [page]");
		desc.add("");
		setLongDescription(desc);
	}

	/* (non-Javadoc)
	 * @see com.mtihc.minecraft.core1.BukkitCommand#execute(org.bukkit.command.CommandSender, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(super.execute(sender, label, args)) {
			return true;
		}
		else if(!sender.hasPermission(getPermission())) {
			sender.sendMessage(ChatColor.RED + "You don't have permission for that command.");
			return false;
		}
		
		String presetName = null;// optional
		int page;// defaults to 1

		// check number of arguments
		if (args.length >= 0 && args.length <= 2) {

			try {
				page = Integer.parseInt(args[0]);
			} catch (IndexOutOfBoundsException e) {
				page = 1;
				presetName = null;
			} catch (NumberFormatException e) {
				presetName = args[0];
				try {
					page = Integer.parseInt(args[1]);
				} catch (Exception exception) {
					page = 1;
				}
			}
		} else {
			// incorrect number of arguments
			plugin.getMessages().incorrectNumberOfArguments(sender,
					"none, or a preset name and optionally a page number.");
			sender.sendMessage(getUsage());
			return false;
		}

		// check preset existance
		if (presetName != null && !plugin.getConfigYaml().hasPreset(presetName)) {
			plugin.getMessages().flagPresetDoesNotExist(sender, presetName);
			return false;
		}
		//
		// we made it through all checks
		// lets execute the command
		//
		int itemsPerPage = 10;
		int totalItems;
		int totalPages;

		// if the player specified a preset name
		// we show a list of flags in that preset
		if (presetName != null) {
			Map<String, Object> preset = plugin.getConfigYaml().getPreset(presetName);
			if (preset == null) {
				plugin.getMessages().flagPresetDoesNotExist(sender, presetName);
				return false;
			}

			Set<String> keys = preset.keySet();

			totalItems = keys.size();
			totalPages = totalItems / itemsPerPage + 1;
			if (page < 1) {
				page = 1;
			} else if (page > totalPages) {
				plugin.getMessages().pageDoesNotExist(sender, page);
				return false;
			}

			sender.sendMessage(Messages.CHAT_COLOR_MESSAGE + "Flags of preset '"
					+ presetName + "' (page " + page + "/" + totalPages + "):");

			if (totalItems == 0) {
				sender.sendMessage(Messages.CHAT_COLOR_A + "(none)");
			} else {
				int startIndex = (page - 1) * itemsPerPage;
				int endIndex = startIndex + itemsPerPage;
				Object[] keysArray = keys.toArray();
				for (int i = startIndex; i < endIndex && i < totalItems; i++) {
					String key = keysArray[i].toString();
					Object value = preset.get(key);
					sender.sendMessage(Messages.CHAT_COLOR_DARK + "" + (i + 1) + ". " + Messages.CHAT_COLOR_A + key
							+ Messages.CHAT_COLOR_DARK + ": " + Messages.CHAT_COLOR_B + value.toString());
				}
			}
		}
		// if the player did not specify a preset name
		// we show all flags available in WorldGuard
		else {

			totalItems = DefaultFlag.flagsList.length;
			totalPages = totalItems / itemsPerPage + 1;
			if (page < 1) {
				page = 1;
			} else if (page > totalPages) {
				plugin.getMessages().pageDoesNotExist(sender, page);
				return false;
			}
			sender.sendMessage(Messages.CHAT_COLOR_MESSAGE + "WorldGuard flag list (page "
					+ page + "/" + totalPages + "):");
			if (totalItems == 0) {
				sender.sendMessage(Messages.CHAT_COLOR_A + "(none)");
			} else {
				int startIndex = (page - 1) * itemsPerPage;
				int endIndex = startIndex + itemsPerPage;
				for (int i = startIndex; i < endIndex && i < totalItems; i++) {
					Flag<?> flag = DefaultFlag.flagsList[i];

					sender.sendMessage(Messages.CHAT_COLOR_DARK + "" + (i + 1) + ". "
							+ Messages.CHAT_COLOR_A + flag.getName()
							+ Messages.CHAT_COLOR_DARK + ": " + Messages.CHAT_COLOR_B
							+ getFlagDescription(flag));
				}
			}
		}
		return true;
		
	}

	/* (non-Javadoc)
	 * @see org.bukkit.command.Command#getPermission()
	 */
	@Override
	public String getPermission() {
		return Permission.FLAGS.getNode();
	}
	

	/**
	 * Returns a description of the specified flag, like possible values.
	 * 
	 * @return A description of the specified flag
	 */
	public String getFlagDescription(Flag<?> flag) {
		//
		// StateFlag: requires StateFlag.State
		//
		if (flag instanceof StateFlag) {
			return "allow/deny";
		}
		//
		// CreatureTypeFlag: requires CreatureType
		//
		// Oddly there is no CreatureTypeFlag in DefaultFlag.flagsList
		// So I added a logical OR here for the exceptional flags, so the code
		// is still clear
		else if (flag instanceof CreatureTypeFlag || flag instanceof SetFlag
				&& flag.getName() == "deny-spawn") {
			return "comma seperated creature names";
		}
		//
		// StringFlag: requires string
		//
		else if (flag instanceof StringFlag) {
			return "tekst";
		}
		//
		// BooleanFlag: requires boolean
		//
		else if (flag instanceof BooleanFlag) {
			return "true/false";
		}
		//
		// IntegerFlag: requires int
		//
		else if (flag instanceof IntegerFlag) {
			return "number";
		}
		//
		// SetFlag: requires Set (HashSet)
		//
		else if (flag instanceof SetFlag) {
			return "comma seperated list";
		}
		//
		// VectorFlag: requires Vector
		//
		else if (flag instanceof VectorFlag) {
			return "comma seperated coordinates";
		}
		//
		// RegionGroupFlag: requires RegionGroup
		//
		else if (flag instanceof RegionGroupFlag) {
			return "members/owners/nonmembers/nonowners";
		}
		return "unknown";
	}
}
