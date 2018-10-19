package nl.knokko.customitems.editor.set.recipe.ingredient;

import nl.knokko.customitems.editor.set.Material;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class SimpleVanillaIngredient implements Ingredient {
	
	private final Material type;

	public SimpleVanillaIngredient(Material material) {
		this.type = material;
	}
	
	public SimpleVanillaIngredient(BitInput input) {
		type = Material.valueOf(input.readJavaString());
	}
	
	public Material getType() {
		return type;
	}

	@Override
	public void save(BitOutput output) {
		output.addJavaString(type.name());
	}

	@Override
	public byte getID() {
		return RecipeEncoding.Ingredient.VANILLA_SIMPLE;
	}

	@Override
	public boolean conflictsWith(Ingredient other) {
		if (other instanceof SimpleVanillaIngredient)
			return ((SimpleVanillaIngredient) other).type == type;
		if (other instanceof DataVanillaIngredient)
			return ((DataVanillaIngredient) other).getType() == type;
		return false;
	}
}