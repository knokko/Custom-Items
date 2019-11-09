package nl.knokko.customitems.editor.set.item;

import java.util.List;

import nl.knokko.customitems.editor.set.projectile.cover.ProjectileCover;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.WandCharges;
import nl.knokko.customitems.projectile.Projectile;
import nl.knokko.util.bits.BitOutput;

public class CustomWand extends CustomItem {
	
	public Projectile projectile;
	/** If cover is null, the projectiles fired by this wand won't have a projectile cover */
	public ProjectileCover cover;
	
	public int cooldown;
	/** If charges is null, the wand doesn't need charges and only has to worry about its cooldown */
	public WandCharges charges;

	public CustomWand(CustomItemType itemType, short itemDamage, String name, String displayName, String[] lore,
			AttributeModifier[] attributes, Enchantment[] defaultEnchantments, NamedImage texture, boolean[] itemFlags,
			byte[] customModel, List<PotionEffect> playerEffects, List<PotionEffect> targetEffects, String[] commands,
			Projectile projectile, ProjectileCover cover, int cooldown, WandCharges charges) {
		super(itemType, itemDamage, name, displayName, lore, attributes, defaultEnchantments, texture, itemFlags,
				customModel, playerEffects, targetEffects, commands);
		this.projectile = projectile;
		this.cover = cover;
		this.cooldown = cooldown;
		this.charges = charges;
	}

	@Override
	public void export(BitOutput output) {
		output.addByte(ItemEncoding.ENCODING_WAND_8);
		
		// The saving of the projectile cover will be handled in Editor/CustomItem
		projectile.toBits(output);
		output.addInt(cooldown);
		output.addBoolean(charges != null);
		if (charges != null) {
			charges.toBits(output);
		}
	}
}
