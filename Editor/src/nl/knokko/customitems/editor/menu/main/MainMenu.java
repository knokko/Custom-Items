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
package nl.knokko.customitems.editor.menu.main;

import java.awt.Color;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.util.TextBuilder.Properties;

public class MainMenu extends GuiMenu {
	
	public static final MainMenu INSTANCE = new MainMenu();
	public static final GuiColor BACKGROUND = new SimpleGuiColor(200, 150, 0);
	
	public static final Properties BUTTON_PROPERTIES = Properties.createButton(new Color(0, 50, 200), new Color(0, 20, 80));
	public static final Properties HOVER_PROPERTIES = Properties.createButton(new Color(0, 65, 250), new Color(0, 30, 100));

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("New item set", BUTTON_PROPERTIES, HOVER_PROPERTIES, () ->  {
			state.getWindow().setMainComponent(CreateMenu.INSTANCE);
		}), 0.3f, 0.65f, 0.7f, 0.8f);
		addComponent(new DynamicTextButton("Edit item set", BUTTON_PROPERTIES, HOVER_PROPERTIES, () -> {
			state.getWindow().setMainComponent(LoadMenu.INSTANCE);
		}), 0.3f, 0.45f, 0.7f, 0.6f);
		addComponent(new DynamicTextButton("Exit editor", BUTTON_PROPERTIES, HOVER_PROPERTIES, () -> {
			state.getWindow().stopRunning();
		}), 0.3f, 0.2f, 0.7f, 0.35f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return BACKGROUND;
	}
}