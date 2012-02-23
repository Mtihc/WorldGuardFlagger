package com.mtihc.minecraft.worldguardflagger.commands;

import java.util.HashMap;
import java.util.List;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.core1.BukkitCommand;
import com.mtihc.minecraft.worldguardflagger.Permission;
import com.mtihc.minecraft.worldguardflagger.WorldGuardFlagger;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * Command to clear all flags from a region
 * 
 * @author Mitch
 *
 */
public class ClearCommand extends BukkitCommand {

	private WorldGuardFlagger plugin;

	/**
	 * Constructor
	 * @param plugin The WorldGuardFlagger plugin
	 * @param name The command's label
	 * @param aliases The command's aliases
	 */
	public ClearCommand(WorldGuardFlagger plugin, String name, List<String> aliases) {
		super(name, "Clear all flags from a WorldGuard region", "<region> [world]", aliases);
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
		String regionName;
		World world;

		// check amount of arguments when Player executes the command
		if (sender instanceof Player && args.length == 1) {
			regionName = args[0];
			// world is the player's current world
			world = ((Player) sender).getWorld();
		}
		// check amount of arguments when ConsoleCommandSender executes the
		// command
		else if (args.length == 2) {
			regionName = args[0];
			// world is the world specified as an argument
			world = plugin.getServer().getWorld(args[1]);
		} else {
			// incorrect number of arguments
			if (sender instanceof Player) {
				plugin.getMessages().incorrectNumberOfArguments(sender, "region name");
			} else {
				plugin.getMessages().incorrectNumberOfArguments(sender,
						"region name and world");
			}
			sender.sendMessage(getUsage());
			return false;
		}

		ProtectedRegion region = null;

		// things to check before sending messages
		boolean hasRegion = false;
		boolean hasPermission = false;
		boolean hasWorld = false;

		// check world existance
		if (world != null) {
			hasWorld = true;
		}
		// check region existance
		if (hasWorld && plugin.hasRegion(world, regionName)) {
			hasRegion = true;
		}
		if (hasRegion) {
			//
			// region needs to exist, in order to check permissions
			//

			WorldGuardPlugin worldGuard = plugin.getWorldGuard();
			// find region
			region = worldGuard.getRegionManager(world).getRegion(regionName);
			// check permissions
			hasPermission = hasPermission(sender, region);
		}

		//
		// Eventhough we need to find the region, before checking permissions,
		// the permissions-check takes precedence over the
		// region-existance-check
		// when giving feedback to the player
		//

		// no permission
		if (!hasPermission) {
			plugin.getMessages().noPermission(sender);
			return false;
		}
		// no world
		if (!hasWorld) {
			plugin.getMessages().worldDoesNotExist(sender, args[1]);
			return false;
		}
		// no region
		if (!hasRegion) {
			plugin.getMessages().regionDoesNotExist(sender, regionName, world.getName());
			return false;
		}
		//
		// we made it through all checks
		// lets execute the command
		//
		HashMap<Flag<?>, Object> clearedFlags = new HashMap<Flag<?>, Object>();
		int n = DefaultFlag.flagsList.length;
		// loop through all possible flags
		for (int i = 0; i < n; i++) {
			// set value of flag to null
			clearedFlags.put(DefaultFlag.flagsList[i], null);
		}
		// apply flags to region
		region.setFlags(clearedFlags);
		// send message to command sender
		plugin.getMessages().flagsClearedOfRegion(sender, regionName);

		return true;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.command.Command#getPermission()
	 */
	@Override
	public String getPermission() {
		return Permission.CLEAR.getNode();
	}
	
	/**
	 * Returns whether the command sender has permission to clear all flags of the specified region.
	 * @param sender Command sender
	 * @param region The region
	 * @return true if the player permission, false otherwise
	 */
	public boolean hasPermission(CommandSender sender, ProtectedRegion region) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			LocalPlayer localPlayer = plugin.getWorldGuard().wrapPlayer(player);

			if (sender.hasPermission(Permission.CLEAR.getNode())
					|| sender.hasPermission(Permission.CLEAR_MEMBER.getNode())
					&& region.isMember(localPlayer)
					|| sender.hasPermission(Permission.CLEAR_OWNER.getNode())
					&& region.isOwner(localPlayer)) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return true;
		}
	}
}
