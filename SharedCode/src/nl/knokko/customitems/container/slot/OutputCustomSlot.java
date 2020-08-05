package nl.knokko.customitems.container.slot;

import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class OutputCustomSlot implements CustomSlot {
	
	public static OutputCustomSlot load1(BitInput input) {
		String name = input.readString();
		return new OutputCustomSlot(name);
	}
	
	private final String name;
	
	public OutputCustomSlot(String name) {
		this.name = name;
	}
	
	@Override
	public void save(BitOutput output) {
		output.addByte(CustomSlot.Encodings.OUTPUT1);
		output.addString(name);
	}
	
	public String getName() {
		return name;
	}

	@Override
	public boolean canInsertItems() {
		return false;
	}

	@Override
	public boolean canTakeItems() {
		return true;
	}
}
