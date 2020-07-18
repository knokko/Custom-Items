package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.container.fuel.CustomFuelRegistry;

public class FuelCustomSlot implements CustomSlot {
	
	private final String name;
	
	// TODO Add possibility for vanilla fuel registry
	private final CustomFuelRegistry fuelRegistry;
	
	public FuelCustomSlot(String name, CustomFuelRegistry fuelRegistry) {
		this.name = name;
		this.fuelRegistry = fuelRegistry;
	}
	
	public String getName() {
		return name;
	}
	
	public CustomFuelRegistry getRegistry() {
		return fuelRegistry;
	}

	@Override
	public boolean canInsertItems() {
		return true;
	}

	@Override
	public boolean canTakeItems() {
		return true;
	}

}
