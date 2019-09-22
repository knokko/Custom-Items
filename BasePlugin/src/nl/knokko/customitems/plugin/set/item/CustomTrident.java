package nl.knokko.customitems.plugin.set.item;

import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;

public class CustomTrident extends CustomTool {
	
	public final int throwDurabilityLoss;
	
	public final double throwDamageMultiplier;
	public final double throwSpeedMultiplier;

	public CustomTrident(short itemDamage, String name, String displayName, String[] lore,
			AttributeModifier[] attributes, Enchantment[] defaultEnchantments, long maxDurability,
			boolean allowEnchanting, boolean allowAnvil, double throwDamageMultiplier, double throwSpeedMultiplier,
			Ingredient repairItem, boolean[] itemFlags, int entityHitDurabilityLoss, int blockBreakDurabilityLoss, 
			int throwDurabilityLoss) {
		super(CustomItemType.TRIDENT, itemDamage, name, displayName, lore, attributes, defaultEnchantments, maxDurability, allowEnchanting,
				allowAnvil, repairItem, itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss);
		this.throwDurabilityLoss = throwDurabilityLoss;
		this.throwSpeedMultiplier = throwSpeedMultiplier;
		this.throwDamageMultiplier = throwDamageMultiplier;
	}

}
