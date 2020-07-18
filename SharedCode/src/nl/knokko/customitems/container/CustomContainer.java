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
	
	private CustomSlot[][] slots;
	
	// TODO Perhaps allow vanilla registries to be used as well
	private final Collection<ContainerRecipe> recipes;
	
	private FuelMode fuelMode;
	// TODO Perhaps allow overruling a vanilla type completely
	private VanillaContainerType type;
	private boolean persistentStorage;
	
	public CustomContainer() {
		this(new ArrayList<>(), FuelMode.ALL, new CustomSlot[9][6], 
				VanillaContainerType.CRAFTING_TABLE, false);
	}
	
	public CustomContainer(Collection<ContainerRecipe> recipes, FuelMode fuelMode, 
			CustomSlot[][] slots, VanillaContainerType type, boolean persistentStorage) {
		this.recipes = recipes;
		this.fuelMode = fuelMode;
		this.slots = slots;
		this.type = type;
		this.persistentStorage = persistentStorage;
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
	
	public void resize(int newWidth, int newHeight) {
		
		int oldWidth = getWidth();
		int oldHeight = getHeight();
		
		CustomSlot[][] newSlots = new CustomSlot[oldWidth][oldHeight];
		for (int x = 0; x < oldWidth && x < newWidth; x++) {
			for (int y = 0; y < oldHeight && y < newHeight; y++) {
				newSlots[x][y] = slots[x][y];
			}
		}
		
		for (int x = oldWidth; x < newWidth; x++) {
			for (int y = 0; y < newHeight; y++) {
				newSlots[x][y] = new EmptyCustomSlot();
			}
		}
		
		for (int y = oldHeight; y < newHeight; y++) {
			for (int x = 0; x < newWidth; x++) {
				newSlots[x][y] = new EmptyCustomSlot();
			}
		}
			
		slots = newSlots;
		oldWidth = newWidth;
		oldHeight = newHeight;
	}
	
	public int getWidth() {
		return slots.length;
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
