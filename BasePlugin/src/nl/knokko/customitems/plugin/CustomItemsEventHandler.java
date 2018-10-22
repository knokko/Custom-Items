package nl.knokko.customitems.plugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import nl.knokko.customitems.plugin.recipe.CustomRecipe;
import nl.knokko.customitems.plugin.recipe.ShapedCustomRecipe;
import nl.knokko.customitems.plugin.recipe.ShapelessCustomRecipe;
import nl.knokko.customitems.plugin.set.item.CustomItem;

public class CustomItemsEventHandler implements Listener {
	
	private Map<UUID,Boolean> shouldInterfere = new HashMap<UUID,Boolean>();
	
	@EventHandler
	public void onItemUse(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		CustomItem custom = CustomItemsPlugin.getInstance().getSet().getItem(item);
		if(custom != null) {
			//for now, just make sure the custom item can't be used as their 'real' item
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		SlotType type = event.getSlotType();
		InventoryAction action = event.getAction();
		if (type == SlotType.RESULT && action != InventoryAction.NOTHING) {
			if (shouldInterfere.get(event.getWhoClicked().getUniqueId())) {
				if (action == InventoryAction.PICKUP_ALL) {
					// This block deals with normal crafting
					Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
						/*
				 		* For every itemstack in crafting matrix when 1 item was crafted:
				 		* actualAmount = 2 * (initialAmount - 1)
				 		* desiredAmount = initialAmount - 1
				 		* desiredAmount = actualAmount / 2;
				 		*/
						ItemStack[] contents = event.getInventory().getContents();
						for (int index = 1; index < contents.length; index++) {
							contents[index].setAmount(contents[index].getAmount() / 2);
						}
					});
				} else if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
					// This block deals with shift clicks on the result slot
					int amountPerCraft = event.getCurrentItem().getAmount();
					int minAmount = 64;
					ItemStack[] contents = event.getInventory().getContents();
					ItemStack result = contents[0].clone();
					for (int index = 1; index < contents.length; index++) 
						if (contents[index].getType() != Material.AIR && contents[index].getAmount() < minAmount)
							minAmount = contents[index].getAmount();
					event.setResult(Result.DENY);
					for (int index = 1; index < contents.length; index++)
						contents[index].setAmount(contents[index].getAmount() - minAmount);
					result.setAmount(amountPerCraft * minAmount);
					event.getInventory().setItem(0, new ItemStack(Material.AIR));
					event.getWhoClicked().getInventory().addItem(result);
				} else {
					// Maybe, there is some edge case I don't know about, so cancel it just to be sure
					event.setResult(Result.DENY);
				}
			}
		} else if (action == InventoryAction.NOTHING || action == InventoryAction.PICKUP_ONE || action == InventoryAction.PICKUP_SOME) {
			ItemStack cursor = event.getCursor();
			ItemStack current = event.getCurrentItem();
			// This block makes custom items stackable
			if (cursor.getType() != Material.AIR && cursor.getType() == current.getType() && cursor.hasItemMeta() && cursor.getItemMeta().isUnbreakable()
					&& current.hasItemMeta() && current.getItemMeta().isUnbreakable()
					&& CustomItem.getDamage(current) == CustomItem.getDamage(cursor)) {
				event.setResult(Result.DENY);
				int amount = current.getAmount() + cursor.getAmount();
				if (amount <= 64) {
					current.setAmount(amount);
					Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
						event.getView().getPlayer().setItemOnCursor(new ItemStack(Material.AIR));
					});
				} else {
					current.setAmount(64);
					cursor.setAmount(amount - 64);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void beforeCraft(PrepareItemCraftEvent event) {
		ItemStack result = event.getInventory().getResult();
		
		// Block vanilla recipes that attempt to use custom items
		if (result != null && result.getType() != Material.AIR) {
			ItemStack[] ingredients = event.getInventory().getStorageContents();
			for (ItemStack ingredient : ingredients) {
				if (ingredient.hasItemMeta() && ingredient.getItemMeta().isUnbreakable()) {
					event.getInventory().setResult(new ItemStack(Material.AIR));
					break;
				}
			}
		}
		
		// Check if there are any custom recipes matching the ingredients
		CustomRecipe[] recipes = CustomItemsPlugin.getInstance().getSet().getRecipes();
		if(recipes.length > 0) {
			// Determine ingredients
			ItemStack[] ingredients = event.getInventory().getStorageContents();
			ingredients = Arrays.copyOfRange(ingredients, 1, ingredients.length);
			Material[] ingredientTypes = new Material[ingredients.length];
			for(int index = 0; index < ingredients.length; index++)
				ingredientTypes[index] = ingredients[index].getType();
			
			// Shaped recipes first because they have priority
			for(int index = 0; index < recipes.length; index++) {
				if(recipes[index] instanceof ShapedCustomRecipe && recipes[index].shouldAccept(ingredients)) {
					event.getInventory().setResult(recipes[index].getResult());
					shouldInterfere.put(event.getView().getPlayer().getUniqueId(), true);
					return;
				}
			}
			
			// No shaped recipe fits, so try the shapeless recipes
			for(int index = 0; index < recipes.length; index++) {
				if(recipes[index] instanceof ShapelessCustomRecipe && recipes[index].shouldAccept(ingredients)) {
					event.getInventory().setResult(recipes[index].getResult());
					shouldInterfere.put(event.getView().getPlayer().getUniqueId(), true);
					return;
				}
			}
		}
		shouldInterfere.put(event.getView().getPlayer().getUniqueId(), false);
	}
}