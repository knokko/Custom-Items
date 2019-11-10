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
	/** The amount of projectiles to shoot each time the wand is used */
	public int amountPerShot;

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
		output.addJavaString(itemType.name());
		output.addShort(itemDamage);
		output.addJavaString(name);
		output.addJavaString(displayName);
		output.addByte((byte) lore.length);
		for(String line : lore)
			output.addJavaString(line);
		output.addByte((byte) attributes.length);
		for (AttributeModifier attribute : attributes) {
			output.addJavaString(attribute.getAttribute().name());
			output.addJavaString(attribute.getSlot().name());
			output.addNumber(attribute.getOperation().ordinal(), (byte) 2, false);
			output.addDouble(attribute.getValue());
		}
		output.addByte((byte) defaultEnchantments.length);
		for (Enchantment enchantment : defaultEnchantments) {
			output.addString(enchantment.getType().name());
			output.addInt(enchantment.getLevel());
		}
		output.addBooleans(itemFlags);
		output.addByte((byte) playerEffects.size());
		for (PotionEffect effect : playerEffects) {
			output.addJavaString(effect.getEffect().name());
			output.addInt(effect.getDuration());
			output.addInt(effect.getLevel());
		}
		output.addByte((byte) targetEffects.size());
		for (PotionEffect effect : targetEffects) {
			output.addJavaString(effect.getEffect().name());
			output.addInt(effect.getDuration());
			output.addInt(effect.getLevel());
		}
		output.addByte((byte) commands.length);
		for (String command : commands) {
			output.addJavaString(command);
		}
		
		// The saving of the projectile cover will be handled in Editor/CustomItem
		projectile.toBits(output);
		output.addInt(cooldown);
		output.addBoolean(charges != null);
		if (charges != null) {
			charges.toBits(output);
		}
		output.addInt(amountPerShot);
	}
}
