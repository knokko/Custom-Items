package nl.knokko.customitems.plugin.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import com.google.common.collect.Lists;

import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.container.IndicatorDomain;
import nl.knokko.customitems.container.fuel.FuelEntry;
import nl.knokko.customitems.container.fuel.FuelMode;
import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.container.slot.display.*;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;
import nl.knokko.customitems.plugin.set.item.CustomItem;
import nl.knokko.customitems.plugin.util.ItemUtils;
import nl.knokko.customitems.recipe.ContainerRecipe;
import nl.knokko.customitems.recipe.ContainerRecipe.InputEntry;
import nl.knokko.customitems.recipe.ContainerRecipe.OutputEntry;

/**
 * An in-game instance of a custom container. While the CustomContainer class defines
 * which types of slots and recipes a custom container will have, a ContainerInstance
 * defines which items are currently present in those slots and optionally which
 * (smelting) recipes are currently in progress.
 */
public class ContainerInstance {
	
	@SuppressWarnings("deprecation")
	private static ItemStack fromDisplay(SlotDisplay display) {
		ItemStack stack;
		if (display instanceof CustomItemSlotDisplay) {
			stack = ((CustomItem)((CustomItemSlotDisplay) display).getItem()).create(display.getAmount());
		} else {
			CIMaterial material;
			if (display instanceof DataVanillaSlotDisplay) {
				material = ((DataVanillaSlotDisplay) display).getMaterial();
			} else if (display instanceof SimpleVanillaSlotDisplay) {
				material = ((SimpleVanillaSlotDisplay) display).getMaterial();
			} else {
				throw new Error("Unknown display type: " + display);
			}
			stack = ItemHelper.createStack(material.name(), display.getAmount());
			if (display instanceof DataVanillaSlotDisplay) {
				MaterialData data = stack.getData();
				data.setData(((DataVanillaSlotDisplay) display).getData());
				stack.setData(data);
			}
		}
		
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(display.getDisplayName());
		meta.setLore(Lists.newArrayList(display.getLore()));
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	private static Inventory createInventory(CustomContainer type) {
		Inventory inv = Bukkit.createInventory(null, 9 * type.getHeight(), type.getDisplayName());
		int invIndex = 0;
		for (int y = 0; y < type.getHeight(); y++) {
			for (int x = 0; x < 9; x++) {
				CustomSlot slot = type.getSlot(x, y);
				if (slot instanceof DecorationCustomSlot) {
					SlotDisplay display = ((DecorationCustomSlot) slot).getDisplay();
					ItemStack stack = fromDisplay(display);
					
					inv.setItem(invIndex, stack);
				}
				invIndex++;
			}
		}
		
		return inv;
	}

	private final CustomContainer type;
	
	private final Inventory inventory;
	
	private final Map<String, Integer> inputSlots;
	private final Map<String, Integer> outputSlots;
	
	// Will stay empty if there are no fuel slots
	private final Collection<FuelSlotEntry> fuelSlots;
	
	private final Collection<FuelIndicator> fuelIndicators;
	private final Collection<ProgressIndicator> progressIndicators;
	
	// Will be ignored if the crafting for this container type is instant
	private int currentCraftingProgress;
	
	private ContainerRecipe currentRecipe;
	
	public ContainerInstance(CustomContainer type) {
		this.type = type;
		this.inventory = createInventory(type);
		
		this.inputSlots = new HashMap<>();
		this.outputSlots = new HashMap<>();
		this.fuelSlots = new ArrayList<>();
		
		this.currentCraftingProgress = 0;
		
		this.fuelIndicators = new ArrayList<>();
		this.progressIndicators = new ArrayList<>();
		
		initSlotMaps();
	}
	
	private void initSlotMaps() {
		int invIndex = 0;
		for (int y = 0; y < type.getHeight(); y++) {
			for (int x = 0; x < 9; x++) {
				
				CustomSlot slot = type.getSlot(x, y);
				if (slot instanceof FuelCustomSlot) {
					fuelSlots.add(new FuelSlotEntry((FuelCustomSlot) slot, invIndex));
				} else if (slot instanceof FuelIndicatorCustomSlot) {
					fuelIndicators.add(new FuelIndicator(invIndex, (FuelIndicatorCustomSlot) slot));
				} else if (slot instanceof InputCustomSlot) {
					inputSlots.put(((InputCustomSlot) slot).getName(), invIndex);
				} else if (slot instanceof OutputCustomSlot) {
					outputSlots.put(((OutputCustomSlot) slot).getName(), invIndex);
				} else if (slot instanceof ProgressIndicatorCustomSlot) {
					progressIndicators.add(new ProgressIndicator(invIndex, (ProgressIndicatorCustomSlot) slot));
				}
				invIndex++;
			}
		}
	}
	
	public CustomContainer getType() {
		return type;
	}
	
	public ItemStack getInput(String inputSlotName) {
		return inventory.getItem(inputSlots.get(inputSlotName));
	}
	
	public ItemStack getOutput(String outputSlotName) {
		return inventory.getItem(outputSlots.get(outputSlotName));
	}
	
	public int getCurrentCraftingProgress() {
		return currentCraftingProgress;
	}
	
	public void update() {
		ContainerRecipe oldRecipe = currentRecipe;
		currentRecipe = null;
		
		candidateLoop:
		for (ContainerRecipe candidate : type.getRecipes()) {
			for (InputEntry input : candidate.getInputs()) {
				ItemStack inSlot = getInput(input.inputSlotName);
				Ingredient ingredient = (Ingredient) input.ingredient;
				if (!ingredient.accept(inSlot)) {
					continue candidateLoop;
				}
			}
			
			for (OutputEntry output : candidate.getOutputs()) {
				ItemStack outSlot = getOutput(output.outputSlotName);
				ItemStack result = (ItemStack) output.result;
				if (!ItemUtils.isEmpty(outSlot)) {
					if (!result.isSimilar(outSlot)) {
						continue candidateLoop;
					}
					if (ItemUtils.getMaxStacksize(outSlot) < outSlot.getAmount() + result.getAmount()) {
						continue candidateLoop;
					}
				}
			}
			
			currentRecipe = candidate;
			break;
		}
		
		if (oldRecipe != currentRecipe) {
			currentCraftingProgress = 0;
		}
		
		if (currentRecipe != null) {
			maybeStartBurning();
		}
		
		if (isBurning()) {
			if (currentRecipe != null) {
				currentCraftingProgress++;
				if (currentCraftingProgress >= currentRecipe.getDuration()) {
					
					// Decrease the stacksize of all relevant input slots by 1
					for (InputEntry input : currentRecipe.getInputs()) {
						
						int invIndex = inputSlots.get(input.inputSlotName);
						ItemStack inputItem = inventory.getItem(invIndex);
						inputItem.setAmount(inputItem.getAmount() - 1);
						
						if (inputItem.getAmount() == 0) {
							inputItem = null;
						}
						
						inventory.setItem(invIndex, inputItem);
					}
					
					// Add the results to the output slots
					for (OutputEntry output : currentRecipe.getOutputs()) {
						
						int invIndex = outputSlots.get(output.outputSlotName);
						ItemStack outputItem = inventory.getItem(invIndex);
						ItemStack result = (ItemStack) output.result;
						
						// If the output slot is empty, set its item to the result
						// Otherwise increase its amount
						if (ItemUtils.isEmpty(outputItem)) {
							outputItem = result.clone();
						} else {
							outputItem.setAmount(outputItem.getAmount() + result.getAmount());
						}
						inventory.setItem(invIndex, outputItem);
						
					}
					currentCraftingProgress = 0;
				}

				for (ProgressIndicator indicator : progressIndicators) {
					
					IndicatorDomain domain = indicator.props.getDomain();
					int newStacksize = domain.getStacksize(currentCraftingProgress, currentRecipe.getDuration());
					
					ItemStack newItemStack = fromDisplay(indicator.props.getDisplay());
					newItemStack.setAmount(newStacksize);
					inventory.setItem(indicator.invIndex, newItemStack);
				}
			}
		}
		
		// Always decrease the burn times
		decrementBurnTimes();
	}
	
	private void updateFuelIndicator(FuelSlotEntry fuel) {
		
		for (FuelIndicator indicator : fuelIndicators) {
			
			// Only update the indicators for this fuel slot
			if (indicator.props.getFuelSlotName().equals(fuel.props.getName())) {
				
				ItemStack indicatingStack = fromDisplay(indicator.props.getDisplay());
				IndicatorDomain domain = indicator.props.getDomain();
				int indicatingStacksize = domain.getStacksize(fuel.remainingBurnTime, fuel.maxBurnTime);
				indicatingStack.setAmount(indicatingStacksize);
				
				inventory.setItem(indicator.invIndex, indicatingStack);
			}
		}
	}
	
	private void decrementBurnTimes() {
		for (FuelSlotEntry fuelSlot : fuelSlots) {
			if (fuelSlot.remainingBurnTime > 0) {
				fuelSlot.remainingBurnTime--;
				updateFuelIndicator(fuelSlot);
			}
		}
	}
	
	private boolean isBurning() {
		if (type.getFuelMode() == FuelMode.ALL) {
			
			for (FuelSlotEntry fuel : fuelSlots) {
				if (fuel.remainingBurnTime == 0) {
					return false;
				}
			}
			return true;
			
		} else if (type.getFuelMode() == FuelMode.ANY) {
			
			for (FuelSlotEntry fuel : fuelSlots) {
				if (fuel.remainingBurnTime != 0) {
					return true;
				}
			}
			return false;
			
		} else {
			throw new Error("Unknown FuelMode: " + type.getFuelMode());
		}
	}
	
	private boolean isFuel(FuelSlotEntry slot, ItemStack candidateFuel) {
		return getBurnTime(slot, candidateFuel) != null;
	}
	
	private Integer getBurnTime(FuelSlotEntry slot, ItemStack fuel) {
		for (FuelEntry registryEntry : slot.props.getRegistry().getEntries()) {
			Ingredient ingredient = (Ingredient) registryEntry.getFuel();
			if (ingredient.accept(fuel)) {
				return registryEntry.getBurnTime();
			}
		}
		
		return null;
	}
	
	private boolean canStartBurning() {
		if (type.getFuelMode() == FuelMode.ALL) {
			
			for (FuelSlotEntry fuel : fuelSlots) {
				
				// If this slot is not yet burning, check if it contains fuel
				if (fuel.remainingBurnTime == 0) {
					ItemStack fuelStack = inventory.getItem(fuel.invIndex);
					
					// If this slot doesn't contain proper fuel, it's lost
					if (!isFuel(fuel, fuelStack)) {
						return false;
					}
				}
				// If it's already burning, this entry is not a bottleneck
			}
			
			// If we reach this line, all fuel slots are either burning or have fuel
			return true;
		} else if (type.getFuelMode() == FuelMode.ANY) {
			
			for (FuelSlotEntry fuel : fuelSlots) {
				
				// If it's already burning, we are already done
				if (fuel.remainingBurnTime != 0) {
					return true;
				}
				
				// Check if it could start burning
				ItemStack fuelCandidate = inventory.getItem(fuel.invIndex);
				if (isFuel(fuel, fuelCandidate)) {
					return true;
				}
			}
			
			// If not a single candidate was found, return false
			return false;
		} else {
			throw new Error("Unknown FuelMode: " + type.getFuelMode());
		}
	}
	
	/**
	 * Should only be called when canStartBurning() returned true
	 */
	private void startBurning() {
		if (type.getFuelMode() == FuelMode.ALL) {
			
			// Make sure all fuel slots are burning
			for (FuelSlotEntry fuel : fuelSlots) {
				
				// Start burning if it isn't burning yet
				if (fuel.remainingBurnTime == 0) {
					
					ItemStack fuelStack = inventory.getItem(fuel.invIndex);
					fuel.remainingBurnTime = getBurnTime(fuel, fuelStack);
					fuel.maxBurnTime = fuel.remainingBurnTime;
					fuelStack.setAmount(fuelStack.getAmount() - 1);
					
					inventory.setItem(fuel.invIndex, fuelStack);
					updateFuelIndicator(fuel);
				}
			}
		} else if (type.getFuelMode() == FuelMode.ANY) {
			
			// Start burning the first best valid fuel
			for (FuelSlotEntry fuel : fuelSlots) {
				
				ItemStack fuelStack = inventory.getItem(fuel.invIndex);
				Integer burnTime = getBurnTime(fuel, fuelStack);
				if (burnTime != null) {
					fuel.remainingBurnTime = burnTime;
					fuel.maxBurnTime = burnTime;
					fuelStack.setAmount(fuelStack.getAmount() - 1);
					
					inventory.setItem(fuel.invIndex, fuelStack);
					updateFuelIndicator(fuel);
					return;
				}
			}
			
			throw new IllegalStateException("Couldn't start burning " + type.getName());
		} else {
			throw new Error("Unknown FuelMode: " + type.getFuelMode());
		}
	}
	
	/**
	 * Starts burning if there is enough fuel and this instance is not yet burning
	 */
	private void maybeStartBurning() {
		
		// Only proceed if we are not yet burning
		if (!isBurning()) {
			
			// Only proceed if we have enough fuel to start
			if (canStartBurning()) {
				startBurning();
			}
		}
	}
	
	private static class FuelSlotEntry {
		
		final FuelCustomSlot props;
		final int invIndex;
		
		int remainingBurnTime;
		int maxBurnTime;
		
		FuelSlotEntry(FuelCustomSlot props, int invIndex) {
			this.props = props;
			this.invIndex = invIndex;
			this.remainingBurnTime = 0;
			this.maxBurnTime = 0;
		}
	}
	
	private static class FuelIndicator {
		
		final int invIndex;
		final FuelIndicatorCustomSlot props;
		
		FuelIndicator(int invIndex, FuelIndicatorCustomSlot props) {
			this.invIndex = invIndex;
			this.props = props;
		}
	}
	
	private static class ProgressIndicator {
		
		final int invIndex;
		final ProgressIndicatorCustomSlot props;
		
		ProgressIndicator(int invIndex, ProgressIndicatorCustomSlot props) {
			this.invIndex = invIndex;
			this.props = props;
		}
	}
}
