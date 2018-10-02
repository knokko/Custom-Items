package nl.knokko.customitems.plugin;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import nl.knokko.customitems.plugin.item.CustomItem;
import nl.knokko.customitems.plugin.item.CustomItems;
import nl.knokko.customitems.plugin.recipe.CustomRecipe;
import nl.knokko.customitems.plugin.recipe.CustomRecipes;

public class CustomItemsEventHandler implements Listener {
	
	@EventHandler
	public void onItemUse(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		CustomItem custom = CustomItems.getCustomItem(item);
		if(custom != null) {
			//for now, just make sure the custom item can't be used as the underlying tool
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void beforeCraft(PrepareItemCraftEvent event) {
		if(!event.isRepair()) {
			ItemStack result = event.getInventory().getResult();
			CustomRecipe[] recipes = CustomRecipes.getRecipes(result);
			if(recipes.length > 0) {
				ItemStack[] ingredients = event.getInventory().getStorageContents();
				Material[] ingredientTypes = new Material[9];
				for(int index = 0; index < 9; index++)
					ingredientTypes[index] = ingredients[index].getType();
				boolean shouldCancel = false;
				for(int index = 0; index < recipes.length; index++) {
					if(recipes[index].areMaterialsCorrect(ingredientTypes)) {
						if(recipes[index].shouldAccept(ingredients)) {
							shouldCancel = false;
							//make sure we don't use the result of another recipe with equal ingredient types
							event.getInventory().setResult(recipes[index].getResult());
							break;
						}
						else {
							shouldCancel = true;
						}
					}
				}
				if(shouldCancel) {
					event.getInventory().setResult(new ItemStack(Material.AIR));
				}
			}
		}
	}
}