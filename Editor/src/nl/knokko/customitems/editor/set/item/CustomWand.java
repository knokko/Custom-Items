package nl.knokko.customitems.editor.set.item;

import java.util.List;

import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.util.bits.BitOutput;

public class CustomWand extends CustomItem {
	
	public int cooldown;

	public CustomWand(CustomItemType itemType, short itemDamage, String name, String displayName, String[] lore,
			AttributeModifier[] attributes, Enchantment[] defaultEnchantments, NamedImage texture, boolean[] itemFlags,
			byte[] customModel, List<PotionEffect> playerEffects, List<PotionEffect> targetEffects, String[] commands) {
		super(itemType, itemDamage, name, displayName, lore, attributes, defaultEnchantments, texture, itemFlags,
				customModel, playerEffects, targetEffects, commands);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void export(BitOutput output) {
		// TODO Auto-generated method stub

	}

}
