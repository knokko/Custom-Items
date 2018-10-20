package nl.knokko.customitems.editor.set.recipe;

import java.util.ArrayList;
import java.util.Collection;

import nl.knokko.customitems.editor.set.CustomItem;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.recipe.ingredient.CustomItemIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.editor.set.recipe.result.Result;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class ShapelessRecipe extends Recipe {
	
	private final Collection<Ingredient> ingredients;

	public ShapelessRecipe(Result result, Collection<Ingredient> ingredients) {
		super(result);
		this.ingredients = ingredients;
	}

	public ShapelessRecipe(BitInput input, ItemSet set) {
		super(input, set);
		byte ingredientCount = input.readByte();
		ingredients = new ArrayList<Ingredient>(ingredientCount);
		for (int counter = 0; counter < ingredientCount; counter++)
			ingredients.add(loadIngredient(input, set));
	}

	@Override
	protected void saveOwn(BitOutput output) {
		output.addByte((byte) ingredients.size());
		for (Ingredient ingredient : ingredients) {
			output.addByte(ingredient.getID());
			ingredient.save(output);
		}
	}
	
	@Override
	public boolean requires(CustomItem item) {
		for (Ingredient ingredient : ingredients)
			if (ingredient instanceof CustomItemIngredient && ((CustomItemIngredient)ingredient).getItem() == item)
				return true;
		return false;
	}
	
	@Override
	public byte getClassEncoding() {
		return RecipeEncoding.SHAPELESS_RECIPE;
	}
	
	public Collection<Ingredient> getIngredients(){
		return ingredients;
	}
	
	/**
	 * Determines if this recipe has conflicting ingredients with a ShapedRecipe that would have the specified ingredients.
	 */
	@Override
	public boolean hasConflictingShapedIngredients(Ingredient[] ingredients) {
		//TODO test this!
		boolean[] used = new boolean[ingredients.length];
		outerLoop:
		for (Ingredient own : this.ingredients) {
			for (int index = 0; index < used.length; index++) {
				if (!used[index] && own.conflictsWith(ingredients[index])) {
					used[index] = true;
					continue outerLoop;
				}
			}
			// If we reach this point, one of our required ingredients is not in the specified ingredients
			return false;
		}
		for (int index = 0; index < used.length; index++)
			if (!used[index] && !(ingredients[index] instanceof NoIngredient))
				return false; // Although all of our ingredients are met, there are extra ingredients left
		return true;
	}
}