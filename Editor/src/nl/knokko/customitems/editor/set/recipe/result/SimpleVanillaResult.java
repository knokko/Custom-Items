package nl.knokko.customitems.editor.set.recipe.result;

import nl.knokko.customitems.editor.set.Material;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class SimpleVanillaResult extends Result {
	
	private final Material type;

	public SimpleVanillaResult(Material type, byte amount) {
		super(amount);
		this.type = type;
		initInfo();
	}
	
	public SimpleVanillaResult(BitInput input) {
		super(input);
		type = Material.valueOf(input.readJavaString());
		initInfo();
	}

	@Override
	protected void saveOwn(BitOutput output) {
		output.addJavaString(type.name());
	}

	@Override
	public byte getID() {
		return RecipeEncoding.Result.VANILLA_SIMPLE;
	}

	@Override
	protected String[] createInfo() {
		return new String[] {
				"Vanilla result:",
				"Type: " + type
		};
	}

	@Override
	public String getString() {
		return type.name().toLowerCase();
	}
}