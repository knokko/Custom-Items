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

import java.util.Collection;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.item.CustomArmor;
import nl.knokko.customitems.editor.set.item.CustomBow;
import nl.knokko.customitems.editor.set.item.CustomHoe;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.item.CustomShears;
import nl.knokko.customitems.editor.set.item.CustomTool;
import nl.knokko.customitems.editor.set.item.SimpleCustomItem;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;

public class ItemOverview extends GuiMenu {
	
	protected final EditMenu menu;
	private final TextComponent errorComponent;
	
	protected ItemList itemList;

	public ItemOverview(EditMenu menu) {
		this.menu = menu;
		errorComponent = new TextComponent("", EditProps.ERROR);
	}
	
	@Override
	public void init() {
		if(didInit) itemList.refresh();
		super.init();
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		itemList = new ItemList();
		addComponent(itemList, 0.3f, 0f, 1f, 0.9f);
		addComponent(new TextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(menu);
		}), 0.05f, 0.7f, 0.2f, 0.8f);
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
		addComponent(new TextButton("Create item", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new CreateItem(menu));
		}), 0.05f, 0.4f, 0.25f, 0.5f);
	}
	
	private class ItemList extends GuiMenu {

		@Override
		protected void addComponents() {
			refresh();
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND;
		}
		
		private void refresh() {
			clearComponents();
			Collection<CustomItem> items = menu.getSet().getItems();
			int index = 0;
			for(CustomItem item : items) {
				float minY = 0.9f - index * 0.1f;
				float maxY = 1f - index * 0.1f;
				addComponent(new TextComponent(item.getName(), EditProps.LABEL), 0f, minY, 0.5f, maxY);
				addComponent(new TextButton("Edit", EditProps.BUTTON, EditProps.HOVER, () -> {
					if (item instanceof CustomBow)
						state.getWindow().setMainComponent(new EditItemBow(menu, (CustomBow) item));
					else if (item instanceof CustomArmor)
						state.getWindow().setMainComponent(new EditItemArmor(menu, (CustomArmor) item, item.getItemType().getMainCategory()));
					else if (item instanceof CustomShears) 
						state.getWindow().setMainComponent(new EditItemShears(menu, (CustomShears) item));
					else if (item instanceof CustomHoe)
						state.getWindow().setMainComponent(new EditItemHoe(menu, (CustomHoe) item));
					else if (item instanceof CustomTool)
						state.getWindow().setMainComponent(new EditItemTool(menu, (CustomTool) item, item.getItemType().getMainCategory()));
					else
						state.getWindow().setMainComponent(new EditItemSimple(menu, (SimpleCustomItem) item));
				}), 0.6f, minY, 0.7f, maxY);
				addComponent(new TextButton("Delete", EditProps.QUIT_BASE, EditProps.QUIT_HOVER, () -> {
					String error = menu.getSet().removeItem(item);
					if (error != null)
						errorComponent.setText(error);
					else
						refresh();
				}), 0.8f, minY, 0.95f, maxY);
				index++;
			}
		}
	}
}