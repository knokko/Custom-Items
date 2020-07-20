package nl.knokko.customitems.container.slot.display;

import nl.knokko.customitems.item.CustomItem;

public class CustomItemSlotDisplay extends SlotDisplay {

	private final CustomItem item;
	
	public CustomItemSlotDisplay(CustomItem item, int amount) {
		super(item.getDisplayName(), item.getLore(), amount);
		this.item = item;
	}
	
	public CustomItem getItem() {
		return item;
	}
}
