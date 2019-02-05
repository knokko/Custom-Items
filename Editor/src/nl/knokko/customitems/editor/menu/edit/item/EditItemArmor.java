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
package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.item.CustomArmor;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.AttributeModifier.Attribute;
import nl.knokko.customitems.item.AttributeModifier.Operation;
import nl.knokko.customitems.item.AttributeModifier.Slot;
import nl.knokko.customitems.item.CustomItemType.Category;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.text.ConditionalTextComponent;
import nl.knokko.gui.component.text.IntEditField;

public class EditItemArmor extends EditItemTool {
	
	private final CustomArmor previous;
	
	private final ColorEditField red;
	private final ColorEditField green;
	private final ColorEditField blue;

	public EditItemArmor(EditMenu menu, CustomArmor previous, Category toolCategory) {
		super(menu, previous, toolCategory);
		this.previous = previous;
		if (previous == null) {
			red = new ColorEditField(160);
			green = new ColorEditField(101);
			blue = new ColorEditField(64);
		} else {
			red = new ColorEditField(previous.getRed());
			green = new ColorEditField(previous.getGreen());
			blue = new ColorEditField(previous.getBlue());
		}
	}
	
	@Override
	protected AttributeModifier getExampleAttributeModifier() {
		double armor;
		Slot slot;
		CustomItemType i = internalType.currentType;
		if (i == CustomItemType.DIAMOND_HELMET) {
			armor = 3;
			slot = Slot.HEAD;
		}
		else if (i == CustomItemType.DIAMOND_CHESTPLATE) {
			armor = 8;
			slot = Slot.CHEST;
		} else if (i == CustomItemType.DIAMOND_LEGGINGS) {
			armor = 6;
			slot = Slot.LEGS;
		} else if (i == CustomItemType.DIAMOND_BOOTS) {
			armor = 3;
			slot = Slot.FEET;
		} else if (i == CustomItemType.IRON_HELMET) {
			armor = 2;
			slot = Slot.HEAD;
		} else if (i == CustomItemType.IRON_CHESTPLATE) {
			armor = 6;
			slot = Slot.CHEST;
		} else if (i == CustomItemType.IRON_LEGGINGS) {
			armor = 5;
			slot = Slot.LEGS;
		} else if (i == CustomItemType.IRON_BOOTS) {
			armor = 2;
			slot = Slot.FEET;
		} else if (i == CustomItemType.CHAINMAIL_HELMET) {
			armor = 2;
			slot = Slot.HEAD;
		} else if (i == CustomItemType.CHAINMAIL_CHESTPLATE) {
			armor = 5;
			slot = Slot.CHEST;
		} else if (i == CustomItemType.CHAINMAIL_LEGGINGS) {
			armor = 4;
			slot = Slot.LEGS;
		} else if (i == CustomItemType.CHAINMAIL_BOOTS) {
			armor = 1;
			slot = Slot.FEET;
		} else if (i == CustomItemType.GOLD_HELMET) {
			armor = 2;
			slot = Slot.HEAD;
		} else if (i == CustomItemType.GOLD_CHESTPLATE) {
			armor = 5;
			slot = Slot.CHEST;
		} else if (i == CustomItemType.GOLD_LEGGINGS) {
			armor = 3;
			slot = Slot.LEGS;
		} else if (i == CustomItemType.GOLD_BOOTS) {
			armor = 1;
			slot = Slot.FEET;
		} else if (i == CustomItemType.LEATHER_HELMET) {
			armor = 1;
			slot = Slot.HEAD;
		} else if (i == CustomItemType.LEATHER_CHESTPLATE) {
			armor = 3;
			slot = Slot.CHEST;
		} else if (i == CustomItemType.LEATHER_LEGGINGS) {
			armor = 2;
			slot = Slot.LEGS;
		} else if (i == CustomItemType.LEATHER_BOOTS) {
			armor = 1;
			slot = Slot.FEET;
		} else {
			throw new IllegalArgumentException("Unknown item type: " + i.name());
		}
		
		return new AttributeModifier(Attribute.ARMOR, slot, Operation.ADD, armor);
	}
	
	@Override
	protected String create(short damage, long maxUses) {
		return menu.getSet().addArmor(
				new CustomArmor(internalType.currentType, damage, name.getText(), getDisplayName(),
						lore, attributes, enchantments, maxUses, allowEnchanting.isChecked(),
						allowAnvil.isChecked(), repairItem.getIngredient(), textureSelect.currentTexture,
						red.getComponent().getInt(), green.getComponent().getInt(), blue.getComponent().getInt()),
						true);
	}
	
	protected String apply(short damage, long maxUses) {
		return menu.getSet().changeArmor(previous, internalType.currentType, damage, name.getText(),
				getDisplayName(), lore, attributes, enchantments, allowEnchanting.isChecked(),
				allowAnvil.isChecked(), repairItem.getIngredient(), maxUses, textureSelect.currentTexture,
				red.getComponent().getInt(), green.getComponent().getInt(), blue.getComponent().getInt(), true);
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new ConditionalTextComponent("Red: ", EditProps.LABEL, () -> {return showColors();}), 0.78f, 0.35f, 0.84f, 0.45f);
		addComponent(new ConditionalTextComponent("Green: ", EditProps.LABEL, () -> {return showColors();}), 0.75f, 0.24f, 0.84f, 0.34f);
		addComponent(new ConditionalTextComponent("Blue: ", EditProps.LABEL, () -> {return showColors();}), 0.77f, 0.13f, 0.84f, 0.23f);
		addComponent(red, 0.85f, 0.35f, 0.9f, 0.45f);
		addComponent(green, 0.85f, 0.24f, 0.9f, 0.34f);
		addComponent(blue, 0.85f, 0.13f, 0.9f, 0.23f);
		errorComponent.setProperties(EditProps.LABEL);
		errorComponent.setText("Hint: Use attribute modifiers to set the armor (toughness) of this piece.");
	}
	
	private boolean showColors() {
		CustomItemType t = internalType.currentType;
		return t == CustomItemType.LEATHER_BOOTS || t == CustomItemType.LEATHER_LEGGINGS
				|| t == CustomItemType.LEATHER_CHESTPLATE || t == CustomItemType.LEATHER_HELMET;
	}
	
	private class ColorEditField extends WrapperComponent<IntEditField> {

		public ColorEditField(int initial) {
			super(new IntEditField(initial, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE, 0, 255));
		}
		
		@Override
		public boolean isActive() {
			return showColors();
		}
	}
}