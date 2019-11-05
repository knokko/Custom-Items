package nl.knokko.customitems.plugin.set.item;

import java.util.List;

import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;

public class CustomShears extends CustomTool {
	
	private final int shearDurabilityLoss;

	public CustomShears(CustomItemType itemType, short itemDamage, String name, String displayName, String[] lore,
			AttributeModifier[] attributes, Enchantment[] defaultEnchantments, long maxDurability,
			boolean allowEnchanting, boolean allowAnvil, Ingredient repairItem, boolean[] itemFlags,
			int entityHitDurabilityLoss, int blockBreakDurabilityLoss, int shearDurabilityLoss, List<PotionEffect> playerEffects, List<PotionEffect> targetEffects) {
		super(itemType, itemDamage, name, displayName, lore, attributes, defaultEnchantments, maxDurability, allowEnchanting,
				allowAnvil, repairItem, itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, playerEffects, targetEffects);
		this.shearDurabilityLoss = shearDurabilityLoss;
	}
	
	public int getShearDurabilityLoss() {
		return shearDurabilityLoss;
	}
}
