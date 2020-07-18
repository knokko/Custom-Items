package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.container.slot.display.SlotDisplay;

public class DecorationCustomSlot implements CustomSlot {
	
	private final SlotDisplay display;
	
	public DecorationCustomSlot(SlotDisplay display) {
		this.display = display;
	}
	
	public SlotDisplay getDisplay() {
		return display;
	}

	@Override
	public boolean canInsertItems() {
		return false;
	}

	@Override
	public boolean canTakeItems() {
		return false;
	}
}
