package nl.knokko.customitems.container.slot;

import java.util.function.Function;

import nl.knokko.customitems.container.fuel.CustomFuelRegistry;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class FuelCustomSlot implements CustomSlot {
	
	public static FuelCustomSlot load1(
			BitInput input, Function<String, CustomFuelRegistry> fuelRegistryByName) {
		
		String name = input.readString();
		CustomFuelRegistry fuelRegistry = fuelRegistryByName.apply(input.readString());
		return new FuelCustomSlot(name, fuelRegistry);
	}
	
	private final String name;
	
	// TODO Add possibility for vanilla fuel registry
	private final CustomFuelRegistry fuelRegistry;
	
	public FuelCustomSlot(String name, CustomFuelRegistry fuelRegistry) {
		this.name = name;
		this.fuelRegistry = fuelRegistry;
	}
	
	@Override
	public void save(BitOutput output) {
		output.addByte(CustomSlot.Encodings.FUEL1);
		output.addString(name);
		output.addString(fuelRegistry.getName());
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

	@Override
	public CustomSlot safeClone(CustomSlot[][] existingSlots) {
		
		// This can only return true if this slot no longer exists
		if (tryName(name, existingSlots)) {
			return this;
		}
		
		// Try name0, name1, name2... until it finds a free name
		int counter = 0;
		while (!tryName(name + counter, existingSlots)) {
			counter++;
		}
		return new FuelCustomSlot(name + counter, fuelRegistry);
	}

	private boolean tryName(String fuelSlotName, CustomSlot[][] existingSlots) {
		for (CustomSlot[] row : existingSlots) {
			for (CustomSlot slot : row) {
				if (slot instanceof FuelCustomSlot && ((FuelCustomSlot)slot).getName().equals(fuelSlotName)) {
					return false;
				}
			}
		}
		return true;
	}
}
