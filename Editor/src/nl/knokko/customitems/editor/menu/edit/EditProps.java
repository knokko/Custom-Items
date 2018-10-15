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
	public static final Properties CANCEL_ACTIVE = Properties.createButton(new Color(255, 255, 0), new Color(65, 65, 0));
	
	public static final Properties BUTTON = Properties.createButton(new Color(0, 200, 200), new Color(0, 50, 50));
	public static final Properties HOVER = Properties.createButton(new Color(0, 255, 255), new Color(0, 65, 65));
	
	public static final Properties LABEL = Properties.createLabel();
	
	public static final Properties EDIT_BASE = Properties.createEdit(new Color(200, 200, 200), new Color(10, 30, 30));
	public static final Properties EDIT_ACTIVE = Properties.createEdit(new Color(255, 255, 255), new Color(100, 255, 255));
	
	public static final Properties SELECT_BASE = Properties.createLabel(Color.BLACK, new Color(0, 200, 200));
	public static final Properties SELECT_HOVER = Properties.createLabel(new Color(50, 50, 50), new Color(0, 250, 250));
	
	public static final Properties CHOOSE_BASE = Properties.createButton(new Color(200, 0, 200), new Color(50, 0, 50));
	public static final Properties CHOOSE_HOVER = Properties.createButton(new Color(255, 0, 255), new Color(65, 0, 65));
	
	public static final GuiColor BACKGROUND = new SimpleGuiColor(0, 0, 200);
}