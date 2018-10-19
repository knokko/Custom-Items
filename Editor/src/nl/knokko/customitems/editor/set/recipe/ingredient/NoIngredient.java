package nl.knokko.customitems.editor.set.recipe.ingredient;

import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.util.bits.BitOutput;

public class NoIngredient implements Ingredient {
	
	private static final String[] INFO = {"empty"};

	@Override
	public void save(BitOutput output) {}

	@Override
	public byte getID() {
		return RecipeEncoding.Ingredient.NONE;
	}

	@Override
	public boolean conflictsWith(Ingredient other) {
		return other instanceof NoIngredient;
	}

	@Override
	public String[] getInfo() {
		return INFO;
	}
	
	@Override
	public String toString() {
		return INFO[0];
	}
}