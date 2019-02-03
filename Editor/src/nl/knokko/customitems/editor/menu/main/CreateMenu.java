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
import java.io.File;
import java.io.IOException;

import nl.knokko.customitems.editor.Editor;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.util.TextBuilder.Properties;

public class CreateMenu extends GuiMenu {
	
	public static final CreateMenu INSTANCE = new CreateMenu();
	
	public static final Properties LABEL_PROPERTIES = Properties.createLabel();
	
	private TextEditField fileName;
	private TextComponent errorComponent;
	
	@Override
	public GuiColor getBackgroundColor() {
		return MainMenu.BACKGROUND;
	}
	
	@Override
	protected void addComponents() {
		fileName = new TextEditField("", Properties.createEdit(), Properties.createEdit(new Color(200, 200, 255), new Color(0, 200, 200), new Color(0, 200, 200)));
		errorComponent = new TextComponent("", Properties.createLabel(Color.RED));
		addComponent(errorComponent, 0.1f, 0.8f, 0.9f, 1);
		addComponent(new TextComponent("Filename: ", LABEL_PROPERTIES), 0.2f, 0.5f, 0.4f, 0.6f);
		addComponent(fileName, 0.45f, 0.5f, 0.75f, 0.6f);
		addComponent(new TextButton("Cancel", Properties.createButton(new Color(200, 100, 0), new Color(50, 25, 0)), Properties.createButton(new Color(250, 125, 0), new Color(60, 30, 0)), () -> {
			state.getWindow().setMainComponent(MainMenu.INSTANCE);
		}), 0.1f, 0.65f, 0.3f, 0.75f);
		addComponent(new TextButton("Create", Properties.createButton(new Color(200, 100, 0), new Color(50, 25, 0)), Properties.createButton(new Color(250, 125, 0), new Color(60, 30, 0)), () -> {
			String error = testFileName(fileName.getText() + ".cisb");
			if(error != null)
				errorComponent.setText(error);
			else
				state.getWindow().setMainComponent(new EditMenu(new ItemSet(fileName.getText())));
		}), 0.35f, 0.25f, 0.65f, 0.35f);
	}
	
	public static String testFileName(String name) {
		if(name.isEmpty())
			return "File name can't be empty";
		String result;
		File file = new File(Editor.getFolder() + "/" + name);
		try {
			if(file.createNewFile()) {
				result = null;
			} else {
				result = "File already exists";
			}
		} catch(IOException ioex) {
			result = "An IO error occured during the write test: " + ioex.getMessage();
		} catch(SecurityException se) {
			result = "It looks like this application doesn't have the rights to create this file.";
		}
		file.delete();
		return result;
	}
}