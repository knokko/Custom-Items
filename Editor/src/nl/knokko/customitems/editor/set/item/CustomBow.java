/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2018 knokko
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
package nl.knokko.customitems.editor.set.item;

import nl.knokko.customitems.editor.set.item.texture.BowTextures;
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.util.bits.BitOutput;

public class CustomBow extends CustomTool {
	
	private double damageMultiplier;
	private double speedMultiplier;
	private int knockbackStrength;
	private boolean hasGravity;

	public CustomBow(short itemDamage, String name, String displayName, String[] lore, AttributeModifier[] attributes,
			int durability, double damageMultiplier, double speedMultiplier, int knockbackStrength, boolean hasGravity, boolean allowEnchanting, boolean allowAnvil, Ingredient repairItem, BowTextures texture) {
		super(CustomItemType.BOW, itemDamage, name, displayName, lore, attributes, durability, allowEnchanting,
				allowAnvil, repairItem, texture);
		this.damageMultiplier = damageMultiplier;
		this.speedMultiplier = speedMultiplier;
		this.knockbackStrength = knockbackStrength;
		this.hasGravity = hasGravity;
	}

	@Override
	public BowTextures getTexture() {
		return (BowTextures) super.getTexture();
	}
	
	@Override
	public void export(BitOutput output) {
		/*
		output.addByte(ItemEncoding.ENCODING_TOOL_3);
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
		output.addInt(durability);
		output.addBoolean(allowEnchanting);
		output.addBoolean(allowAnvil);
		repairItem.save(output);
		*/
		
		output.addByte(ItemEncoding.ENCODING_BOW_3);
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
		output.addInt(durability);
		output.addDouble(damageMultiplier);
		output.addDouble(speedMultiplier);
		output.addInt(knockbackStrength);
		output.addBoolean(hasGravity);
		output.addBoolean(allowEnchanting);
		output.addBoolean(allowAnvil);
		repairItem.save(output);
	}
	
	public double getDamageMultiplier() {
		return damageMultiplier;
	}
	
	public void setDamageMultiplier(double newMultiplier) {
		damageMultiplier = newMultiplier;
	}
	
	public double getSpeedMultiplier() {
		return speedMultiplier;
	}
	
	public void setSpeedMultiplier(double newMultiplier) {
		speedMultiplier = newMultiplier;
	}
	
	public int getKnockbackStrength() {
		return knockbackStrength;
	}
	
	public void setKnockbackStrength(int newStrength) {
		knockbackStrength = newStrength;
	}
	
	public boolean hasGravity() {
		return hasGravity;
	}
	
	public void setGravity(boolean useGravity) {
		hasGravity = useGravity;
	}
}