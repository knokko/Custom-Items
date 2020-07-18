package nl.knokko.customitems.container.slot.display;

public abstract class SlotDisplay {

	protected final String displayName;
	protected final String[] lore;
	
	public SlotDisplay(String displayName, String[] lore) {
		this.displayName = displayName;
		this.lore = lore;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String[] getLore() {
		return lore;
	}
}
