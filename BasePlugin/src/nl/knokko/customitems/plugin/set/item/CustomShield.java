package nl.knokko.customitems.plugin.set.item;

import java.util.List;

import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;

public class CustomShield extends CustomTool {
	
	private final double durabilityThreshold;

	public CustomShield(CustomItemType itemType, short itemDamage, String name, String displayName, String[] lore,
			AttributeModifier[] attributes, Enchantment[] defaultEnchantments, long maxDurability,
			boolean allowEnchanting, boolean allowAnvil, Ingredient repairItem, boolean[] itemFlags,
			int entityHitDurabilityLoss, int blockBreakDurabilityLoss, double durabilityThreshold,  
			List<PotionEffect> playerEffects, List<PotionEffect> targetEffects, String[] commands) {
		super(itemType, itemDamage, name, displayName, lore, attributes, defaultEnchantments, maxDurability, allowEnchanting,
				allowAnvil, repairItem, itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, playerEffects, targetEffects, commands);
		this.durabilityThreshold = durabilityThreshold;
	}
	
	public double getDurabilityThreshold() {
		return durabilityThreshold;
	}
}
