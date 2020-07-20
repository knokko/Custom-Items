package nl.knokko.customitems.container.slot.display;

public abstract class SlotDisplay {

	protected final String displayName;
	protected final String[] lore;
	
	protected final int amount;
	
	public SlotDisplay(String displayName, String[] lore, int amount) {
		this.displayName = displayName;
		this.lore = lore;
		this.amount = amount;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String[] getLore() {
		return lore;
	}
	
	public int getAmount() {
		return amount;
	}
}
