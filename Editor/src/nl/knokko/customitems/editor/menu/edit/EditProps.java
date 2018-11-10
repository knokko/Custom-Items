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
package nl.knokko.customitems.editor.menu.edit;

import java.awt.Color;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.util.TextBuilder.Properties;

public class EditProps {
	
	public static final Properties ERROR = Properties.createLabel(Color.RED, 2048, 256);
	
	public static final Properties SAVE_BASE = Properties.createButton(new Color(0, 200, 0), new Color(0, 50, 0));
	public static final Properties SAVE_HOVER = Properties.createButton(new Color(0, 255, 0), new Color(0, 65, 0));
	
	public static final Properties QUIT_BASE = Properties.createButton(new Color(200, 0, 0), new Color(50, 0, 0));
	public static final Properties QUIT_HOVER = Properties.createButton(new Color(250, 0, 0), new Color(65, 0, 0));
	
	public static final Properties CANCEL_BASE = Properties.createButton(new Color(200, 200, 0), new Color(50, 50, 0));
	public static final Properties CANCEL_HOVER = Properties.createButton(new Color(255, 255, 0), new Color(65, 65, 0));
	
	public static final Properties BUTTON = Properties.createButton(new Color(0, 200, 200), new Color(0, 50, 50));
	public static final Properties HOVER = Properties.createButton(new Color(0, 255, 255), new Color(0, 65, 65));
	
	public static final Properties LABEL = Properties.createLabel();
	
	public static final Properties EDIT_BASE = Properties.createEdit(new Color(200, 200, 200), new Color(10, 30, 30));
	public static final Properties EDIT_ACTIVE = Properties.createEdit(new Color(255, 255, 255), new Color(100, 255, 255));
	
	public static final Properties SELECT_BASE = Properties.createLabel(Color.BLACK, new Color(0, 200, 200), 512, 128);
	public static final Properties SELECT_HOVER = Properties.createLabel(new Color(50, 50, 50), new Color(0, 250, 250), 512, 128);
	public static final Properties SELECT_ACTIVE = Properties.createLabel(new Color(70, 70, 70), new Color(50, 255, 255), 512, 128);
	
	public static final Properties CHOOSE_BASE = Properties.createButton(new Color(200, 0, 200), new Color(50, 0, 50));
	public static final Properties CHOOSE_HOVER = Properties.createButton(new Color(255, 0, 255), new Color(65, 0, 65));
	
	public static final GuiColor BACKGROUND = new SimpleGuiColor(0, 0, 200);
}