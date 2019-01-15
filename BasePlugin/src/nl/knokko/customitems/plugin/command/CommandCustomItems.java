/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2018 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package nl.knokko.customitems.plugin.command;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.LanguageFile;
import nl.knokko.customitems.plugin.set.item.CustomItem;

public class CommandCustomItems implements CommandExecutor {
	
	public static Player getOnlinePlayer(String name) {
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		for(Player player : players)
			if(player.getName().equals(name))
				return player;
		return null;
	}
	
	private void sendUseage(CommandSender sender) {
		sendGiveUseage(sender);
	}
	
	private void sendGiveUseage(CommandSender sender) {
		sender.sendMessage(lang.getCommandUseage());
	}
	
	private LanguageFile lang;
	
	public CommandCustomItems(LanguageFile lang) {
		this.lang = lang;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.isOp()) {
			sender.sendMessage(lang.getCommandNoAccess());
			return false;
		}
		if(args.length == 0) {
			sendUseage(sender);
		}
		else {
			if(args[0].equals("give")) {
				if(args.length == 2 || args.length == 3) {
					CustomItem item = CustomItemsPlugin.getInstance().getSet().getItem(args[1]);
					if(item != null) {
						Player receiver = null;
						if(args.length == 2) {
							if(sender instanceof Player) {
								receiver = (Player) sender;
							}
							else {
								sender.sendMessage(lang.getCommandNoPlayerSpecified());
							}
						}
						if(args.length == 3) {
							receiver = getOnlinePlayer(args[2]);
							if(receiver == null) {
								sender.sendMessage(lang.getCommandPlayerNotFound(args[2]));
							}
						}
						if(receiver != null) {
							receiver.getInventory().addItem(item.create(1));
							sender.sendMessage(lang.getCommandItemGiven());
						}
					}
					else {
						sender.sendMessage(lang.getCommandNoSuchItem(args[1]));
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
						sender.sendMessage(lang.getCommandDamageItem(item.getDurability()));
					}
					else {
						sender.sendMessage(lang.getCommandDamageNotInHand());
					}
				}
				else {
					sender.sendMessage(lang.getCommandDamageNoPlayer());
				}
			}
			else {
				sendUseage(sender);
			}
		}
		return false;
	}
}