package nl.knokko.customitems.editor.set.recipe;

import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class ShapedRecipe extends Recipe {
	
	private final Ingredient[] ingredients;

	public ShapedRecipe() {
		ingredients = new Ingredient[9];
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
	
	public Ingredient getIngredient(int x, int y) {
		return ingredients[x + y * 3];
	}
	
	public void setIngredient(Ingredient ingredient, int x, int y) {
		ingredients[x + y * 3] = ingredient;
	}
}