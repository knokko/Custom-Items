package nl.knokko.customitems.plugin.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
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
import nl.knokko.customitems.container.slot.display.CustomItemDisplayItem;
import nl.knokko.customitems.container.slot.display.DataVanillaDisplayItem;
import nl.knokko.customitems.container.slot.display.SimpleVanillaDisplayItem;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.plugin.container.ContainerInfo.DecorationProps;
import nl.knokko.customitems.plugin.container.ContainerInfo.IndicatorProps;
import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;
import nl.knokko.customitems.plugin.set.item.CustomItem;
import nl.knokko.customitems.plugin.util.ItemUtils;
import nl.knokko.customitems.recipe.ContainerRecipe;
import nl.knokko.customitems.recipe.ContainerRecipe.InputEntry;
import nl.knokko.customitems.recipe.ContainerRecipe.OutputEntry;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

/**
 * An in-game instance of a custom container. While the CustomContainer class defines
 * which types of slots and recipes a custom container will have, a ContainerInstance
 * defines which items are currently present in those slots and optionally which
 * (smelting) recipes are currently in progress.
 */
public class ContainerInstance {
	
	@SuppressWarnings("deprecation")
	public static ItemStack fromDisplay(SlotDisplay display) {
		ItemStack stack;
		if (display.getItem() instanceof CustomItemDisplayItem) {
			stack = ((CustomItem)((CustomItemDisplayItem) display.getItem()).getItem()).create(display.getAmount());
		} else {
			CIMaterial material;
			if (display.getItem() instanceof DataVanillaDisplayItem) {
				material = ((DataVanillaDisplayItem) display.getItem()).getMaterial();
			} else if (display.getItem() instanceof SimpleVanillaDisplayItem) {
				material = ((SimpleVanillaDisplayItem) display.getItem()).getMaterial();
			} else {
				throw new Error("Unknown display type: " + display);
			}
			stack = ItemHelper.createStack(material.name(), display.getAmount());
			if (display.getItem() instanceof DataVanillaDisplayItem) {
				MaterialData data = stack.getData();
				data.setData(((DataVanillaDisplayItem) display.getItem()).getData());
				stack.setData(data);
				stack.setDurability(data.getData());
			}
		}
		
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(display.getDisplayName());
		meta.setLore(Lists.newArrayList(display.getLore()));
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	private static Inventory createInventory(ContainerInfo typeInfo) {
		CustomContainer container = typeInfo.getContainer();
		Inventory inv = Bukkit.createInventory(null, 9 * container.getHeight(), 
				container.getSelectionIcon().getDisplayName());
		
		for (DecorationProps decoration : typeInfo.getDecorations()) {
			ItemStack stack = fromDisplay(decoration.getSlotDisplay());
			inv.setItem(decoration.getInventoryIndex(), stack);
		}
		for (IndicatorProps indicator : typeInfo.getCraftingIndicators()) {
			inv.setItem(indicator.getInventoryIndex(), fromDisplay(indicator.getPlaceholder()));
		}
		typeInfo.getFuelSlots().forEach(entry -> {
			for (IndicatorProps indicator : typeInfo.getFuelIndicators(entry.getKey())) {
				inv.setItem(indicator.getInventoryIndex(), fromDisplay(indicator.getPlaceholder()));
			}
		});
		
		return inv;
	}
	
	public static void discard1(BitInput input) {
		
		// TODO Perhaps drop the items rather than discarding them
		// Discard the item stacks for all 3 slot types: input, output and fuel
		for (int slotTypeCounter = 0; slotTypeCounter < 3; slotTypeCounter++) {
			
			// Discard all their slots
			int numSlots = input.readInt();
			for (int slotCounter = 0; slotCounter < numSlots; slotCounter++) {
				// Discard 2 strings: the name of the slot and the string 
				// representation of the ItemStack it contained
				input.readString();
				input.readString();
			}
		}
		
		// Discard the remaining fuel burn times
		int numBurningFuelSlots = input.readInt();
		for (int counter = 0; counter < numBurningFuelSlots; counter++) {
			
			// Discard slot name (String), remaining burn time and max burn time (int)
			input.readString();
			input.readInt();
			input.readInt();
		}
		
		// Discard remaining crafting progress
		input.readInt();
		// Discard stored experience
		input.readInt();
	}
	
	public static ContainerInstance load1(BitInput input, ContainerInfo typeInfo) {
		
		ContainerInstance instance = new ContainerInstance(typeInfo);
		Inventory inv = instance.inventory;
		
		class StringStack {
			
			final String slotName;
			final ItemStack stack;
			
			StringStack(String slotName, ItemStack stack) {
				this.slotName = slotName;
				this.stack = stack;
			}
			
			private boolean putSimple(Function<String, Integer> getSlot, Inventory dest) {
				Integer rightSlot = getSlot.apply(slotName);
				if (rightSlot != null) {
					dest.setItem(rightSlot, stack);
					return true;
				} else {
					return false;
				}
			}
			
			private void putInEmptySlot(
					Iterable<Integer> firstSlots, Iterable<Integer> secondSlots,
					Iterable<Integer> thirdSlots) {
				
				// Use the preferred/first slots whenever possible
				for (Integer slotIndex : firstSlots) {
					if (ItemUtils.isEmpty(inv.getItem(slotIndex))) {
						inv.setItem(slotIndex, stack);
						return;
					}
				}
				
				// Fall back to the second choice type of slots
				for (int fuelSlot : secondSlots) {
					if (ItemUtils.isEmpty(inv.getItem(fuelSlot))) {
						inv.setItem(fuelSlot, stack);
						return;
					}
				}
				
				// The third choice type slots are the last resort
				for (Integer slotIndex : thirdSlots) {
					if (ItemUtils.isEmpty(inv.getItem(slotIndex))) {
						inv.setItem(slotIndex, stack);
						return;
					}
				}
				
				// If this line is reached, this item will be discarded
				// I don't know a better way to resolve this
			}
		}
		
		class FuelEntry {
			
			final String slotName;
			final int remainingBurnTime;
			final int fullBurnTime;
			
			FuelEntry(String slotName, int remainingBurnTime, int fullBurnTime) {
				this.slotName = slotName;
				this.remainingBurnTime = remainingBurnTime;
				this.fullBurnTime = fullBurnTime;
			}
		}
		
		class SlotReader {
			
			Collection<StringStack> readSlots(BitInput input) {
				
				int numNonEmptySlots = input.readInt();
				Collection<StringStack> slotStacks = new ArrayList<>();
				for (int counter = 0; counter < numNonEmptySlots; counter++) {
					String slotName = input.readString();
					String itemStackString = input.readString();
					
					YamlConfiguration dummyConfig = new YamlConfiguration();
					try {
						dummyConfig.loadFromString(itemStackString);
					} catch (InvalidConfigurationException e) {
						throw new IllegalArgumentException("Bad item stack string: " + itemStackString);
					}
					ItemStack slotStack = dummyConfig.getItemStack("theStack");
					
					slotStacks.add(new StringStack(slotName, slotStack));
				}
				
				return slotStacks;
			}
		}
		
		SlotReader sr = new SlotReader();
		Collection<StringStack> inputStacks = sr.readSlots(input);
		Collection<StringStack> outputStacks = sr.readSlots(input);
		Collection<StringStack> fuelStacks = sr.readSlots(input);
		
		int numBurningFuelSlots = input.readInt();
		Collection<FuelEntry> fuelBurnDurations = new ArrayList<>(numBurningFuelSlots);
		for (int counter = 0; counter < numBurningFuelSlots; counter++) {
			
			String fuelSlotName = input.readString();
			int remainingBurnTime = input.readInt();
			int fullBurnTime = input.readInt();
			fuelBurnDurations.add(new FuelEntry(fuelSlotName, remainingBurnTime, fullBurnTime));
		}
		
		int craftingProgress = input.readInt();
		
		int storedExperience = input.readInt();
		
		/* Now that we have gathered all information, it's time to distribute the
		 * loaded item stacks over the container slots. This process is complex
		 * because the container might not have the exact same configuration as the
		 * moment when it was saved: the server owner might have changed it
		 * (via the editor) in the meantime.
		 */
		
		// But first the simple cases
		inputStacks.removeIf(inputStack -> inputStack.putSimple(typeInfo::getInputSlotIndex, inv));
		outputStacks.removeIf(outputStack -> outputStack.putSimple(typeInfo::getOutputSlotIndex, inv));
		fuelStacks.removeIf(fuelStack -> fuelStack.putSimple(typeInfo::getFuelSlotIndex, inv));
		
		// Now the annoying cases where the slot is changed or renamed
		for (StringStack inputStack : inputStacks) {
			inputStack.putInEmptySlot(
					takeValues(typeInfo.getInputSlots()), 
					takeValues(typeInfo.getOutputSlots()), 
					takeValues(typeInfo.getFuelSlots())
			);
		}
		
		for (StringStack outputStack : outputStacks) {
			outputStack.putInEmptySlot(
					takeValues(typeInfo.getOutputSlots()), 
					takeValues(typeInfo.getFuelSlots()), 
					takeValues(typeInfo.getInputSlots())
			);
		}
		
		for (StringStack fuelStack : fuelStacks) {
			fuelStack.putInEmptySlot(
					takeValues(typeInfo.getFuelSlots()), 
					takeValues(typeInfo.getOutputSlots()), 
					takeValues(typeInfo.getInputSlots())
			);
		}
		
		/*
		 * If fuel slots are renamed, the fuel burn time is hard to load as well.
		 * The trick of finding another fuel slot could be used here, but this can
		 * cause the situation where the burn time of one fuel slot would be given
		 * to another fuel slot. If that other fuel slot is very hard to get burning,
		 * this could give a very powerful advantage to a player.
		 * 
		 * I think simply discarding the burn time of renamed fuel slots is the best
		 * option here.
		 */
		for (FuelEntry fuel : fuelBurnDurations) {
			FuelBurnEntry entry = instance.fuelSlots.get(fuel.slotName);
			if (entry != null) {
				entry.remainingBurnTime = fuel.remainingBurnTime;
				entry.maxBurnTime = fuel.fullBurnTime;
			} else {
				Bukkit.getLogger().warning("Discarding burn duration for fuel slot "+ fuel.slotName + " for container " + instance.typeInfo.getContainer().getName());
			}
		}
		
		// This one is simple
		instance.currentCraftingProgress = craftingProgress;
		instance.storedExperience = storedExperience;
		
		return instance;
	}
	
	private static <T> Iterable<Integer> takeValues(Iterable<Entry<T, Integer>> entries) {
		return StreamSupport.stream(entries.spliterator(), false).map(entry -> entry.getValue())::iterator;
	}

	private final ContainerInfo typeInfo;
	
	private final Inventory inventory;
	
	// Will stay empty if there are no fuel slots
	private final Map<String, FuelBurnEntry> fuelSlots;
	
	// Will be ignored if the crafting for this container type is instant
	private int currentCraftingProgress;
	
	private ContainerRecipe currentRecipe;
	
	private int storedExperience;
	
	public ContainerInstance(ContainerInfo typeInfo) {
		if (typeInfo == null) throw new NullPointerException("typeInfo");
		this.typeInfo = typeInfo;
		this.inventory = createInventory(typeInfo);
		
		this.fuelSlots = new HashMap<>();
		this.initFuelSlots();
		
		this.currentCraftingProgress = 0;
	}
	
	public int getStoredExperience() {
		return storedExperience;
	}
	
	public void clearStoredExperience() {
		storedExperience = 0;
	}
	
	private void initFuelSlots() {
		for (Entry<String, Integer> fuelSlot : typeInfo.getFuelSlots()) {
			fuelSlots.put(fuelSlot.getKey(), new FuelBurnEntry());
		}
	}
	
	public void save1(BitOutput output) {
		
		// Since the container layout of type could change, we need to be careful
		// We will store entries of (slotName, itemStack) to store all items
		BiConsumer<String, Integer> stackSaver = (slotName, invIndex) -> {
			output.addString(slotName);
			ItemStack itemStack = inventory.getItem(invIndex);
			
			// Serializing item stacks is a little effort
			YamlConfiguration dummyConfiguration = new YamlConfiguration();
			dummyConfiguration.set("theStack", itemStack);
			output.addString(dummyConfiguration.saveToString());
		};
		
		Consumer<Iterable<Entry<String, Integer>>> slotsSaver = collection -> {
			
			int numNonEmptySlots = 0;
			for (Entry<String, Integer> entry : collection) {
				ItemStack stack = inventory.getItem(entry.getValue());
				if (!ItemUtils.isEmpty(stack)) {
					numNonEmptySlots++;
				}
			}
			
			output.addInt(numNonEmptySlots);
			for (Entry<String, Integer> entry : collection) {
				ItemStack stack = inventory.getItem(entry.getValue());
				if (!ItemUtils.isEmpty(stack)) {
					stackSaver.accept(entry.getKey(), entry.getValue());
				}
			}
		};
		
		slotsSaver.accept(typeInfo.getInputSlots());
		slotsSaver.accept(typeInfo.getOutputSlots());
		slotsSaver.accept(typeInfo.getFuelSlots());
		
		int numBurningFuelSlots = 0;
		for (FuelBurnEntry burn : fuelSlots.values()) {
			if (burn.remainingBurnTime != 0) {
				numBurningFuelSlots++;
			}
		}
		
		// The fuel slots have a bit more data to store
		output.addInt(numBurningFuelSlots);
		fuelSlots.forEach((slotName, fuel) -> {
			if (fuel.remainingBurnTime != 0) {
				output.addString(slotName);
				output.addInt(fuel.remainingBurnTime);
				output.addInt(fuel.maxBurnTime);
			}
		});
		
		// Finally, we need to store the progress within the current crafting recipe
		output.addInt(currentCraftingProgress);
		
		// And the experience that is currently stored
		output.addInt(storedExperience);
		
		// We don't need to store for which recipe that progress is, because it can
		// be derived from the contents of the inventory slots
	}
	
	public CustomContainer getType() {
		return typeInfo.getContainer();
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	
	public void dropAllItems(Location location) {
		dropAllItems(location, typeInfo.getInputSlots());
		dropAllItems(location, typeInfo.getOutputSlots());
		dropAllItems(location, typeInfo.getFuelSlots());
	}
	
	private void dropAllItems(Location location, Iterable<Entry<String, Integer>> slots) {
		slots.forEach(entry -> {
			ItemStack stack = inventory.getItem(entry.getValue());
			if (!ItemUtils.isEmpty(stack)) {
				location.getWorld().dropItem(location, stack);
				inventory.setItem(entry.getValue(), null);
			}
		});
	}
	
	/**
	 * @return true if at least 1 of the fuel slots is burning
	 */
	public boolean isAnySlotBurning() {
		for (FuelBurnEntry entry : fuelSlots.values()) {
			if (entry.remainingBurnTime != 0) {
				return true;
			}
		}
		
		return false;
	}
	
	public ItemStack getInput(String inputSlotName) {
		return inventory.getItem(typeInfo.getInputSlotIndex(inputSlotName));
	}
	
	public ItemStack getOutput(String outputSlotName) {
		return inventory.getItem(typeInfo.getOutputSlotIndex(outputSlotName));
	}
	
	public ItemStack getFuel(String fuelSlotName) {
		return inventory.getItem(typeInfo.getFuelSlotIndex(fuelSlotName));
	}
	
	public void setInput(String inputSlotName, ItemStack newStack) {
		inventory.setItem(typeInfo.getInputSlotIndex(inputSlotName), newStack);
	}
	
	public void setOutput(String outputSlotName, ItemStack newStack) {
		inventory.setItem(typeInfo.getOutputSlotIndex(outputSlotName), newStack);
	}
	
	public void setFuel(String fuelSlotName, ItemStack newStack) {
		inventory.setItem(typeInfo.getFuelSlotIndex(fuelSlotName), newStack);
	}
	
	public int getCurrentCraftingProgress() {
		return currentCraftingProgress;
	}
	
	public void update() {
		ContainerRecipe oldRecipe = currentRecipe;
		currentRecipe = null;
		
		candidateLoop:
		for (ContainerRecipe candidate : typeInfo.getContainer().getRecipes()) {
			for (InputEntry input : candidate.getInputs()) {
				ItemStack inSlot = getInput(input.getInputSlotName());
				Ingredient ingredient = (Ingredient) input.getIngredient();
				if (!ingredient.accept(inSlot)) {
					continue candidateLoop;
				}
			}
			
			for (OutputEntry output : candidate.getOutputs()) {
				ItemStack outSlot = getOutput(output.getOutputSlotName());
				ItemStack result = (ItemStack) output.getResult();
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
		
		int oldCraftingProgress = currentCraftingProgress;
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
						
						int invIndex = typeInfo.getInputSlotIndex(input.getInputSlotName());
						ItemStack inputItem = inventory.getItem(invIndex);
						inputItem.setAmount(inputItem.getAmount() - 1);
						
						if (inputItem.getAmount() == 0) {
							inputItem = null;
						}
						
						inventory.setItem(invIndex, inputItem);
					}
					
					// Add the results to the output slots
					for (OutputEntry output : currentRecipe.getOutputs()) {
						
						int invIndex = typeInfo.getOutputSlotIndex(output.getOutputSlotName());
						ItemStack outputItem = inventory.getItem(invIndex);
						ItemStack result = (ItemStack) output.getResult();
						
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
					storedExperience += currentRecipe.getExperience();
				}
			}
		}
		
		if (oldCraftingProgress != currentCraftingProgress) {
			for (IndicatorProps indicator : typeInfo.getCraftingIndicators()) {
				
				if (currentCraftingProgress > 0) {
					IndicatorDomain domain = indicator.getIndicatorDomain();
					int newStacksize = domain.getStacksize(currentCraftingProgress, currentRecipe.getDuration());
					
					ItemStack newItemStack = fromDisplay(indicator.getSlotDisplay());
					newItemStack.setAmount(newStacksize);
					inventory.setItem(indicator.getInventoryIndex(), newItemStack);
				} else {
					inventory.setItem(
							indicator.getInventoryIndex(), 
							fromDisplay(indicator.getPlaceholder()
					));
				}
			}
		}
		
		// Always decrease the burn times
		decrementBurnTimes();
	}
	
	private void updateFuelIndicator(String fuelSlotName) {
		
		for (IndicatorProps indicator : typeInfo.getFuelIndicators(fuelSlotName)) {
			
			ItemStack indicatingStack = fromDisplay(indicator.getSlotDisplay());
			IndicatorDomain domain = indicator.getIndicatorDomain();
			
			FuelBurnEntry burn = fuelSlots.get(fuelSlotName);
			int indicatingStacksize = domain.getStacksize(burn.remainingBurnTime, burn.maxBurnTime);
			if (indicatingStacksize > 0) {
				indicatingStack.setAmount(indicatingStacksize);
			} else {
				indicatingStack = fromDisplay(indicator.getPlaceholder());
			}
			
			inventory.setItem(indicator.getInventoryIndex(), indicatingStack);
		}
	}
	
	private void decrementBurnTimes() {
		for (Entry<String, FuelBurnEntry> burnEntry : fuelSlots.entrySet()) {
			FuelBurnEntry burn = burnEntry.getValue();
			String fuelSlotName = burnEntry.getKey();
			if (burn.remainingBurnTime > 0) {
				burn.remainingBurnTime--;
				updateFuelIndicator(fuelSlotName);
			}
		}
	}
	
	private boolean isBurning() {
		if (typeInfo.getContainer().getFuelMode() == FuelMode.ALL) {
			
			for (FuelBurnEntry fuel : fuelSlots.values()) {
				if (fuel.remainingBurnTime == 0) {
					return false;
				}
			}
			return true;
			
		} else if (typeInfo.getContainer().getFuelMode() == FuelMode.ANY) {
			
			for (FuelBurnEntry fuel : fuelSlots.values()) {
				if (fuel.remainingBurnTime != 0) {
					return true;
				}
			}
			return false;
			
		} else {
			throw new Error("Unknown FuelMode: " + typeInfo.getContainer().getFuelMode());
		}
	}
	
	private boolean isFuel(String fuelSlotName, FuelBurnEntry slot, ItemStack candidateFuel) {
		return getBurnTime(fuelSlotName, slot, candidateFuel) != null;
	}
	
	private Integer getBurnTime(String fuelSlotName, FuelBurnEntry slot, ItemStack fuel) {
		for (FuelEntry registryEntry : typeInfo.getFuelRegistry(fuelSlotName)) {
			Ingredient ingredient = (Ingredient) registryEntry.getFuel();
			if (ingredient.accept(fuel)) {
				return registryEntry.getBurnTime();
			}
		}
		
		return null;
	}
	
	private boolean canStartBurning() {
		if (typeInfo.getContainer().getFuelMode() == FuelMode.ALL) {
			
			for (Entry<String, FuelBurnEntry> fuelEntry : fuelSlots.entrySet()) {
				
				String fuelSlotName = fuelEntry.getKey();
				FuelBurnEntry fuel = fuelEntry.getValue();
				
				// If this slot is not yet burning, check if it contains fuel
				if (fuel.remainingBurnTime == 0) {
					ItemStack fuelStack = getFuel(fuelSlotName);
					
					// If this slot doesn't contain proper fuel, it's lost
					if (!isFuel(fuelSlotName, fuel, fuelStack)) {
						return false;
					}
				}
				// If it's already burning, this entry is not a bottleneck
			}
			
			// If we reach this line, all fuel slots are either burning or have fuel
			return true;
		} else if (typeInfo.getContainer().getFuelMode() == FuelMode.ANY) {
			
			for (Entry<String, FuelBurnEntry> fuelEntry : fuelSlots.entrySet()) {
				
				String fuelSlotName = fuelEntry.getKey();
				FuelBurnEntry fuel = fuelEntry.getValue();
				
				// If it's already burning, we are already done
				if (fuel.remainingBurnTime != 0) {
					return true;
				}
				
				// Check if it could start burning
				ItemStack fuelCandidate = getFuel(fuelSlotName);
				if (isFuel(fuelSlotName, fuel, fuelCandidate)) {
					return true;
				}
			}
			
			// If not a single candidate was found, return false
			return false;
		} else {
			throw new Error("Unknown FuelMode: " + typeInfo.getContainer().getFuelMode());
		}
	}
	
	/**
	 * Should only be called when canStartBurning() returned true
	 */
	private void startBurning() {
		if (typeInfo.getContainer().getFuelMode() == FuelMode.ALL) {
			
			// Make sure all fuel slots are burning
			for (Entry<String, FuelBurnEntry> fuelEntry : fuelSlots.entrySet()) {
				
				String fuelSlotName = fuelEntry.getKey();
				FuelBurnEntry fuel = fuelEntry.getValue();
				
				// Start burning if it isn't burning yet
				if (fuel.remainingBurnTime == 0) {
					
					ItemStack fuelStack = getFuel(fuelSlotName);
					fuel.remainingBurnTime = getBurnTime(fuelSlotName, fuel, fuelStack);
					fuel.maxBurnTime = fuel.remainingBurnTime;
					fuelStack.setAmount(fuelStack.getAmount() - 1);
					
					setFuel(fuelSlotName, fuelStack);
					updateFuelIndicator(fuelSlotName);
				}
			}
		} else if (typeInfo.getContainer().getFuelMode() == FuelMode.ANY) {
			
			// Start burning the first best valid fuel
			for (Entry<String, FuelBurnEntry> fuelEntry : fuelSlots.entrySet()) {
				
				String fuelSlotName = fuelEntry.getKey();
				FuelBurnEntry fuel = fuelEntry.getValue();
				
				ItemStack fuelStack = getFuel(fuelSlotName);
				Integer burnTime = getBurnTime(fuelSlotName, fuel, fuelStack);
				if (burnTime != null) {
					fuel.remainingBurnTime = burnTime;
					fuel.maxBurnTime = burnTime;
					fuelStack.setAmount(fuelStack.getAmount() - 1);
					
					setFuel(fuelSlotName, fuelStack);
					updateFuelIndicator(fuelSlotName);
					return;
				}
			}
			
			throw new IllegalStateException("Couldn't start burning " + typeInfo.getContainer().getName());
		} else {
			throw new Error("Unknown FuelMode: " + typeInfo.getContainer().getFuelMode());
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
	
	private static class FuelBurnEntry {
		
		int remainingBurnTime;
		int maxBurnTime;
		
		FuelBurnEntry() {
			this.remainingBurnTime = 0;
			this.maxBurnTime = 0;
		}
	}
}
