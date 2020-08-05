package nl.knokko.customitems.container.fuel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

import nl.knokko.customitems.recipe.SCIngredient;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class CustomFuelRegistry {
	
	public static CustomFuelRegistry load1(BitInput input, 
			Supplier<SCIngredient> loadIngredient) {
		
		String name = input.readString();
		int numEntries = input.readInt();
		Collection<FuelEntry> entries = new ArrayList<>(numEntries);
		
		for (int entryCounter = 0; entryCounter < numEntries; entryCounter++) {
			SCIngredient ingredient = loadIngredient.get();
			int burnTime = input.readInt();
			entries.add(new FuelEntry(ingredient, burnTime));
		}
		
		return new CustomFuelRegistry(name, entries);
	}

	private String name;
	private Collection<FuelEntry> entries;
	
	public CustomFuelRegistry(String name, Collection<FuelEntry> entries) {
		this.name = name;
		this.entries = entries;
	}
	
	public void save1(BitOutput output, Consumer<SCIngredient> saveIngredient) {
		output.addString(name);
		output.addInt(entries.size());
		for (FuelEntry entry : entries) {
			saveIngredient.accept(entry.getFuel());
			output.addInt(entry.getBurnTime());
		}
	}
	
	/**
	 * Should only be called from Editor/ItemSet
	 */
	public void setName(String newName) {
		name = newName;
	}
	
	/**
	 * Should only be called from Editor/ItemSet
	 */
	public void setEntries(Collection<FuelEntry> newEntries) {
		entries = newEntries;
	}
	
	public String getName() {
		return name;
	}
	
	public Iterable<FuelEntry> getEntries() {
		return entries;
	}
}
