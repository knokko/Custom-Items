package nl.knokko.customitems.plugin.set.item;

import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;

public class SimpleCustomItem extends CustomItem {
	
	private int stackSize;

	public SimpleCustomItem(CustomItemType itemType, short itemDamage, String name, String displayName, String[] lore,
			AttributeModifier[] attributes, Enchantment[] defaultEnchantments, int stackSize) {
		super(itemType, itemDamage, name, displayName, lore, attributes, defaultEnchantments);
		this.stackSize = stackSize;
	}
	
	@Override
	public int getMaxStacksize() {
		return stackSize;
	}
}