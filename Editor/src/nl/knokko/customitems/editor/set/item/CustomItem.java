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

public class CustomItem extends nl.knokko.customitems.item.CustomItem {
	
	protected NamedImage texture;

	public CustomItem(CustomItemType itemType, short itemDamage, String name, String displayName, String[] lore, 
			AttributeModifier[] attributes, NamedImage texture) {
		super(itemType, itemDamage, name, displayName, lore, attributes);
		this.texture = texture;
	}
	
	public NamedImage getTexture() {
		return texture;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDisplayName(String name) {
		displayName = name;
	}
	
	public void setItemType(CustomItemType type) {
		itemType = type;
	}
	
	public void setItemDamage(short damage) {
		itemDamage = damage;
	}
	
	public void setLore(String[] lore) {
		this.lore = lore;
	}
	
	public void setAttributes(AttributeModifier[] attributes) {
		this.attributes = attributes;
	}
	
	public void setTexture(NamedImage texture) {
		this.texture = texture;
	}
	
	public void save(BitOutput output) {
		export(output);
		output.addJavaString(texture.getName());
	}
	
	public void export(BitOutput output) {
		/*
		 * This is for ENCODING_SIMPLE_1
		output.addByte(ItemEncoding.ENCODING_SIMPLE_1);
		output.addJavaString(itemType.name());
		output.addShort(itemDamage);
		output.addJavaString(name);
		output.addJavaString(displayName);
		output.addByte((byte) lore.length);
		for(String line : lore)
			output.addJavaString(line);
		*/
		
		output.addByte(ItemEncoding.ENCODING_SIMPLE_2);
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
	}
}