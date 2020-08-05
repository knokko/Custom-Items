package nl.knokko.customitems.container.slot;

import nl.knokko.util.bits.BitOutput;

public class EmptyCustomSlot implements CustomSlot {

	@Override
	public boolean canInsertItems() {
		return false;
	}

	@Override
	public boolean canTakeItems() {
		return false;
	}
	
	@Override
	public void save(BitOutput output) {
		output.addByte(CustomSlot.Encodings.EMPTY);
	}
}
