package nl.knokko.customitems.plugin;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import nl.knokko.customitems.plugin.recipe.CustomRecipe;
import nl.knokko.customitems.plugin.set.item.CustomItem;

public class CustomItemsEventHandler implements Listener {
	
	@EventHandler
	public void onItemUse(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		CustomItem custom = CustomItemsPlugin.getInstance().getSet().getItem(item);
		if(custom != null) {
			//for now, just make sure the custom item can't be used as their 'real' item
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void beforeCraft(PrepareItemCraftEvent event) {
		ItemStack result = event.getInventory().getResult();
		
		// Block vanilla recipes that attempt to use custom items
		if (result != null && result.getType() != Material.AIR) {
			ItemStack[] ingredients = event.getInventory().getStorageContents();
			for (ItemStack ingredient : ingredients) {
				if (ingredient.hasItemMeta() && ingredient.getItemMeta().isUnbreakable() && ingredient.getDurability() > 0) {
					event.getInventory().setResult(new ItemStack(Material.AIR));
					break;
				}
			}
		}
		
		// Check if there are any custom recipes matching the ingredients
		CustomRecipe[] recipes = CustomItemsPlugin.getInstance().getSet().getRecipes();
		if(recipes.length > 0) {
			ItemStack[] ingredients = event.getInventory().getStorageContents();
			ingredients = Arrays.copyOfRange(ingredients, 1, ingredients.length);
			Material[] ingredientTypes = new Material[ingredients.length];
			for(int index = 0; index < ingredients.length; index++)
				ingredientTypes[index] = ingredients[index].getType();
			for(int index = 0; index < recipes.length; index++) {
				if(recipes[index].shouldAccept(ingredients)) {
					event.getInventory().setResult(recipes[index].getResult());
					return;
				}
			}
		}
	}
}