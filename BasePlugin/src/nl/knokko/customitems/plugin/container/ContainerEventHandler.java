package nl.knokko.customitems.plugin.container;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.container.VanillaContainerType;
import nl.knokko.customitems.container.slot.CustomSlot;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.data.PluginData;

public class ContainerEventHandler implements Listener {
	
	private static PluginData pluginData() {
		return CustomItemsPlugin.getInstance().getData();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		// Delay it to prevent the items to be dropped while the block is still there
		Bukkit.getScheduler().scheduleSyncDelayedTask(
				CustomItemsPlugin.getInstance(), 
				() -> pluginData().destroyCustomContainersAt(
						event.getBlock().getLocation()
				)
		);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getPlayer() instanceof Player) {
			pluginData().clearContainerSelectionLocation((Player) event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		
		if (event.getWhoClicked() instanceof Player) {
			
			ContainerInstance customContainer = pluginData().getCustomContainer((Player) event.getWhoClicked());
			if (customContainer != null) {
				
				int slotIndex = event.getRawSlot();
				CustomContainer containerType = customContainer.getType();
				if (slotIndex >= 0 && slotIndex < 9 * containerType.getHeight()) {
					CustomSlot slot = customContainer.getType().getSlot(slotIndex % 9, slotIndex / 9);
					
					// Make sure slots can only be used the way they should be used
					if (isTake(event.getAction())) {
						if (!slot.canTakeItems()) {
							event.setCancelled(true);
						}
					} else if (isInsert(event.getAction())) {
						if (!slot.canInsertItems()) {
							event.setCancelled(true);
						}
					} else if (
							event.getAction() == InventoryAction.SWAP_WITH_CURSOR ||
							
							/*
							 * NOTHING is an interesting case, because it can occur
							 * when players attempt to stack stackable custom items
							 * in a slot.
							 * Because it's hard to predict if anything will happen
							 * regardless, we will be safe and only allow the action
							 * if the slot supports both insert and take actions.
							 */
							event.getAction() == InventoryAction.NOTHING) {
						if (!slot.canTakeItems() || !slot.canInsertItems()) {
							event.setCancelled(true);
						}
					} else {
						
						// Some other inventory action occurred
						// I don't know whether or not this should be allowed
						// But better safe than sorry
						event.setCancelled(true);
					}
				}
			}
			
			List<CustomContainer> containerSelection = pluginData().getCustomContainerSelection(event.getWhoClicked());
			if (containerSelection != null) {
				
				// Block any inventory action during container selection
				event.setCancelled(true);
				
				int slotIndex = event.getRawSlot();
				if (slotIndex == 0) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(
							CustomItemsPlugin.getInstance(), 
							event.getWhoClicked()::closeInventory
					);
					event.getWhoClicked().closeInventory();
				} else if (slotIndex <= containerSelection.size()) {
					CustomContainer toOpen = containerSelection.get(slotIndex - 1);
					Bukkit.getScheduler().scheduleSyncDelayedTask(
							CustomItemsPlugin.getInstance(), 
							() -> pluginData().selectCustomContainer(
									(Player) event.getWhoClicked(), 
									toOpen
							)
					);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getPlayer().isSneaking()) {
			
			String blockName = ItemHelper.getMaterialName(event.getClickedBlock());
			CIMaterial blockType;
			try {
				blockType = CIMaterial.valueOf(blockName);
			} catch (IllegalArgumentException unknownBlockTpe) {
				blockType = null;
			}
			
			VanillaContainerType vanillaType = VanillaContainerType.fromMaterial(blockType);
			Inventory menu = pluginData().getCustomContainerMenu(
					event.getClickedBlock().getLocation(), 
					event.getPlayer(), vanillaType
			);
			
			if (menu != null) {
				event.getPlayer().openInventory(menu);
			}
		}
	}
	
	private boolean isTake(InventoryAction action) {
		return action == InventoryAction.PICKUP_ONE ||
				action == InventoryAction.PICKUP_SOME ||
				action == InventoryAction.PICKUP_HALF ||
				action == InventoryAction.PICKUP_ALL;
	}
	
	private boolean isInsert(InventoryAction action) {
		return action == InventoryAction.PLACE_ONE ||
				action == InventoryAction.PLACE_SOME ||
				action == InventoryAction.PLACE_ALL;
	}
}
