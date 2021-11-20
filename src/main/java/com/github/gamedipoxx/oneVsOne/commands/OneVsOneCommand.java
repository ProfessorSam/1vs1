package com.github.gamedipoxx.oneVsOne.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.gamedipoxx.oneVsOne.Messages;
import com.github.gamedipoxx.oneVsOne.OneVsOne;
import com.github.gamedipoxx.oneVsOne.arena.Arena;

public class OneVsOneCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player == false) {
			return false;
		}
		Player player = (Player) sender;
		if (player.hasPermission("OneVsOne.admin")) {
			if (args.length >= 1) {
				switch (args[0]) {
				case ("create"):
					player.sendMessage("§cArena: " + Arena.createAndRegisterArena().getArenaName());
				case ("join"):
					if (args.length == 2) {
						if(OneVsOne.getArena().size() == 0) {
							player.sendMessage("§cKeine Arena verfügbar!");
							break;
						}
						for (Arena arena : OneVsOne.getArena()) {
							if(arena.getArenaName().equalsIgnoreCase(args[1])) {
								arena.joinPlayer(player);
							}
						}
					} 
				case("delete"):
					if(args.length != 2) {
						break;
					}
					if(Arena.getArena(args[1]) == null) {
						player.sendMessage(Messages.PREFIX.getString() + Messages.NOARENAFOUND.getString());
					}
					else {
						Arena.deleteAndUnregisterArena(Arena.getArena(args[1]));
					}
				
				default:
					break;
				}
			} else {
				player.sendMessage("§cCommands: /onevsone [create, join, delete]");
			}
		}
		return false;

	}
}
