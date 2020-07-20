package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.container.IndicatorDomain;
import nl.knokko.customitems.container.slot.display.SlotDisplay;

public class FuelIndicatorCustomSlot implements CustomSlot {
	
	private final String fuelSlotName;
	
	private final SlotDisplay display;
	private final IndicatorDomain domain;
	
	public FuelIndicatorCustomSlot(String fuelSlotName, SlotDisplay display,
			IndicatorDomain domain) {
		this.fuelSlotName = fuelSlotName;
		this.display = display;
		this.domain = domain;
	}
	
	public String getFuelSlotName() {
		return fuelSlotName;
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
