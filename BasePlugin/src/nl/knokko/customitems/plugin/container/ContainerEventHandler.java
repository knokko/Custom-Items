package nl.knokko.customitems.plugin.container;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.data.PluginData;

public class ContainerEventHandler implements Listener {
	
	private static PluginData getPluginData() {
		return CustomItemsPlugin.getInstance().getData();
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		if (event.getPlayer() instanceof Player) {
			Player player = (Player) event.getPlayer();
			PluginData data = getPluginData();
			ContainerInstance session = data.getContainerSession(player);
			
			// If the opened inventory doesn't belong to the container session, we
			// need to stop the container session
			if (session != null && event.getInventory() != session.getInventory()) {
				data.closeContainerSession(player);
			}
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getPlayer() instanceof Player) {
			Player player = (Player) event.getPlayer();
			PluginData data = getPluginData();
			
			// If the player is in a container session and closes his inventory,
			if (data.getContainerSession(player) != null) {
				data.closeContainerSession(player);
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		
	}
	
	public static void onDisable() {
		
	}
}
