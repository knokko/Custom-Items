package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.container.IndicatorDomain;
import nl.knokko.customitems.container.slot.display.SlotDisplay;

public class ProgressIndicatorCustomSlot implements CustomSlot {
	
	private final SlotDisplay display;
	private final SlotDisplay placeholder;
	private final IndicatorDomain domain;
	
	public ProgressIndicatorCustomSlot(SlotDisplay display, SlotDisplay placeholder, 
			IndicatorDomain domain) {
		this.display = display;
		this.placeholder = placeholder;
		this.domain = domain;
	}
	
	public SlotDisplay getDisplay() {
		return display;
	}
	
	public SlotDisplay getPlaceHolder() {
		return placeholder;
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
