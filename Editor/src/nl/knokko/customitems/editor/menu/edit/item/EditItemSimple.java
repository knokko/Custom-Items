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
package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.item.CustomItemType.Category;

public class EditItemSimple extends EditItemBase {
	
	private final CustomItem previous;

	public EditItemSimple(EditMenu menu, CustomItem previous) {
		super(menu, previous);
		this.previous = previous;
		
	}

	@Override
	protected String create() {
		String error = null;
		try {
			short damage = Short.parseShort(internalDamage.getText());
			if(damage > 0 && damage < internalType.currentType.getMaxDurability())
				error = menu.getSet().addItem(new CustomItem(internalType.currentType, damage, name.getText(), getDisplayName(), lore, attributes, enchantments, textureSelect.currentTexture));
			else
				error = "The internal item damage must be larger than 0 and smaller than " + internalType.currentType.getMaxDurability();
		} catch (NumberFormatException nfe) {
			error = "The internal item damage must be an integer.";
		}
		return error;
	}

	@Override
	protected String apply() {
		String error = null;
		try {
			short damage = Short.parseShort(internalDamage.getText());
			if(damage > 0 && damage < internalType.currentType.getMaxDurability())
				error = menu.getSet().changeItem(previous, internalType.currentType, damage, name.getText(), getDisplayName(), lore, attributes, enchantments, textureSelect.currentTexture, true);
			else
				error = "The internal item damage must be larger than 0 and smaller than " + internalType.currentType.getMaxDurability();
		} catch (NumberFormatException nfe) {
			error = "The internal item damage must be an integer.";
		}
		return error;
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