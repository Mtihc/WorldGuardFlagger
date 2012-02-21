package com.mtihc.minecraft.worldguardflagger.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.core1.BukkitCommand;
import com.mtihc.minecraft.worldguardflagger.Messages;
import com.mtihc.minecraft.worldguardflagger.Permission;
import com.mtihc.minecraft.worldguardflagger.WorldGuardFlagger;

public class PresetsCommand extends BukkitCommand {

	private WorldGuardFlagger plugin;

	public PresetsCommand(WorldGuardFlagger plugin, String name, List<String> aliases) {
		super(name, "List all presets that you're allowed to use.", "[page]", aliases);
		this.plugin = plugin;
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
		
		

		if (args.length > 1) {
			// incorrect number of arguments
			plugin.getMessages().incorrectNumberOfArguments(sender, "optional page number");
			sender.sendMessage(getUsage());
			return false;
		}

		// try to get the page number from the arguments
		int page;
		try {
			// get the specified page number
			page = Integer.valueOf(args[0]);
		} catch (IndexOutOfBoundsException e) {
			// player did not specify a page number
			// no problem, we show the first page
			page = 1;
		} catch (NumberFormatException e) {
			// player specified something other than a number
			// don't bother to show an error message
			// we show the first page
			page = 1;
		}

		//
		// we made it through all checks
		// lets execute the command
		//
		Set<String> presets = plugin.getConfigYaml().getPresets();
		if (presets != null && !presets.isEmpty()) {

			HashSet<String> permittedPresets = new HashSet<String>();
			
			for (String preset : presets) {
				if(hasPermissionForPreset(sender, preset)) {
					permittedPresets.add(preset);
				}
			}
			
			int itemsPerPage = 10;
			int totalItems = permittedPresets.size();
			int totalPages = totalItems / itemsPerPage + 1;
			if (page < 1) {
				page = 1;
			} else if (page > totalPages) {
				plugin.getMessages().pageDoesNotExist(sender, page);
				return false;
			}

			sender.sendMessage(Messages.CHAT_COLOR_MESSAGE + "Flag presets (page "
					+ page + "/" + totalPages + "):");

			if (permittedPresets.size() > 0) {
				int startIndex = (page - 1) * itemsPerPage;
				int endIndex = startIndex + itemsPerPage;
				Object[] permittedPresetArray = permittedPresets.toArray();
				
				for (int i = startIndex; i < endIndex && i < totalItems; i++) {
					if(permittedPresetArray[i] == null) {
						continue;
					}
					sender.sendMessage(Messages.CHAT_COLOR_DARK + "" + (i + 1) + ". "
							+ Messages.CHAT_COLOR_MESSAGE + permittedPresetArray[i].toString());
				}

				return true;
			} else {
				sender.sendMessage(Messages.CHAT_COLOR_A + "(none)");
				return true;
			}
		} else {
			sender.sendMessage(Messages.CHAT_COLOR_ERROR + "No flag presets found.");
		}
		return true;
	}

	private boolean hasPermissionForPreset(CommandSender sender, String preset) {
		return (hasChildPermission(Permission.SET.getNode(), sender, preset) || hasChildPermission(Permission.SET_MEMBER.getNode(), sender, preset) || hasChildPermission(Permission.SET_OWNER.getNode(), sender, preset));
	}
	
	private boolean hasChildPermission(String node, CommandSender sender, String preset) {
		return sender.hasPermission(node) || sender.hasPermission(node + ".*") || sender.hasPermission(node + "." + preset);
	}

	/* (non-Javadoc)
	 * @see org.bukkit.command.Command#getPermission()
	 */
	@Override
	public String getPermission() {
		return Permission.PRESETS.getNode();
	}
	
	

	
}
