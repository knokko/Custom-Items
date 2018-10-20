package nl.knokko.customitems.plugin.recipe;

import org.bukkit.inventory.ItemStack;

import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;

public class ShapelessCustomRecipe implements CustomRecipe {
	
	private final Ingredient[] ingredients;
	private final ItemStack result;

	public ShapelessCustomRecipe(Ingredient[] ingredients, ItemStack result) {
		this.ingredients = ingredients;
		this.result = result;
	}

	@Override
	public ItemStack getResult() {
		return result;
	}

	@Override
	public boolean shouldAccept(ItemStack[] ingredients) {
		boolean[] has = new boolean[this.ingredients.length];
		for (ItemStack ingredient : ingredients) {
			for (int index = 0; index < has.length; index++) {
				if (!has[index] && this.ingredients[index].accept(ingredient)) {
					has[index] = true;
					continue;
				}
				// When we reach this code, we don't need that ingredient
				return false;
			}
		}
		
		// Now see if we have all necessary ingredients
		for (boolean b : has)
			if (!b)
				return false;
		
		// We have exactly what we need
		return true;
	}
}