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
import nl.knokko.customitems.editor.set.item.CustomBow;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.item.NamedImage;
import nl.knokko.customitems.editor.set.item.texture.BowTextures;
import nl.knokko.customitems.item.CustomItemType.Category;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.text.TextComponent;
import nl.knokko.gui.component.text.TextEditField;

public class EditItemBow extends EditItemTool {

	private final CustomBow previous;

	private final TextEditField damageMultiplier;
	private final TextEditField speedMultiplier;
	private final TextEditField knockbackStrength;
	private final CheckboxComponent gravity;

	public EditItemBow(EditMenu menu, CustomBow previous) {
		super(menu, previous, Category.BOW);
		this.previous = previous;
		if (previous != null) {
			damageMultiplier = new TextEditField(previous.getDamageMultiplier() + "", EditProps.EDIT_BASE,
					EditProps.EDIT_ACTIVE);
			speedMultiplier = new TextEditField(previous.getSpeedMultiplier() + "", EditProps.EDIT_BASE,
					EditProps.EDIT_ACTIVE);
			knockbackStrength = new TextEditField(previous.getKnockbackStrength() + "", EditProps.EDIT_BASE,
					EditProps.EDIT_ACTIVE);
			gravity = new CheckboxComponent(previous.hasGravity());
		} else {
			damageMultiplier = new TextEditField("1.0", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			speedMultiplier = new TextEditField("1.0", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			knockbackStrength = new TextEditField("0", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			gravity = new CheckboxComponent(true);
		}
	}

	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new TextComponent("Damage multiplier: ", EditProps.LABEL), 0.71f, 0.35f, 0.895f, 0.45f);
		addComponent(damageMultiplier, 0.895f, 0.35f, 0.965f, 0.45f);
		addComponent(new TextComponent("Speed multiplier: ", EditProps.LABEL), 0.71f, 0.24f, 0.88f, 0.34f);
		addComponent(speedMultiplier, 0.895f, 0.24f, 0.965f, 0.34f);
		addComponent(new TextComponent("knockback strength: ", EditProps.LABEL), 0.71f, 0.13f, 0.9f, 0.23f);
		addComponent(knockbackStrength, 0.9f, 0.13f, 0.95f, 0.23f);
		addComponent(new TextComponent("Arrow gravity", EditProps.LABEL), 0.8f, 0.02f, 0.95f, 0.12f);
		addComponent(gravity, 0.75f, 0.02f, 0.775f, 0.045f);
	}

	@Override
	protected boolean allowTexture(NamedImage texture) {
		return texture instanceof BowTextures;
	}

	@Override
	protected CustomBow previous() {
		return previous;
	}

	@Override
	protected String create(short damage, long maxUses) {
		String error = null;
		try {
			double damageMultiplier = Double.parseDouble(this.damageMultiplier.getText());
			try {
				double speedMultiplier = Double.parseDouble(this.speedMultiplier.getText());
				try {
					int knockbackStrength = Integer.parseInt(this.knockbackStrength.getText());
					if (maxUses > 0 || maxUses == CustomItem.UNBREAKABLE_TOOL_DURABILITY) {
						error = menu.getSet()
								.addBow(new CustomBow(damage, name.getText(), getDisplayName(), lore, attributes,
										enchantments, maxUses, damageMultiplier, speedMultiplier, knockbackStrength,
										gravity.isChecked(), allowEnchanting.isChecked(), allowAnvil.isChecked(),
										repairItem.getIngredient(), (BowTextures) textureSelect.currentTexture), true);
					} else
						error = "The max uses must be greater than 0 or " + CustomItem.UNBREAKABLE_TOOL_DURABILITY
								+ " to become unbreakable";
				} catch (NumberFormatException nfe) {
					error = "The knockback strength must be an integer";
				}
			} catch (NumberFormatException nfe) {
				error = "The speed multiplier must be a number";
			}
		} catch (NumberFormatException nfe) {
			error = "The damage multiplier must be a number";
		}
		return error;
	}

	@Override
	protected String apply(short damage, long maxUses) {
		String error = null;
		try {
			double damageMultiplier = Double.parseDouble(this.damageMultiplier.getText());
			try {
				double speedMultiplier = Double.parseDouble(this.speedMultiplier.getText());
				try {
					int knockbackStrength = Integer.parseInt(this.knockbackStrength.getText());
					if (maxUses > 0 || maxUses == CustomItem.UNBREAKABLE_TOOL_DURABILITY) {
						error = menu.getSet().changeBow(previous, damage, name.getText(), getDisplayName(), lore,
								attributes, enchantments, damageMultiplier, speedMultiplier, knockbackStrength,
								gravity.isChecked(), allowEnchanting.isChecked(), allowAnvil.isChecked(),
								repairItem.getIngredient(), maxUses, (BowTextures) textureSelect.currentTexture, true);
					} else
						error = "The max uses must be greater than 0 or " + CustomItem.UNBREAKABLE_TOOL_DURABILITY
								+ " to become unbreakable";
				} catch (NumberFormatException nfe) {
					error = "The knockback strength must be an integer";
				}
			} catch (NumberFormatException nfe) {
				error = "The speed multiplier must be a number";
			}
		} catch (NumberFormatException nfe) {
			error = "The damage multiplier must be a number";
		}
		return error;
	}
}