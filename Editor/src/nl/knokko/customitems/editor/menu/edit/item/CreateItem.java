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
import nl.knokko.customitems.item.CustomItemType.Category;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class CreateItem extends GuiMenu {
	
	private final EditMenu menu;

	public CreateItem(EditMenu menu) {
		this.menu = menu;
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(menu.getItemOverview());
		}), 0.1f, 0.8f, 0.25f, 0.9f);
		addComponent(new DynamicTextButton("Simple Item", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemSimple(menu, null));
		}), 0.5f, 0.8f, 0.7f, 0.9f);
		addComponent(new DynamicTextButton("Sword", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemTool(menu, null, Category.SWORD));
		}), 0.5f, 0.65f, 0.7f, 0.75f);
		addComponent(new DynamicTextButton("Pickaxe", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemTool(menu, null, Category.PICKAXE));
		}), 0.5f, 0.525f, 0.7f, 0.625f);
		addComponent(new DynamicTextButton("Axe", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemTool(menu, null, Category.AXE));
		}), 0.5f, 0.4f, 0.7f, 0.5f);
		addComponent(new DynamicTextButton("Shovel", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemTool(menu, null, Category.SHOVEL));
		}), 0.5f, 0.275f, 0.7f, 0.375f);
		addComponent(new DynamicTextButton("Hoe", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemHoe(menu, null));
		}), 0.5f, 0.15f, 0.7f, 0.25f);
		addComponent(new DynamicTextButton("Shear", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemShears(menu, null));
		}), 0.5f, 0.025f, 0.7f, 0.125f);
		addComponent(new DynamicTextButton("Bow", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemBow(menu, null));
		}), 0.75f, 0.65f, 0.95f, 0.75f);
		addComponent(new DynamicTextButton("Helmet", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemArmor(menu, null, Category.HELMET));
		}), 0.75f, 0.525f, 0.95f, 0.625f);
		addComponent(new DynamicTextButton("Chestplate", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemArmor(menu, null, Category.CHESTPLATE));
		}), 0.75f, 0.4f, 0.95f, 0.5f);
		addComponent(new DynamicTextButton("Leggings", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemArmor(menu, null, Category.LEGGINGS));
		}), 0.75f, 0.275f, 0.95f, 0.375f);
		addComponent(new DynamicTextButton("Boots", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemArmor(menu, null, Category.BOOTS));
		}), 0.75f, 0.15f, 0.95f, 0.25f);
		addComponent(new DynamicTextButton("Shield", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemShield(menu, null));
		}), 0.75f, 0.025f, 0.95f, 0.125f);
	}
}