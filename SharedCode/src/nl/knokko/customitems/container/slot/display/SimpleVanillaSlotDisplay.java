package nl.knokko.customitems.container.slot.display;

import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class SimpleVanillaSlotDisplay extends SlotDisplay {
	
	public static SimpleVanillaSlotDisplay load1(BitInput input) {
		
		CIMaterial material = CIMaterial.valueOf(input.readString());
		String displayName = input.readString();
		String[] lore = new String[input.readInt()];
		for (int loreIndex = 0; loreIndex < lore.length; loreIndex++) {
			lore[loreIndex] = input.readString();
		}
		int amount = input.readInt();
		
		return new SimpleVanillaSlotDisplay(material, displayName, lore, amount);
	}
	
	private final CIMaterial material;

	public SimpleVanillaSlotDisplay(CIMaterial material, String displayName, 
			String[] lore, int amount) {
		super(displayName, lore, amount);
		this.material = material;
	}
	
	@Override
	public void save(BitOutput output) {
		output.addByte(SlotDisplay.Encodings.SIMPLE_VANILLA1);
		output.addString(material.name());
		output.addString(displayName);
		output.addInt(lore.length);
		for (String line : lore) {
			output.addString(line);
		}
		output.addInt(amount);
	}
	
	public CIMaterial getMaterial() {
		return material;
	}
}
