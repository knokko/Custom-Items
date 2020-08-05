package nl.knokko.customitems.plugin.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.container.IndicatorDomain;
import nl.knokko.customitems.container.fuel.CustomFuelRegistry;
import nl.knokko.customitems.container.fuel.FuelEntry;
import nl.knokko.customitems.container.slot.CustomSlot;
import nl.knokko.customitems.container.slot.DecorationCustomSlot;
import nl.knokko.customitems.container.slot.FuelCustomSlot;
import nl.knokko.customitems.container.slot.FuelIndicatorCustomSlot;
import nl.knokko.customitems.container.slot.InputCustomSlot;
import nl.knokko.customitems.container.slot.OutputCustomSlot;
import nl.knokko.customitems.container.slot.ProgressIndicatorCustomSlot;
import nl.knokko.customitems.container.slot.display.SlotDisplay;

/**
 * This class captures information about a given custom container that is useful for
 * managing the inventories based on the container.
 */
public class ContainerInfo {

	private final CustomContainer container;
	
	private final Map<String, Integer> inputSlots;
	private final Map<String, Integer> outputSlots;
	private final Map<String, Integer> fuelSlots;
	
	private final Collection<IndicatorProps> craftingIndicators;
	private final Map<String, Collection<IndicatorProps>> fuelIndicators;
	private final Map<String, CustomFuelRegistry> fuelRegistries;
	
	private final Collection<DecorationProps> decorations;
	
	public ContainerInfo(CustomContainer container) {
		this.container = container;
		
		this.inputSlots = new HashMap<>();
		this.outputSlots = new HashMap<>();
		this.fuelSlots = new HashMap<>();
		this.craftingIndicators = new ArrayList<>();
		this.fuelIndicators = new HashMap<>();
		this.fuelRegistries = new HashMap<>();
		this.decorations = new ArrayList<>();
		
		int invIndex = 0;
		for (int y = 0; y < container.getHeight(); y++) {
			for (int x = 0; x < 9; x++) {
				
				CustomSlot slot = container.getSlot(x, y);
				if (slot instanceof FuelCustomSlot) {
					FuelCustomSlot fuelSlot = (FuelCustomSlot) slot;
					fuelSlots.put(fuelSlot.getName(), invIndex);
					System.out.println("The name of the fuelSlot is " + fuelSlot.getName());
					fuelRegistries.put(fuelSlot.getName(), fuelSlot.getRegistry());
				} else if (slot instanceof FuelIndicatorCustomSlot) {
					FuelIndicatorCustomSlot indicatorSlot = (FuelIndicatorCustomSlot) slot;
					Collection<IndicatorProps> indicators = fuelIndicators.get(indicatorSlot.getFuelSlotName());
					if (indicators == null) {
						indicators = new ArrayList<>(1);
						fuelIndicators.put(indicatorSlot.getFuelSlotName(), indicators);
					}
					indicators.add(new IndicatorProps(invIndex, 
							indicatorSlot.getDisplay(), indicatorSlot.getPlaceholder(), 
							indicatorSlot.getDomain()
					));
				} else if (slot instanceof InputCustomSlot) {
					inputSlots.put(((InputCustomSlot) slot).getName(), invIndex);
				} else if (slot instanceof OutputCustomSlot) {
					outputSlots.put(((OutputCustomSlot) slot).getName(), invIndex);
				} else if (slot instanceof ProgressIndicatorCustomSlot) {
					ProgressIndicatorCustomSlot indicatorSlot = (ProgressIndicatorCustomSlot) slot;
					craftingIndicators.add(new IndicatorProps(invIndex, 
							indicatorSlot.getDisplay(), indicatorSlot.getPlaceHolder(),
							indicatorSlot.getDomain()
					));
				} else if (slot instanceof DecorationCustomSlot) {
					decorations.add(new DecorationProps(invIndex, ((DecorationCustomSlot) slot).getDisplay()));
				}
				invIndex++;
			}
		}
	}
	
	public CustomContainer getContainer() {
		return container;
	}
	
	public Integer getInputSlotIndex(String slotName) {
		return inputSlots.get(slotName);
	}
	
	public Integer getOutputSlotIndex(String slotName) {
		return outputSlots.get(slotName);
	}
	
	public Integer getFuelSlotIndex(String slotName) {
		return fuelSlots.get(slotName);
	}
	
	public Iterable<IndicatorProps> getCraftingIndicators() {
		return craftingIndicators;
	}
	
	public Iterable<IndicatorProps> getFuelIndicators(String fuelSlotName) {
		Collection<IndicatorProps> result = fuelIndicators.get(fuelSlotName);
		if (result == null) 
			throw new IllegalArgumentException("Bad fuelSlotName " + fuelSlotName);
		return result;
	}
	
	public Iterable<FuelEntry> getFuelRegistry(String fuelSlotName) {
		return fuelRegistries.get(fuelSlotName).getEntries();
	}
	
	public Iterable<DecorationProps> getDecorations() {
		return decorations;
	}
	
	public Iterable<Entry<String, Integer>> getInputSlots() {
		return inputSlots.entrySet();
	}
	
	public Iterable<Entry<String, Integer>> getOutputSlots() {
		return outputSlots.entrySet();
	}
	
	public Iterable<Entry<String, Integer>> getFuelSlots() {
		return fuelSlots.entrySet();
	}
	
	public static class IndicatorProps {
		
		private final int invIndex;
		
		private final SlotDisplay display;
		private final SlotDisplay placeholder;
		private final IndicatorDomain domain;
		
		private IndicatorProps(int invIndex, SlotDisplay display, 
				SlotDisplay placeholder, IndicatorDomain domain) {
			this.invIndex = invIndex;
			this.display = display;
			this.placeholder = placeholder;
			this.domain = domain;
		}
		
		public int getInventoryIndex() {
			return invIndex;
		}
		
		public SlotDisplay getSlotDisplay() {
			return display;
		}
		
		public SlotDisplay getPlaceholder() {
			return placeholder;
		}
		
		public IndicatorDomain getIndicatorDomain() {
			return domain;
		}
	}
	
	public static class DecorationProps {
		
		private final int invIndex;
		
		private final SlotDisplay display;
		
		private DecorationProps(int invIndex, SlotDisplay display) {
			this.invIndex = invIndex;
			this.display = display;
		}
		
		public int getInventoryIndex() {
			return invIndex;
		}
		
		public SlotDisplay getSlotDisplay() {
			return display;
		}
	}
}
