package nl.knokko.customitems.editor.set.recipe.result;

import nl.knokko.customitems.editor.set.Material;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class DataVanillaResult implements Result {
	
	private final Material type;
	private final byte data;

	public DataVanillaResult(Material type, byte data) {
		this.type = type;
		this.data = data;
	}
	
	public DataVanillaResult(BitInput input) {
		type = Material.valueOf(input.readJavaString());
		data = input.readByte();
	}

	@Override
	public void save(BitOutput output) {
		output.addJavaString(type.name());
		output.addByte(data);
	}

	@Override
	public byte getID() {
		return RecipeEncoding.Result.VANILLA_DATA;
	}
}