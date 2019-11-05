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
package nl.knokko.customitems.editor.set.item;

import nl.knokko.customitems.editor.set.ItemDamageClaim;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.util.bits.BitOutput;

public abstract class CustomItem extends nl.knokko.customitems.item.CustomItem implements ItemDamageClaim {

	protected NamedImage texture;
	protected byte[] customModel;

	public CustomItem(CustomItemType itemType, short itemDamage, String name, String displayName, String[] lore,
			AttributeModifier[] attributes, Enchantment[] defaultEnchantments, NamedImage texture,
			boolean[] itemFlags, byte[] customModel) {
		super(itemType, itemDamage, name, displayName, lore, attributes, defaultEnchantments, itemFlags);
		this.texture = texture;
		this.customModel = customModel;
	}

	@Override
	public String toString() {
		return "CustomItem(Name: " + name + ", Type: " + itemType + ", Damage: " + itemDamage + ")";
	}

	public NamedImage getTexture() {
		return texture;
	}
	
	/**
	 * @return The file content of the custom model fire or null if this item doesn't have a custom model
	 */
	public byte[] getCustomModel() {
		return customModel;
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

	public void setDefaultEnchantments(Enchantment[] enchantments) {
		this.defaultEnchantments = enchantments;
	}

	public void setTexture(NamedImage texture) {
		this.texture = texture;
	}

	public void setItemFlags(boolean[] itemFlags) {
		this.itemFlags = itemFlags;
	}
	
	public void setCustomModel(byte[] content) {
		customModel = content;
	}
	
	@Override
	public String getResourcePath() {
		return "customitems/" + name;
	}

	public final void save1(BitOutput output) {
		export(output);
		output.addJavaString(texture.getName());
	}
	
	public final void save2(BitOutput output) {
		export(output);
		output.addJavaString(texture.getName());
		output.addBoolean(customModel != null);
		if (customModel != null) {
			output.addByteArray(customModel);
		}
		if (this instanceof CustomShield) {
			CustomShield shield = (CustomShield) this;
			output.addBoolean(shield.getBlockingModel() != null);
			if (shield.getBlockingModel() != null) {
				output.addByteArray(shield.getBlockingModel());
			}
		} else if (this instanceof CustomTrident) {
			CustomTrident trident = (CustomTrident) this;
			output.addBoolean(trident.customInHandModel != null);
			if (trident.customInHandModel != null) {
				output.addByteArray(trident.customInHandModel);
			}
			output.addBoolean(trident.customThrowingModel != null);
			if (trident.customThrowingModel != null) {
				output.addByteArray(trident.customThrowingModel);
			}
		}
	}

	public abstract void export(BitOutput output);
}