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
import nl.knokko.customitems.editor.menu.edit.item.attribute.AttributesOverview;
import nl.knokko.customitems.editor.menu.edit.item.enchantment.EnchantmentsOverview;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.item.NamedImage;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.menu.TextArrayEditMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;
import nl.knokko.gui.component.text.TextEditField;

public abstract class EditItemBase extends GuiMenu {
	
	private static final float LABEL_X = 0.25f;
	private static final float BUTTON_X = 0.4f;
	
	private static final AttributeModifier[] DEFAULT_ATTRIBUTES = {};
	private static final Enchantment[] DEFAULT_ENCHANTMENTS = {};
	
	protected final EditMenu menu;
	
	protected TextEditField name;
	protected ItemTypeSelect internalType;
	protected TextEditField internalDamage;
	protected TextEditField displayName;
	protected String[] lore;
	protected AttributeModifier[] attributes;
	protected Enchantment[] enchantments;
	protected TextureSelect textureSelect;
	protected TextComponent errorComponent;

	public EditItemBase(EditMenu menu, CustomItem previous) {
		this.menu = menu;
		if(previous != null) {
			name = new TextEditField(previous.getName(), EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			internalType = new ItemTypeSelect(previous.getItemType());
			internalDamage = new TextEditField(Short.toString(previous.getItemDamage()), EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			displayName = new TextEditField(previous.getDisplayName(), EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			textureSelect = new TextureSelect(previous.getTexture());
			lore = previous.getLore();
			attributes = previous.getAttributes();
			enchantments = previous.getDefaultEnchantments();
		} else {
			name = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			internalType = new ItemTypeSelect(CustomItemType.DIAMOND_HOE);
			internalDamage = new TextEditField(Short.toString(menu.getSet().nextAvailableDamage(internalType.currentType)), EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			displayName = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			textureSelect = new TextureSelect(null);
			lore = new String[] {};
			attributes = DEFAULT_ATTRIBUTES;
			enchantments = DEFAULT_ENCHANTMENTS;
		}
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		errorComponent = new TextComponent("", EditProps.ERROR);
		addComponent(new TextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(menu.getItemOverview());
		}), 0.025f, 0.7f, 0.15f, 0.8f);
		addComponent(new TextComponent("Name: ", EditProps.LABEL), LABEL_X, 0.8f, LABEL_X + 0.075f, 0.85f);
		addComponent(new TextComponent("Internal item type: ", EditProps.LABEL), LABEL_X, 0.74f, LABEL_X + 0.125f, 0.79f);
		addComponent(new TextComponent("Internal item damage: ", EditProps.LABEL), LABEL_X, 0.68f, LABEL_X + 0.125f, 0.73f);
		addComponent(new TextComponent("Display name: ", EditProps.LABEL), LABEL_X, 0.62f, LABEL_X + 0.1f, 0.67f);
		addComponent(new TextComponent("Lore: ", EditProps.LABEL), LABEL_X, 0.56f, LABEL_X + 0.075f, 0.61f);
		addComponent(new TextComponent("Attribute modifiers: ", EditProps.LABEL), LABEL_X, 0.5f, LABEL_X + 0.125f, 0.55f);
		addComponent(new TextComponent("Default enchantments: ", EditProps.LABEL), LABEL_X, 0.44f, LABEL_X + 0.13f, 0.49f);
		addComponent(new TextComponent("Texture: ", EditProps.LABEL), LABEL_X, 0.38f, LABEL_X + 0.075f, 0.43f);
		if(previous() != null) {
			addComponent(new TextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				String error = apply();
				if(error != null) {
					errorComponent.setText(error);
					errorComponent.setProperties(EditProps.ERROR);
				} else
					state.getWindow().setMainComponent(menu.getItemOverview());
			}), 0.025f, 0.1f, 0.15f, 0.2f);
		}
		else {
			addComponent(new TextButton("Create", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				String error = create();
				if(error != null) {
					errorComponent.setProperties(EditProps.ERROR);
					errorComponent.setText(error);
				}
				else
					state.getWindow().setMainComponent(menu.getItemOverview());
			}), 0.025f, 0.1f, 0.15f, 0.2f);
		}
		addComponent(errorComponent, 0.1f, 0.9f, 0.9f, 1f);
		addComponent(name, BUTTON_X, 0.8f, BUTTON_X + 0.1f, 0.85f);
		addComponent(internalType, BUTTON_X, 0.74f, BUTTON_X + 0.1f, 0.79f);
		addComponent(internalDamage, BUTTON_X, 0.68f, BUTTON_X + 0.1f, 0.73f);
		addComponent(displayName, BUTTON_X, 0.62f, BUTTON_X + 0.1f, 0.67f);
		addLoreComponent();
		addAttributesComponent();
		addEnchantmentsComponent();
		addComponent(textureSelect, BUTTON_X, 0.38f, BUTTON_X + 0.1f, 0.43f);
	}
	
	protected abstract String create(short internalItemDamage);
	
	protected abstract String apply(short internalItemDamage);
	
	protected String create() {
		String error = null;
		try {
			short damage = Short.parseShort(internalDamage.getText());
			if(damage > 0 && damage < internalType.currentType.getMaxDurability())
				return create(damage);
			else
				error = "The internal item damage must be larger than 0 and smaller than " + internalType.currentType.getMaxDurability();
		} catch (NumberFormatException nfe) {
			error = "The internal item damage must be an integer.";
		}
		return error;
	}

	protected String apply() {
		String error = null;
		try {
			short damage = Short.parseShort(internalDamage.getText());
			if(damage > 0 && damage < internalType.currentType.getMaxDurability())
				return apply(damage);
			else
				error = "The internal item damage must be larger than 0 and smaller than " + internalType.currentType.getMaxDurability();
		} catch (NumberFormatException nfe) {
			error = "The internal item damage must be an integer.";
		}
		return error;
	}
	
	protected abstract CustomItem previous();
	
	protected String getDisplayName() {
		return displayName.getText().replaceAll("&", "§");
	}
	
	protected class ItemTypeSelect extends TextButton {
		
		protected CustomItemType currentType;

		public ItemTypeSelect(CustomItemType initial) {
			super(initial.toString(), EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, null);
			currentType = initial;
		}
		
		@Override
		public void init() {
			super.init();
			clickAction = () -> {
				state.getWindow().setMainComponent(new SelectItemType(EditItemBase.this, (CustomItemType type) -> {
					currentType = type;
					setText(type.toString());
				}, getCategory()));
			};
		}
	}
	
	protected class TextureSelect extends TextButton {
		
		protected NamedImage currentTexture;
		
		private TextureSelect(NamedImage initial) {
			super(initial != null ? initial.getName() : "None", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, null);
			currentTexture = initial;
		}
		
		@Override
		public void init() {
			super.init();
			clickAction = () -> {
				state.getWindow().setMainComponent(new SelectTexture(menu.getSet(), EditItemBase.this, (NamedImage texture) -> {
					return allowTexture(texture);
				}, (NamedImage texture) -> {
					currentTexture = texture;
					setText(texture.getName());
				}));
			};
		}
	}
	
	private void addLoreComponent() {
		addComponent(new TextButton("Change lore...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new TextArrayEditMenu(EditItemBase.this, (String[] newLore) -> {
				lore = newLore;
				for (int index = 0; index < lore.length; index++)
					lore[index] = lore[index].replaceAll("&", "§");
			}, EditProps.BACKGROUND, EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, EditProps.SAVE_BASE, EditProps.SAVE_HOVER, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE, lore));
		}), BUTTON_X, 0.56f, BUTTON_X + 0.1f, 0.61f);
	}
	
	protected abstract AttributeModifier getExampleAttributeModifier();
	
	private void addAttributesComponent() {
		addComponent(new TextButton("Change attributes...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new AttributesOverview(getExampleAttributeModifier(), attributes, EditItemBase.this, (AttributeModifier[] attributes) -> {
				this.attributes = attributes;
			}));
		}), BUTTON_X, 0.5f, BUTTON_X + 0.1f, 0.55f);
	}
	
	private void addEnchantmentsComponent() {
		addComponent(new TextButton("Change enchantments...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EnchantmentsOverview(enchantments, EditItemBase.this, (Enchantment[] enchantments) -> {
				this.enchantments = enchantments;
			}));
		}), BUTTON_X, 0.44f, BUTTON_X + 0.1f, 0.49f);
	}
	
	protected abstract CustomItemType.Category getCategory();
	
	protected boolean allowTexture(NamedImage texture) {
		return true;
	}
}