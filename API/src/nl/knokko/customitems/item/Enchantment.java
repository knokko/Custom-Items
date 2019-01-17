package nl.knokko.customitems.item;

public class Enchantment {
	
	private final EnchantmentType type;
	private final int level;

	public Enchantment(EnchantmentType type, int level) {
		this.type = type;
		this.level = level;
	}
	
	public EnchantmentType getType() {
		return type;
	}
	
	public int getLevel() {
		return level;
	}
}