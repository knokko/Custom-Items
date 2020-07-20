package nl.knokko.customitems.container.slot.display;

import nl.knokko.customitems.item.CIMaterial;

public class DataVanillaSlotDisplay extends SlotDisplay {
	
	private final CIMaterial material;
	private final byte data;

	public DataVanillaSlotDisplay(CIMaterial material, byte data, 
			String displayName, String[] lore, int amount) {
		super(displayName, lore, amount);
		this.material = material;
		this.data = data;
	}

	public CIMaterial getMaterial() {
		return material;
	}
	
	public byte getData() {
		return data;
	}
}
