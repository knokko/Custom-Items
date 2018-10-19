package nl.knokko.customitems.editor.set.recipe.ingredient;

import nl.knokko.customitems.editor.set.Material;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class DataVanillaIngredient implements Ingredient {
	
	private final Material type;
	private final byte data;
	
	private String[] info;

	public DataVanillaIngredient(Material type, byte data) {
		this.type = type;
		this.data = data;
		determineInfo();
	}
	
	public DataVanillaIngredient(BitInput input) {
		type = Material.valueOf(input.readJavaString());
		data = input.readByte();
		determineInfo();
	}
	
	private void determineInfo() {
		info = new String[] {
				"Vanilla ingredient:",
				"Type: " + type.name().toLowerCase(),
				"Data: " + data
		};
	}
	
	public Material getType() {
		return type;
	}
	
	public byte getData() {
		return data;
	}

	@Override
	public void save(BitOutput output) {
		output.addJavaString(type.name());
		output.addByte(data);
	}

	@Override
	public byte getID() {
		return RecipeEncoding.Ingredient.VANILLA_DATA;
	}

	@Override
	public boolean conflictsWith(Ingredient other) {
		if (other instanceof SimpleVanillaIngredient)
			return ((SimpleVanillaIngredient) other).getType() == type;
		if (other instanceof DataVanillaIngredient) {
			DataVanillaIngredient dvi = (DataVanillaIngredient) other;
			return dvi.type == type && dvi.data == data;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return type.name().toLowerCase() + "(" + data + ")";
	}

	@Override
	public String[] getInfo() {
		return info;
	}
}