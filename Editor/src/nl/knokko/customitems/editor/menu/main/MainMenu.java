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

import nl.knokko.customitems.editor.menu.commandhelp.CommandBlockHelpOverview;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class MainMenu extends GuiMenu {
	
	public static final MainMenu INSTANCE = new MainMenu();
	
	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("New item set", EditProps.BUTTON, EditProps.HOVER, () ->  {
			state.getWindow().setMainComponent(CreateMenu.INSTANCE);
		}), 0.3f, 0.8f, 0.7f, 0.95f);
		addComponent(new DynamicTextButton("Edit item set", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(LoadMenu.INSTANCE);
		}), 0.3f, 0.6f, 0.7f, 0.75f);
		addComponent(new DynamicTextButton("Combine item sets", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(CombineMenu.getInstance());
		}), 0.3f, 0.4f, 0.7f, 0.55f);
		addComponent(new DynamicTextButton("Exit editor", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().stopRunning();
		}), 0.3f, 0.15f, 0.7f, 0.3f);

		addComponent(new DynamicTextComponent("For help, visit the discord server:", EditProps.LABEL), 0.05f, 0.7f, 0.25f, 0.75f);
		addComponent(new DynamicTextButton("Click here to copy the invite link", EditProps.BUTTON, EditProps.HOVER, () -> {
					CommandBlockHelpOverview.setClipboard("https://discordapp.com/invite/bmF3Zvu");
		}), 0.05f, 0.65f, 0.25f, 0.7f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}