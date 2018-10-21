package nl.knokko.customitems.plugin.recipe.ingredient;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NoIngredient implements Ingredient {

	@Override
	public boolean accept(ItemStack item) {
		return item.getType() == Material.AIR;
	}

	@Override
	public Material getType() {
		return Material.AIR;
	}

	@Override
	public short getData() {
		return 0;
	}
}