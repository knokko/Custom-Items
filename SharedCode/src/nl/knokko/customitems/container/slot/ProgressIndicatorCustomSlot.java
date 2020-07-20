package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.container.IndicatorDomain;
import nl.knokko.customitems.container.slot.display.SlotDisplay;

public class ProgressIndicatorCustomSlot implements CustomSlot {
	
	private final SlotDisplay display;
	private final IndicatorDomain domain;
	
	public ProgressIndicatorCustomSlot(SlotDisplay display, IndicatorDomain domain) {
		this.display = display;
		this.domain = domain;
	}
	
	public SlotDisplay getDisplay() {
		return display;
	}
	
	public IndicatorDomain getDomain() {
		return domain;
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
