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

}
