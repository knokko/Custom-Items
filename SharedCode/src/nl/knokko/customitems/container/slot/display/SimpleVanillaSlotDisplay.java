package nl.knokko.customitems.container.slot.display;

import nl.knokko.customitems.item.CIMaterial;

public class SimpleVanillaSlotDisplay extends SlotDisplay {
	
	private final CIMaterial material;

	public SimpleVanillaSlotDisplay(CIMaterial material, String displayName, 
			String[] lore, int amount) {
		super(displayName, lore, amount);
		this.material = material;
	}
	
	public CIMaterial getMaterial() {
		return material;
	}
}
