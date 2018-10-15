package nl.knokko.customitems.editor.menu.main;

import java.awt.Color;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.util.TextBuilder.Properties;

public class MainMenu extends GuiMenu {
	
	public static final MainMenu INSTANCE = new MainMenu();
	public static final GuiColor BACKGROUND = new SimpleGuiColor(200, 150, 0);
	
	public static final Properties BUTTON_PROPERTIES = Properties.createButton(new Color(0, 50, 200), new Color(0, 20, 80));
	public static final Properties HOVER_PROPERTIES = Properties.createButton(new Color(0, 65, 250), new Color(0, 30, 100));

	@Override
	protected void addComponents() {
		addComponent(new TextButton("New item set", BUTTON_PROPERTIES, HOVER_PROPERTIES, () ->  {
			state.getWindow().setMainComponent(CreateMenu.INSTANCE);
		}), 0.3f, 0.65f, 0.7f, 0.8f);
		addComponent(new TextButton("Edit item set", BUTTON_PROPERTIES, HOVER_PROPERTIES, () -> {
			state.getWindow().setMainComponent(new LoadMenu());
		}), 0.3f, 0.45f, 0.7f, 0.6f);
		addComponent(new TextButton("Exit editor", BUTTON_PROPERTIES, HOVER_PROPERTIES, () -> {
			state.getWindow().close();
		}), 0.3f, 0.2f, 0.7f, 0.35f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return BACKGROUND;
	}
}