package nl.knokko.customitems.container.slot;

import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import static nl.knokko.customitems.container.slot.CustomSlot.Encodings.*;

import java.util.function.Function;

import nl.knokko.customitems.container.fuel.CustomFuelRegistry;
import nl.knokko.customitems.item.CustomItem;
import nl.knokko.customitems.trouble.UnknownEncodingException;

public interface CustomSlot {
	
	static CustomSlot load(
			BitInput input, 
			Function<String, CustomItem> itemByName,
			Function<String, CustomFuelRegistry> fuelRegistryByName) 
					throws UnknownEncodingException {
		
		byte encoding = input.readByte();
		switch (encoding) {
		case DECORATION1: return DecorationCustomSlot.load1(input, itemByName);
		case EMPTY: return new EmptyCustomSlot();
		case FUEL1: return FuelCustomSlot.load1(input, fuelRegistryByName);
		case FUEL_INDICATOR1: return FuelIndicatorCustomSlot.load1(input, itemByName);
		case INPUT1: return InputCustomSlot.load1(input);
		case OUTPUT1: return OutputCustomSlot.load1(input);
		case PROGRESS_INDICATOR1: return ProgressIndicatorCustomSlot.load1(input, itemByName);
		default: throw new UnknownEncodingException("CustomSlot", encoding);
		}
	}

	boolean canInsertItems();
	
	boolean canTakeItems();
	
	CustomSlot safeClone(CustomSlot[][] existingSlots);
	
	void save(BitOutput output);
	
	static class Encodings {
		
		public static final byte DECORATION1 = 0;
		public static final byte EMPTY = 1;
		public static final byte FUEL1 = 2;
		public static final byte FUEL_INDICATOR1 = 3;
		public static final byte INPUT1 = 4;
		public static final byte OUTPUT1 = 5;
		public static final byte PROGRESS_INDICATOR1 = 6;
	}
}
