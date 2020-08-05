package nl.knokko.customitems.container.slot.display;

import static nl.knokko.customitems.container.slot.display.SlotDisplay.Encodings.CUSTOM1;
import static nl.knokko.customitems.container.slot.display.SlotDisplay.Encodings.DATA_VANILLA1;
import static nl.knokko.customitems.container.slot.display.SlotDisplay.Encodings.SIMPLE_VANILLA1;

import java.util.function.Function;

import nl.knokko.customitems.item.CustomItem;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public abstract class SlotDisplay {
	
	public static SlotDisplay load(
			BitInput input, 
			Function<String, CustomItem> itemByName) throws UnknownEncodingException {
		
		byte encoding = input.readByte();
		switch (encoding) {
		case CUSTOM1: return CustomItemSlotDisplay.load1(input, itemByName);
		case DATA_VANILLA1: return DataVanillaSlotDisplay.load1(input);
		case SIMPLE_VANILLA1: return SimpleVanillaSlotDisplay.load1(input);
		default: throw new UnknownEncodingException("SlotDisplay", encoding);
		}
	}

	protected final String displayName;
	protected final String[] lore;
	
	protected final int amount;
	
	public SlotDisplay(String displayName, String[] lore, int amount) {
		this.displayName = displayName;
		this.lore = lore;
		this.amount = amount;
	}
	
	public abstract void save(BitOutput output);
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String[] getLore() {
		return lore;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public static class Encodings {
		
		public static final byte CUSTOM1 = 0;
		public static final byte DATA_VANILLA1 = 1;
		public static final byte SIMPLE_VANILLA1 = 2;
	}
}
