package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.container.IndicatorDomain;

public class ProgressIndicatorCustomSlot implements CustomSlot {
	
	private final IndicatorDomain domain;
	
	public ProgressIndicatorCustomSlot(IndicatorDomain domain) {
		this.domain = domain;
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
