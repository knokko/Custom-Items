package nl.knokko.customitems.container.slot;

public class EmptyCustomSlot implements CustomSlot {

	@Override
	public boolean canInsertItems() {
		return false;
	}

	@Override
	public boolean canTakeItems() {
		return false;
	}
}
