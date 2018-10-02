package nl.knokko.customitems.plugin.recipe;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CustomRecipes {
	
	private static final CustomRecipe[] EMPTY = {};
	
	private static Map<Material, CustomRecipe[]> recipeMap = new EnumMap<Material, CustomRecipe[]>(Material.class);
	
	public static void register(CustomRecipe recipe) {
		CustomRecipe[] recipes = recipeMap.get(recipe.getResult().getType());
		if(recipes == null)
			recipes = new CustomRecipe[1];
		else
			recipes = Arrays.copyOf(recipes, recipes.length + 1);
		recipes[recipes.length - 1] = recipe;
		recipeMap.put(recipe.getResult().getType(), recipes);
	}
	
	public static CustomRecipe[] getRecipes(ItemStack result) {
		CustomRecipe[] recipes = recipeMap.get(result.getType());
		if(recipes == null) return EMPTY;
		boolean[] correctResults = new boolean[recipes.length];
		int length = 0;
		for(int index = 0; index < recipes.length; index++) {
			if(recipes[index].getResult().equals(result)) {
				correctResults[index] = true;
				length++;
			}
		}
		CustomRecipe[] correctRecipes = new CustomRecipe[length];
		int correctIndex = 0;
		for(int index = 0; index < correctResults.length; index++)
			if(correctResults[index])
				correctRecipes[correctIndex++] = recipes[index];
		return correctRecipes;
	}
}