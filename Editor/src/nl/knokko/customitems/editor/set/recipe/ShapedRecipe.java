package nl.knokko.customitems.editor.set.recipe;

import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.recipe.ingredient.CustomItemIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.customitems.editor.set.recipe.result.Result;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class ShapedRecipe extends Recipe {
	
	private final Ingredient[] ingredients;

	public ShapedRecipe(Ingredient[] ingredients, Result result) {
		super(result);
		this.ingredients = ingredients;
	}
	
	public ShapedRecipe(BitInput input, ItemSet set) {
		super(input, set);
		ingredients = new Ingredient[9];
		for (int index = 0; index < ingredients.length; index++)
			ingredients[index] = loadIngredient(input, set);
	}

	@Override
	protected void saveOwn(BitOutput output) {
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
	public boolean hasConflictingShapedIngredients(Ingredient[] ingredients) {
		for (int index = 0; index < 9; index++)
			if (!ingredients[index].conflictsWith(this.ingredients[index]))
				return false;
		return true;
	}
	
	@Override
	protected byte getClassEncoding() {
		return RecipeEncoding.SHAPED_RECIPE;
	}
	
	public Ingredient getIngredient(int x, int y) {
		return ingredients[x + y * 3];
	}
	
	/**
	 * Don't modify this array!
	 * @return The array containing the ingredients of this shaped recipe
	 */
	public Ingredient[] getIngredients() {
		return ingredients;
	}
	
	public void setIngredients(Ingredient[] ingredients) {
		System.arraycopy(ingredients, 0, this.ingredients, 0, 9);
	}
	
	/**
	 * Shaped recipes simply have more priority than shapeless recipes
	 */
	@Override
	public boolean hasConflictingShapelessIngredients(Ingredient[] ingredients) {
		return false;
	}
}