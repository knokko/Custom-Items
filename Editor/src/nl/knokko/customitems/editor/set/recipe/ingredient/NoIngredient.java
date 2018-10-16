package nl.knokko.customitems.editor.set.recipe.ingredient;

import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.util.bits.BitOutput;

public class NoIngredient implements Ingredient {

	@Override
	public void save(BitOutput output) {}

	@Override
	public byte getID() {
		return RecipeEncoding.Ingredient.NONE;
	}
}