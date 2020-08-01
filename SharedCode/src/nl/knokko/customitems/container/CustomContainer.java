package nl.knokko.customitems.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import nl.knokko.customitems.container.fuel.FuelMode;
import nl.knokko.customitems.container.slot.CustomSlot;
import nl.knokko.customitems.container.slot.EmptyCustomSlot;
import nl.knokko.customitems.recipe.ContainerRecipe;
import nl.knokko.customitems.recipe.ContainerRecipe.InputEntry;
import nl.knokko.customitems.recipe.ContainerRecipe.OutputEntry;

public class CustomContainer {
	
	// A bukkit/minecraft limitation only allows inventories with a width of 9 slots
	private static final int WIDTH = 9;
	
	private String name;
	private String displayName;
	
	private CustomSlot[][] slots;
	
	// TODO Perhaps allow vanilla registries to be used as well
	private final Collection<ContainerRecipe> recipes;
	
	private FuelMode fuelMode;
	// TODO Perhaps allow overruling a vanilla type completely
	private VanillaContainerType type;
	private boolean persistentStorage;
	
	public CustomContainer(String name) {
		this(name, name, new ArrayList<>(), FuelMode.ALL, new CustomSlot[9][6], 
				VanillaContainerType.CRAFTING_TABLE, false);
	}
	
	public CustomContainer(String name, String displayName, 
			Collection<ContainerRecipe> recipes, FuelMode fuelMode, 
			CustomSlot[][] slots, VanillaContainerType type, boolean persistentStorage) {
		this.name = name;
		this.displayName = displayName;
		this.recipes = recipes;
		this.fuelMode = fuelMode;
		this.slots = slots;
		this.type = type;
		this.persistentStorage = persistentStorage;
		
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < slots[x].length; y++) {
				slots[x][y] = new EmptyCustomSlot();
			}
		}
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setName(String newName) {
		name = newName;
	}
	
	public void setDisplayName(String newName) {
		displayName = newName;
	}
	
	public VanillaContainerType getVanillaType() {
		return type;
	}
	
	public void setVanillaType(VanillaContainerType newType) {
		type = newType;
	}
	
	public boolean hasPersistentStorage() {
		return persistentStorage;
	}
	
	public void setPersistentStorage(boolean persistent) {
		persistentStorage = persistent;
	}
	
	public void setFuelMode(FuelMode newFuelMode) {
		fuelMode = newFuelMode;
	}
	
	public FuelMode getFuelMode() {
		return fuelMode;
	}
	
	public void resize(int newHeight) {
		
		int oldHeight = getHeight();
		
		CustomSlot[][] newSlots = new CustomSlot[WIDTH][oldHeight];
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < oldHeight && y < newHeight; y++) {
				newSlots[x][y] = slots[x][y];
			}
		}
		
		for (int y = oldHeight; y < newHeight; y++) {
			for (int x = 0; x < WIDTH; x++) {
				newSlots[x][y] = new EmptyCustomSlot();
			}
		}
			
		slots = newSlots;
		oldHeight = newHeight;
	}
	
	public int getHeight() {
		return slots[0].length;
	}

	public CustomSlot getSlot(int x, int y) {
		return slots[x][y];
	}
	
	public void setSlot(CustomSlot newSlot, int x, int y) {
		slots[x][y] = newSlot;
	}
	
	public Collection<ContainerRecipe> getRecipes() {
		return recipes;
	}
	
	public Set<String> getRequiredInputSlotNames() {
		Set<String> slotNames = new HashSet<>();
		for (ContainerRecipe recipe : recipes) {
			for (InputEntry input : recipe.getInputs()) {
				slotNames.add(input.inputSlotName);
			}
		}
		
		return slotNames;
	}
	
	public Set<String> getRequiredOutputSlotNames() {
		Set<String> slotNames = new HashSet<>();
		for (ContainerRecipe recipe : recipes) {
			for (OutputEntry output : recipe.getOutputs()) {
				slotNames.add(output.outputSlotName);
			}
		}
		
		return slotNames;
	}
}
