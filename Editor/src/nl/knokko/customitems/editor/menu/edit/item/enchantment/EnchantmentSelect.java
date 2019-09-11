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
package nl.knokko.customitems.editor.menu.edit.item.enchantment;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.item.EnchantmentType;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class EnchantmentSelect extends GuiMenu {

	private final Receiver receiver;
	private final GuiComponent returnMenu;

	public EnchantmentSelect(Receiver receiver, GuiComponent returnMenu) {
		this.receiver = receiver;
		this.returnMenu = returnMenu;
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.1f, 0.1f, 0.2f, 0.2f);
		float x = 0.3f;
		float y = 0.9f;
		EnchantmentType[] enchantments = EnchantmentType.values();
		for (EnchantmentType enchantment : enchantments) {
			addComponent(new DynamicTextButton(enchantment.toString(), EditProps.SELECT_BASE, EditProps.SELECT_HOVER, () -> {
				receiver.onSelect(enchantment);
				state.getWindow().setMainComponent(returnMenu);
			}), x, y - 0.05f, x + 0.125f, y);
			y -= 0.06f;
			if (y < 0.15f) {
				y = 0.9f;
				x += 0.15f;
			}
		}
	}
	
	public static interface Receiver {
		
		void onSelect(EnchantmentType enchantment);
	}
}
