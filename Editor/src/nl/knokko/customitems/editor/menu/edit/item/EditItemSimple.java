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
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.item.SimpleCustomItem;
import nl.knokko.customitems.item.CustomItemType.Category;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.TextComponent;

public class EditItemSimple extends EditItemBase {
	
	private final SimpleCustomItem previous;
	
	private final IntEditField maxStacksize;

	public EditItemSimple(EditMenu menu, SimpleCustomItem previous) {
		super(menu, previous);
		this.previous = previous;
		if (previous == null) {
			maxStacksize = new IntEditField(64, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE, 1, 64);
		} else {
			maxStacksize = new IntEditField(previous.getMaxStacksize(), EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE, 1, 64);
		}
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new TextComponent("Max stacksize:", EditProps.LABEL), 0.71f, 0.35f, 0.895f, 0.45f);
		addComponent(maxStacksize, 0.9f, 0.35f, 0.975f, 0.45f);
	}

	@Override
	protected String create(short damage) {
		return menu.getSet().addSimpleItem(new SimpleCustomItem(internalType.currentType, damage, name.getText(), getDisplayName(), lore, attributes, enchantments, maxStacksize.getInt(), textureSelect.currentTexture));
	}

	@Override
	protected String apply(short damage) {
		return menu.getSet().changeSimpleItem(previous, internalType.currentType, damage, name.getText(), getDisplayName(), lore, attributes, enchantments, textureSelect.currentTexture, maxStacksize.getInt(), true);
	}

	@Override
	protected CustomItem previous() {
		return previous;
	}

	@Override
	protected Category getCategory() {
		return Category.DEFAULT;
	}
}