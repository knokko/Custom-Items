package nl.knokko.customitems.editor.set.recipe.result;

import nl.knokko.customitems.editor.set.Material;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class SimpleVanillaResult implements Result {
	
	private final Material type;

	public SimpleVanillaResult(Material type) {
		this.type = type;
	}
	
	public SimpleVanillaResult(BitInput input) {
		type = Material.valueOf(input.readJavaString());
	}

	@Override
	public void save(BitOutput output) {
		output.addJavaString(type.name());
	}

	@Override
	public byte getID() {
		return RecipeEncoding.Result.VANILLA_SIMPLE;
	}
}