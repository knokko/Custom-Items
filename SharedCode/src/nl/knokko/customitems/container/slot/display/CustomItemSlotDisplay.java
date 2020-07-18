package nl.knokko.customitems.container.slot.display;

import nl.knokko.customitems.item.CustomItem;

public class CustomItemSlotDisplay extends SlotDisplay {

	private final CustomItem item;
	
	public CustomItemSlotDisplay(CustomItem item) {
		super(item.getDisplayName(), item.getLore());
		this.item = item;
	}
	
	public CustomItem getItem() {
		return item;
	}
}
