package nl.knokko.customitems.container.fuel;

import java.util.Collection;

public class CustomFuelRegistry {

	private String name;
	private Collection<FuelEntry> entries;
	
	public CustomFuelRegistry(String name, Collection<FuelEntry> entries) {
		this.name = name;
		this.entries = entries;
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
