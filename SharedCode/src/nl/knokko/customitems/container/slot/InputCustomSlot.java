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

	@Override
	public CustomSlot safeClone(CustomSlot[][] existingSlots) {
		
		// This can only return true if this slot no longer exists
		if (tryName(name, existingSlots)) {
			return this;
		}
		
		// Try name0, name1, name2... until it finds a free name
		int counter = 0;
		while (!tryName(name + counter, existingSlots)) {
			counter++;
		}
		return new InputCustomSlot(name + counter);
	}

	private boolean tryName(String inputSlotName, CustomSlot[][] existingSlots) {
		for (CustomSlot[] row : existingSlots) {
			for (CustomSlot slot : row) {
				if (slot instanceof InputCustomSlot && ((InputCustomSlot)slot).getName().equals(inputSlotName)) {
					return false;
				}
			}
		}
		return true;
	}
}
