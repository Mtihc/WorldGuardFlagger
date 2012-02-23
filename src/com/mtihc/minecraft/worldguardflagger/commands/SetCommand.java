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
 * Command to apply a flag preset to a WorldGuard region.
 * 
 * @author Mitch
 *
 */
public class SetCommand extends BukkitCommand {

	private WorldGuardFlagger plugin;

	/**
	 * Constructor
	 * @param plugin The WorldGuardFlagger plugin
	 * @param name The command's label
	 * @param aliases The command's aliases
	 */
	public SetCommand(WorldGuardFlagger plugin, String name, List<String> aliases) {
		super(name, "Apply a flagpreset to a WorldGuard region", "<region> [world] <flagpreset>", aliases);
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
		
		String regionName = null;
		World world = null;
		String presetName = null;

		// check amount of arguments when Player executes the command
		if (sender instanceof Player && args.length == 2) {
			regionName = args[0];
			presetName = args[1];
			// world is the player's current world
			world = ((Player) sender).getWorld();
		}
		// check amount of arguments when anyone executes the command
		else if (args.length == 3) {
			regionName = args[0];
			// world is the world specified as an argument
			world = plugin.getServer().getWorld(args[1]);
			presetName = args[2];
		} else {
			// incorrect number of arguments
			if (sender instanceof Player) {
				plugin.getMessages().incorrectNumberOfArguments(sender,
						"region name and preset name");
			} else {
				plugin.getMessages().incorrectNumberOfArguments(sender,
						"region name, world and preset name");
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
			// region needs to exist,
			// in order to check permissions
			//

			WorldGuardPlugin worldGuard = plugin.getWorldGuard();
			// find region
			region = worldGuard.getRegionManager(world).getRegion(regionName);

			// check permissions
			hasPermission = hasPermission(sender, region, presetName);
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
		// check preset existance
		if (!plugin.getConfigYaml().hasPreset(presetName)) {
			// no preset
			plugin.getMessages().flagPresetDoesNotExist(sender, presetName);
			return false;
		}

		//
		// we made it through all checks
		// lets execute the command
		//
		doSetFlags(sender, presetName, region);
		// send message to command sender
		plugin.getMessages().flagsAppliedToRegion(sender, presetName, region.getId());
		
		return true;
	}
	
	/**
	 * Applies a preset to a region
	 * @param sender Command sender
	 * @param presetName preset name
	 * @param region the protected region
	 */
	private void doSetFlags(CommandSender sender, String presetName, ProtectedRegion region) {
		
		HashMap<Flag<?>, Object> flags = new HashMap<Flag<?>, Object>();
		int n = DefaultFlag.flagsList.length;
		// loop through all possible flags
		for (int i = 0; i < n; i++) {
			Flag<?> flag = DefaultFlag.flagsList[i];

			
			// get value for flag, null if not defined
			flags.put(flag, plugin.parseFlagInput(sender, presetName, flag));
		}
		// apply flags to region
		region.setFlags(flags);
		
	}

	/* (non-Javadoc)
	 * @see org.bukkit.command.Command#getPermission()
	 */
	@Override
	public String getPermission() {
		return Permission.SET.getNode();
	}
	
	/**
	 * Returns whether the command sender has permission to apply the preset to the protected region.
	 * @param sender Command sender
	 * @param region the protected region
	 * @param preset preset name
	 * @return true if the player has permission, false otherwise
	 */
	public boolean hasPermission(CommandSender sender, ProtectedRegion region, String preset) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			LocalPlayer localPlayer = plugin.getWorldGuard().wrapPlayer(player);

			if (sender.hasPermission(Permission.SET.getNode())
					|| sender.hasPermission(Permission.SET.getNode() + ".*")
					|| sender.hasPermission(Permission.SET.getNode() + "."
							+ preset)) {
				return true;
			} else if (region.isMember(localPlayer)
					&& (sender.hasPermission(Permission.SET_MEMBER.getNode())
							|| sender.hasPermission(Permission.SET_MEMBER.getNode() + ".*") 
							|| sender.hasPermission(Permission.SET_MEMBER.getNode() + "." + preset))) {
				return true;
			} else if (region.isOwner(localPlayer)
					&& (sender.hasPermission(Permission.SET_OWNER.getNode())
							|| sender.hasPermission(Permission.SET_OWNER.getNode() + ".*") 
							|| sender.hasPermission(Permission.SET_OWNER.getNode() + "." + preset))) {
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
