package nl.knokko.customitems.plugin.set.item;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;

import nl.knokko.customitems.damage.DamageResistances;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.ReplaceCondition;
import nl.knokko.customitems.item.ReplaceCondition.ConditionOperation;
import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;

public class CustomHelmet3D extends CustomArmor {

	public CustomHelmet3D(CustomItemType itemType, short itemDamage, String name, String displayName, String[] lore,
			AttributeModifier[] attributes, Enchantment[] defaultEnchantments, long maxDurability,
			boolean allowEnchanting, boolean allowAnvil, Ingredient repairItem, Color color, boolean[] itemFlags,
			int entityHitDurabilityLoss, int blockBreakDurabilityLoss, DamageResistances damageResistances,
			List<PotionEffect> playerEffects, List<PotionEffect> targetEffects, 
			String[] commands, ReplaceCondition[] conditions, ConditionOperation op) {
		super(itemType, itemDamage, name, displayName, lore, attributes, defaultEnchantments, maxDurability, allowEnchanting,
				allowAnvil, repairItem, color, itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, damageResistances,
				playerEffects, targetEffects, commands, conditions, op);
	}
	
	// Don't let custom helmets till dirt because it is a hoe internally
	@Override
	public boolean forbidDefaultUse(ItemStack item) {
    	return true;
    }
}
