package nl.knokko.customitems.container.slot;

import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class InputCustomSlot implements CustomSlot {
	
	public static InputCustomSlot load1(BitInput input) {
		String name = input.readString();
		return new InputCustomSlot(name);
	}
	
	private final String name;
	
	public InputCustomSlot(String name) {
		this.name = name;
	}
	
	@Override
	public void save(BitOutput output) {
		output.addByte(CustomSlot.Encodings.INPUT1);
		output.addString(name);
	}
	
	public String getName() {
		return name;
	}

	@Override
	public boolean canInsertItems() {
		return true;
	}

	@Override
	public boolean canTakeItems() {
		return true;
	}
}
