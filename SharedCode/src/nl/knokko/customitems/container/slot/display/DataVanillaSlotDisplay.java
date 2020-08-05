package nl.knokko.customitems.container.slot.display;

import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class DataVanillaSlotDisplay extends SlotDisplay {
	
	public static DataVanillaSlotDisplay load1(BitInput input) {
		
		CIMaterial material = CIMaterial.valueOf(input.readString());
		byte data = input.readByte();
		String displayName = input.readString();
		String[] lore = new String[input.readInt()];
		for (int loreIndex = 0; loreIndex < lore.length; loreIndex++) {
			lore[loreIndex] = input.readString();
		}
		int amount = input.readInt();
		
		return new DataVanillaSlotDisplay(material, data, displayName, lore, amount);
	}
	
	private final CIMaterial material;
	private final byte data;

	public DataVanillaSlotDisplay(CIMaterial material, byte data, 
			String displayName, String[] lore, int amount) {
		super(displayName, lore, amount);
		this.material = material;
		this.data = data;
	}
	
	@Override
	public void save(BitOutput output) {
		output.addByte(SlotDisplay.Encodings.DATA_VANILLA1);
		output.addString(material.name());
		output.addByte(data);
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
	
	public byte getData() {
		return data;
	}
}
