package nl.knokko.customitems.plugin.set.item;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;

public class CustomArmor extends CustomTool {
	
	private final Color color;

	public CustomArmor(CustomItemType itemType, short itemDamage, String name, String displayName, String[] lore,
			AttributeModifier[] attributes, Enchantment[] defaultEnchantments, long maxDurability,
			boolean allowEnchanting, boolean allowAnvil, Ingredient repairItem, Color color) {
		super(itemType, itemDamage, name, displayName, lore, attributes, defaultEnchantments, maxDurability,
				allowEnchanting, allowAnvil, repairItem);
		this.color = color;
	}
	
	@Override
	public ItemStack create(int amount, long durability) {
		ItemStack base = super.create(amount, durability);
		CustomItemType i = itemType;
		if (i == CustomItemType.LEATHER_HELMET || i == CustomItemType.LEATHER_CHESTPLATE
				|| i == CustomItemType.LEATHER_LEGGINGS || i == CustomItemType.LEATHER_BOOTS) {
			LeatherArmorMeta meta = (LeatherArmorMeta) base.getItemMeta();
			meta.setColor(color);
			base.setItemMeta(meta);
		}
		return base;
	}
}