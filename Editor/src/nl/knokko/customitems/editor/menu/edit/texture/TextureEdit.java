package nl.knokko.customitems.editor.menu.edit.texture;

import java.awt.Color;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.set.NamedImage;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;
import nl.knokko.gui.util.TextBuilder.Properties;

public class TextureEdit extends GuiMenu {
	
	protected final EditMenu menu;
	protected final NamedImage previous;
	protected final TextComponent errorComponent;

	public TextureEdit(EditMenu menu, NamedImage previous) {
		this.menu = menu;
		this.previous = previous;
		errorComponent = new TextComponent("", Properties.createLabel(Color.RED));
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return SimpleGuiColor.GREEN;
	}

	@Override
	protected void addComponents() {
		addComponent(errorComponent, 0.1f, 0.9f, 0.9f, 1f);
		addComponent(new TextButton("Cancel", Properties.createButton(new Color(200, 0, 0), new Color(50, 0, 0)), Properties.createButton(new Color(250, 0, 0), new Color(65, 0, 0)), () -> {
			state.getWindow().setMainComponent(menu.getTextureOverview());
		}), 0.1f, 0.7f, 0.25f, 0.8f);
	}
}