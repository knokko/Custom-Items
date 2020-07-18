package nl.knokko.customitems.container.slot;

public class InputCustomSlot implements CustomSlot {
	
	private final String name;
	
	public InputCustomSlot(String name) {
		this.name = name;
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
