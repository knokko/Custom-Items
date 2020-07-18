package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.container.fuel.CustomFuelRegistry;

public class FuelCustomSlot implements CustomSlot {
	
	private final int id;
	
	// TODO Add possibility for vanilla fuel registry
	private final CustomFuelRegistry fuelRegistry;
	
	public FuelCustomSlot(int id, CustomFuelRegistry fuelRegistry) {
		this.id = id;
		this.fuelRegistry = fuelRegistry;
	}
	
	public int getId() {
		return id;
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
