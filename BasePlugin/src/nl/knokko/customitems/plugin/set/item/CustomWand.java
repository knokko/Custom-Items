package nl.knokko.customitems.plugin.set.item;

import java.util.List;

import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.WandCharges;
import nl.knokko.customitems.projectile.CIProjectile;

public class CustomWand extends CustomItem {
	
	public final CIProjectile projectile;
	public final int cooldown;
	public final WandCharges charges;
	public final int amountPerShot;

	public CustomWand(CustomItemType itemType, short itemDamage, String name, String displayName, String[] lore,
			AttributeModifier[] attributes, Enchantment[] defaultEnchantments, boolean[] itemFlags,
			List<PotionEffect> playerEffects, List<PotionEffect> targetEffects, String[] commands,
			CIProjectile projectile, int cooldown, WandCharges charges, int amountPerShot) {
		super(itemType, itemDamage, name, displayName, lore, attributes, defaultEnchantments, itemFlags, playerEffects,
				targetEffects, commands);
		this.projectile = projectile;
		this.cooldown = cooldown;
		this.charges = charges;
		this.amountPerShot = amountPerShot;
	}

	@Override
	public int getMaxStacksize() {
		return 1;
	}
}
