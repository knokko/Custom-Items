package nl.knokko.customitems.container.slot;

import java.util.function.Function;

import nl.knokko.customitems.container.IndicatorDomain;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.item.CustomItem;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class ProgressIndicatorCustomSlot implements CustomSlot {
	
	public static ProgressIndicatorCustomSlot load1(
			BitInput input, Function<String, CustomItem> itemByName) 
					throws UnknownEncodingException {
		
		SlotDisplay display = SlotDisplay.load(input, itemByName);
		SlotDisplay placeholder = SlotDisplay.load(input, itemByName);
		IndicatorDomain domain = IndicatorDomain.load(input);
		return new ProgressIndicatorCustomSlot(display, placeholder, domain);
	}
	
	private final SlotDisplay display;
	private final SlotDisplay placeholder;
	private final IndicatorDomain domain;
	
	public ProgressIndicatorCustomSlot(SlotDisplay display, SlotDisplay placeholder, 
			IndicatorDomain domain) {
		this.display = display;
		this.placeholder = placeholder;
		this.domain = domain;
	}
	
	@Override
	public void save(BitOutput output) {
		output.addByte(CustomSlot.Encodings.PROGRESS_INDICATOR1);
		display.save(output);
		placeholder.save(output);
		domain.save(output);
	}
	
	public SlotDisplay getDisplay() {
		return display;
	}
	
	public SlotDisplay getPlaceHolder() {
		return placeholder;
	}
	
	public IndicatorDomain getDomain() {
		return domain;
	}

	@Override
	public boolean canInsertItems() {
		return false;
	}

	@Override
	public boolean canTakeItems() {
		return false;
	}

}
