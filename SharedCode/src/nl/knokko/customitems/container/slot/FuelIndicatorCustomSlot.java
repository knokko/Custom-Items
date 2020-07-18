package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.container.IndicatorDomain;

public class FuelIndicatorCustomSlot implements CustomSlot {
	
	private final String fuelSlotName;
	
	private final IndicatorDomain domain;
	
	public FuelIndicatorCustomSlot(String fuelSlotName, IndicatorDomain domain) {
		this.fuelSlotName = fuelSlotName;
		this.domain = domain;
	}
	
	public String getFuelSlotName() {
		return fuelSlotName;
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
