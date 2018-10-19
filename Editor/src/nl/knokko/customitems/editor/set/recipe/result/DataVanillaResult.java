package nl.knokko.customitems.editor.set.recipe.result;

import nl.knokko.customitems.editor.set.Material;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class DataVanillaResult extends Result {
	
	private final Material type;
	private final byte data;

	public DataVanillaResult(Material type, byte data, byte amount) {
		super(amount);
		this.type = type;
		this.data = data;
		initInfo();
	}
	
	public DataVanillaResult(BitInput input) {
		super(input);
		type = Material.valueOf(input.readJavaString());
		data = input.readByte();
		initInfo();
	}

	@Override
	protected void saveOwn(BitOutput output) {
		output.addJavaString(type.name());
		output.addByte(data);
	}

	@Override
	public byte getID() {
		return RecipeEncoding.Result.VANILLA_DATA;
	}

	@Override
	protected String[] createInfo() {
		return new String[] {
				"Vanilla result:",
				"Type: " + type.name().toLowerCase(),
				"Data: " + data
		};
	}

	@Override
	public String getString() {
		return type.name().toLowerCase() + "(" + data + ")";
	}
}