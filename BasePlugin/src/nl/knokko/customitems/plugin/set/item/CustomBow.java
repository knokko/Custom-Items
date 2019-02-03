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

import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;

public class CustomBow extends CustomTool {

	private final double damageMultiplier;
	private final double speedMultiplier;
	private final int knockbackStrength;
	private final boolean hasGravity;

	public CustomBow(short itemDamage, String name, String displayName, String[] lore, AttributeModifier[] attributes,
			Enchantment[] defaultEnchantments, long maxDurability, double damageMultiplier, double speedMultiplier, int knockbackStrength,
			boolean hasGravity, boolean allowEnchanting, boolean allowAnvil, Ingredient repairItem) {
		super(CustomItemType.BOW, itemDamage, name, displayName, lore, attributes, defaultEnchantments, maxDurability, allowEnchanting,
				allowAnvil, repairItem);
		this.damageMultiplier = damageMultiplier;
		this.speedMultiplier = speedMultiplier;
		this.knockbackStrength = knockbackStrength;
		this.hasGravity = hasGravity;
	}

	public double getDamageMultiplier() {
		return damageMultiplier;
	}

	public double getSpeedMultiplier() {
		return speedMultiplier;
	}

	public int getKnockbackStrength() {
		return knockbackStrength;
	}

	public boolean hasGravity() {
		return hasGravity;
	}
}