/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2019 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package nl.knokko.customitems.plugin.set.item;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import nl.knokko.customitems.damage.DamageResistances;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;

public class CustomArmor extends CustomTool {
	
	private final Color color;
	
	private final DamageResistances damageResistances;

	public CustomArmor(CustomItemType itemType, short itemDamage, String name, String displayName, String[] lore,
			AttributeModifier[] attributes, Enchantment[] defaultEnchantments, long maxDurability,
			boolean allowEnchanting, boolean allowAnvil, Ingredient repairItem, Color color, boolean[] itemFlags,
			int entityHitDurabilityLoss, int blockBreakDurabilityLoss, DamageResistances damageResistances, List<PotionEffect> playerEffects, List<PotionEffect> targetEffects) {
		super(itemType, itemDamage, name, displayName, lore, attributes, defaultEnchantments, maxDurability,
				allowEnchanting, allowAnvil, repairItem, itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, playerEffects, targetEffects);
		this.color = color;
		this.damageResistances = damageResistances;
	}
	
	@Override
	protected String getDisplayTagContent12() {
		if (itemType.isLeatherArmor()) {
			return super.getDisplayTagContent12() + ",color:" + (color.getBlue() + 256 * color.getGreen() + 65536 * color.getRed());
		} else {
			return super.getDisplayTagContent12();
		}
	}
	
	@Override
	protected String getDisplayTagContent14() {
		if (itemType.isLeatherArmor()) {
			return super.getDisplayTagContent14() + ",color:" + (color.getBlue() + 256 * color.getGreen() + 65536 * color.getRed());
		} else {
			return super.getDisplayTagContent14();
		}
	}
	
	@Override
	public ItemMeta createItemMeta(ItemStack item, List<String> lore) {
		ItemMeta meta = super.createItemMeta(item, lore);
		if (itemType.isLeatherArmor()) {
			((LeatherArmorMeta) meta).setColor(color);
		}
		return meta;
	}
	
	public DamageResistances getDamageResistances() {
		return damageResistances;
	}
}