package nl.knokko.customitems.container.slot;

public class OutputCustomSlot implements CustomSlot {
	
	private final String name;
	
	public OutputCustomSlot(String name) {
		this.name = name;
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
