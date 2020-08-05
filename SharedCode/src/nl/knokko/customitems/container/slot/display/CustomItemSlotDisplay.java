package nl.knokko.customitems.container.slot.display;

import java.util.function.Function;

import nl.knokko.customitems.item.CustomItem;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class CustomItemSlotDisplay extends SlotDisplay {
	
	public static CustomItemSlotDisplay load1(BitInput input, 
			Function<String, CustomItem> itemByName) {
		CustomItem item = itemByName.apply(input.readString());
		int amount = input.readInt();
		return new CustomItemSlotDisplay(item, amount);
	}

	private final CustomItem item;
	
	public CustomItemSlotDisplay(CustomItem item, int amount) {
		super(item.getDisplayName(), item.getLore(), amount);
		this.item = item;
	}
	
	@Override
	public void save(BitOutput output) {
		output.addByte(SlotDisplay.Encodings.CUSTOM1);
		output.addString(item.getName());
		output.addInt(amount);
	}
	
	public CustomItem getItem() {
		return item;
	}
}
