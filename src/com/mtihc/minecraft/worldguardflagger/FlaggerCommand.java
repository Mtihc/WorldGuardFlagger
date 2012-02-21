package com.mtihc.minecraft.worldguardflagger;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.core1.BukkitCommand;
import com.mtihc.minecraft.worldguardflagger.commands.ClearCommand;
import com.mtihc.minecraft.worldguardflagger.commands.FlagsCommand;
import com.mtihc.minecraft.worldguardflagger.commands.PresetsCommand;
import com.mtihc.minecraft.worldguardflagger.commands.SetCommand;

public class FlaggerCommand extends BukkitCommand {

	private JavaPlugin plugin;


	public FlaggerCommand(WorldGuardFlagger plugin, String name, List<String> aliases) {
		super(name, "Reload config. To get help, type /" + name + " help", "", aliases);
		
		this.plugin = plugin;
		Server server = plugin.getServer();
		
		ArrayList<String> longDescription = new ArrayList<String>();
		
		longDescription.add("Reload the configuration file");
		longDescription.add(ChatColor.GREEN + "Nested commands:");
		
		BukkitCommand set = new SetCommand(plugin, "set", null);
		addNested(set, server);
		longDescription.add(set.getUsage());
		
		BukkitCommand clear = new ClearCommand(plugin, "clear", null);
		addNested(clear, server);
		longDescription.add(clear.getUsage());
		BukkitCommand flags = new FlagsCommand(plugin, "flags", null);
		addNested(flags, server);
		longDescription.add(flags.getUsage());
		
		BukkitCommand presets = new PresetsCommand(plugin, "presets", null);
		addNested(presets, server);
		longDescription.add(presets.getUsage());
		
		longDescription.add("/" + name + " <command> help");
		
		setLongDescription(longDescription);
		
		
		
		
		
		
		
	}
	

	/* (non-Javadoc)
	 * @see com.mtihc.minecraft.core1.BukkitCommand#execute(org.bukkit.command.CommandSender, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		// try to execute a nested command
		if(super.execute(sender, label, args)) {
			return true;
		}
		
		// no nested command
		if(args.length != 0) {
			// but tried it
			String cmd = args[0];
			sender.sendMessage(ChatColor.RED + "Unknown command '/" + label + " " + cmd + "'");
			sender.sendMessage(ChatColor.RED + "To get command help, type: " + ChatColor.WHITE + "/" + label + " " + "help");
			return true;
		}

		// no nested command and no arguments,
		// means reload command
		if(!sender.hasPermission(Permission.RELOAD.getNode())) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to reload the configuration.");
			sender.sendMessage(ChatColor.RED + "To get command help, type: " + ChatColor.WHITE + "/" + label + " " + "help");
			return true;
		}
		
		// reload
		plugin.reloadConfig();
		// send message
		sender.sendMessage(ChatColor.GREEN + plugin.getDescription().getName() + " configuration file reloaded.");
		
		return true;
	}


}
