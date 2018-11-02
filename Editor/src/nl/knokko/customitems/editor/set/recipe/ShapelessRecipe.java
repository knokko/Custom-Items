package nl.knokko.customitems.editor.set.recipe;

import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.recipe.ingredient.CustomItemIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.customitems.editor.set.recipe.result.Result;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class ShapelessRecipe extends Recipe {
	
	private Ingredient[] ingredients;

	public ShapelessRecipe(Result result, Ingredient[] ingredients) {
		super(result);
		this.ingredients = ingredients;
	}

	public ShapelessRecipe(BitInput input, ItemSet set) {
		super(input, set);
		byte ingredientCount = (byte) input.readNumber((byte) 4, false);
		ingredients = new Ingredient[ingredientCount];
		for (int counter = 0; counter < ingredientCount; counter++)
			ingredients[counter] = loadIngredient(input, set);
	}

	@Override
	protected void saveOwn(BitOutput output) {
		output.addNumber(ingredients.length, (byte) 4, false);
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
	
	public Ingredient[] getIngredients(){
		return ingredients;
	}
	
	public void setIngredients(Ingredient[] newIngredients) {
		ingredients = newIngredients;
	}
	
	/**
	 * Always returns false because shaped recipes simply have more priority
	 */
	@Override
	public boolean hasConflictingShapedIngredients(Ingredient[] ingredients) {
		return false;
	}

	@Override
	public boolean hasConflictingShapelessIngredients(Ingredient[] ingredients) {
		if (ingredients.length != this.ingredients.length)
			return false;
		boolean[] has = new boolean[ingredients.length];
		for (Ingredient ingredient : ingredients) {
			for (int index = 0; index < has.length; index++) {
				if (!has[index] && ingredient.conflictsWith(this.ingredients[index])) {
					has[index] = true;
					break;
				}
			}
		}
		for (boolean h : has)
			if (!h)
				return false;
		return true;
	}
}