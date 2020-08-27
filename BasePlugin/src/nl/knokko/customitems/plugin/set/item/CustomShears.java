package nl.knokko.customitems.plugin.set.item;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.plugin.CustomItemsEventHandler;
import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;

public class CustomShears extends CustomTool {
	
	private final int shearDurabilityLoss;

	public CustomShears(CustomItemType itemType, short itemDamage, String name, String displayName, String[] lore,
			AttributeModifier[] attributes, Enchantment[] defaultEnchantments, long maxDurability,
			boolean allowEnchanting, boolean allowAnvil, Ingredient repairItem, boolean[] itemFlags,
			int entityHitDurabilityLoss, int blockBreakDurabilityLoss, int shearDurabilityLoss, 
			List<PotionEffect> playerEffects, List<PotionEffect> targetEffects, String[] commands) {
		super(itemType, itemDamage, name, displayName, lore, attributes, defaultEnchantments, maxDurability, allowEnchanting,
				allowAnvil, repairItem, itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, playerEffects, targetEffects, commands);
		this.shearDurabilityLoss = shearDurabilityLoss;
	}
	
	public int getShearDurabilityLoss() {
		return shearDurabilityLoss;
	}
	
	@Override
	public void onBlockBreak(Player player, ItemStack tool, Block block, boolean wasSolid) {
		// Only lose durability when breaking non-solid blocks because we shear it
		if (!wasSolid && blockBreakDurabilityLoss != 0 && decreaseDurability(tool, blockBreakDurabilityLoss)) {
			CustomItemsEventHandler.playBreakSound(player);
			player.getInventory().setItemInMainHand(null);
		}
	}
}
