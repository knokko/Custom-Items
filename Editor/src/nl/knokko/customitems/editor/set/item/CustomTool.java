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

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.util.bits.BitOutput;

public class CustomTool extends CustomItem {
	
	private int durability;
	
	private boolean allowEnchanting;
	private boolean allowAnvil;

	public CustomTool(CustomItemType itemType, short itemDamage, String name, String displayName, String[] lore,
			AttributeModifier[] attributes, int durability, boolean allowEnchanting, boolean allowAnvil, NamedImage texture) {
		super(itemType, itemDamage, name, displayName, lore, attributes, texture);
		this.durability = durability;
		this.allowEnchanting = allowEnchanting;
		this.allowAnvil = allowAnvil;
	}
	
	@Override
	public void export(BitOutput output) {
		output.addByte(ItemEncoding.ENCODING_TOOL_2);
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
	}
	
	public boolean allowEnchanting() {
		return allowEnchanting;
	}
	
	public boolean allowAnvilActions() {
		return allowAnvil;
	}
	
	public int getDurability() {
		return durability;
	}
	
	public void setAllowEnchanting(boolean allow) {
		allowEnchanting = allow;
	}
	
	public void setAllowAnvilActions(boolean allow) {
		allowAnvil = allow;
	}
	
	public void setDurability(int durability) {
		this.durability = durability;
	}
}