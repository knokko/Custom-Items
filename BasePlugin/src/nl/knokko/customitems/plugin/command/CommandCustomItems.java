package nl.knokko.customitems.plugin.command;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import nl.knokko.customitems.plugin.item.CustomItem;
import nl.knokko.customitems.plugin.item.CustomItems;

public class CommandCustomItems implements CommandExecutor {
	
	public static Player getOnlinePlayer(String name) {
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		for(Player player : players)
			if(player.getName().equals(name))
				return player;
		return null;
	}
	
	private static void sendUseage(CommandSender sender) {
		sendGiveUseage(sender);
	}
	
	private static void sendGiveUseage(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "Use /customitems give <item name> [player name]");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.isOp()) {
			sender.sendMessage(ChatColor.DARK_RED + "Only operators can use this command.");
			return false;
		}
		if(args.length == 0) {
			sendUseage(sender);
		}
		else {
			if(args[0].equals("give")) {
				if(args.length == 2 || args.length == 3) {
					CustomItem item = CustomItems.getCustomItem(args[1]);
					if(item != null) {
						Player receiver = null;
						if(args.length == 2) {
							if(sender instanceof Player) {
								receiver = (Player) sender;
							}
							else {
								sender.sendMessage("Non-player operators need to specify a player name");
							}
						}
						if(args.length == 3) {
							receiver = getOnlinePlayer(args[2]);
							if(receiver == null) {
								sender.sendMessage(ChatColor.RED + "Can't find player " + args[2]);
								sender.sendMessage(ChatColor.RED + "This can only be used on online players.");
							}
						}
						if(receiver != null) {
							receiver.getInventory().addItem(item.create(1));
							sender.sendMessage(ChatColor.GREEN + "Custom item has been given.");
						}
					}
					else {
						sender.sendMessage(ChatColor.RED + "There is no custom item with name " + args[1]);
					}
				}
				else {
					sendGiveUseage(sender);
				}
			}
			else if(args[0].equals("damage")) {
				if(sender instanceof Player) {
					Player player = (Player) sender;
					ItemStack item = player.getInventory().getItemInMainHand();
					if(item != null) {
						sender.sendMessage("Item durability is " + item.getDurability());
					}
					else {
						sender.sendMessage("Hold the item you want to check in your main hand");
					}
				}
				else {
					sender.sendMessage("Only players can view the damage of the item in their main hand");
				}
			}
			else {
				sendUseage(sender);
			}
		}
		return false;
	}
}