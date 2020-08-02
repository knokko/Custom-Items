package nl.knokko.customitems.plugin.container;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.container.VanillaContainerType;
import nl.knokko.customitems.container.slot.CustomSlot;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.data.PluginData;
import nl.knokko.customitems.plugin.set.ItemSet;

public class ContainerEventHandler implements Listener {
	
	private static PluginData pluginData() {
		return CustomItemsPlugin.getInstance().getData();
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		
		if (event.getWhoClicked() instanceof Player) {
			ContainerInstance customContainer = pluginData().getCustomContainer((Player) event.getWhoClicked());
			if (customContainer != null) {
				
				int slotIndex = event.getRawSlot();
				CustomContainer containerType = customContainer.getType();
				if (slotIndex < 9 * containerType.getHeight()) {
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
					} else {
						
						// Some other inventory action occurred
						// I don't know whether or not this should be allowed
						// But better safe than sorry
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getPlayer().isSneaking()) {
			
			ItemSet set = CustomItemsPlugin.getInstance().getSet();
			Collection<CustomContainer> potentialContainers = new ArrayList<>();
			String blockName = ItemHelper.getMaterialName(event.getClickedBlock());
			CIMaterial blockType;
			try {
				blockType = CIMaterial.valueOf(blockName);
			} catch (IllegalArgumentException unknownBlockTpe) {
				blockType = null;
			}
			for (CustomContainer container : set.getContainers()) {
				
				if (container.getVanillaType() == VanillaContainerType.CRAFTING_TABLE) {
					if (blockType == CIMaterial.CRAFTING_TABLE || blockType == CIMaterial.WORKBENCH) {
						potentialContainers.add(container);
					}
				} else if (container.getVanillaType() == VanillaContainerType.FURNACE) {
					if (blockType == CIMaterial.FURNACE || blockType == CIMaterial.BURNING_FURNACE) {
						potentialContainers.add(container);
					}
				} else if (container.getVanillaType() == VanillaContainerType.ENCHANTING_TABLE) {
					if (blockType == CIMaterial.ENCHANTING_TABLE || blockType == CIMaterial.ENCHANTMENT_TABLE) {
						potentialContainers.add(container);
					}
				} else if (container.getVanillaType() == VanillaContainerType.ANVIL) {
					if (blockType == CIMaterial.ANVIL || blockType == CIMaterial.CHIPPED_ANVIL
							|| blockType == CIMaterial.DAMAGED_ANVIL) {
						potentialContainers.add(container);
					}
				} else if (container.getVanillaType() == VanillaContainerType.LOOM) {
					if (blockType == CIMaterial.LOOM) {
						potentialContainers.add(container);
					}
				} else if (container.getVanillaType() == VanillaContainerType.BLAST_FURNACE) {
					if (blockType == CIMaterial.BLAST_FURNACE) {
						potentialContainers.add(container);
					}
				} else if (container.getVanillaType() == VanillaContainerType.SMOKER) {
					if (blockType == CIMaterial.SMOKER) {
						potentialContainers.add(container);
					}
				} else if (container.getVanillaType() == VanillaContainerType.STONE_CUTTER) {
					if (blockType == CIMaterial.STONECUTTER) {
						potentialContainers.add(container);
					}
				} else if (container.getVanillaType() == VanillaContainerType.GRINDSTONE) {
					if (blockType == CIMaterial.GRINDSTONE) {
						potentialContainers.add(container);
					}
				}
			}
			
			if (potentialContainers.size() == 1) {
				event.getPlayer().openInventory(pluginData().getCustomContainer(
						event.getClickedBlock().getLocation(), event.getPlayer(), 
						potentialContainers.iterator().next()
				).getInventory()
			);
			} else if (potentialContainers.size() > 1) {
				// TODO Handle this case
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
