package nl.knokko.customitems.editor.menu.edit.item;

import java.awt.Color;
import java.util.Collection;

import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.NamedImage;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.simple.SimpleImageComponent;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.util.TextBuilder.Properties;

public class SelectTexture extends GuiMenu {
	
	public static final Properties BUTTON_PROPERTIES = Properties.createLabel(Color.BLACK, new Color(0, 200, 200));
	public static final Properties HOVER_PROPERTIES = Properties.createLabel(new Color(50, 50, 50), new Color(0, 250, 250));
	
	private final GuiComponent returnMenu;
	private final ReturnAction returnAction;
	private final ItemSet set;

	public SelectTexture(ItemSet set, GuiComponent returnMenu, ReturnAction returnAction) {
		this.returnMenu = returnMenu;
		this.returnAction = returnAction;
		this.set = set;
	}

	@Override
	protected void addComponents() {
		addComponent(new TextButton("Cancel", Properties.createButton(new Color(200, 150, 0), new Color(40, 30, 0)), Properties.createButton(new Color(250, 200, 0), new Color(50, 40, 0)), () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.1f, 0.6f, 0.3f, 0.7f);
		Collection<NamedImage> textures = set.getTextures();
		int index = 0;
		for(NamedImage texture : textures) {
			addComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(texture.getImage())), 0.4f, 0.9f - index * 0.15f, 0.5f, 1f - index * 0.15f);
			addComponent(new TextButton(texture.getName(), BUTTON_PROPERTIES, HOVER_PROPERTIES, () -> {
				returnAction.onSelect(texture);
				state.getWindow().setMainComponent(returnMenu);
			}), 0.55f, 0.9f - index * 0.15f, 0.85f, 1f - index * 0.15f);
			index++;
		}
	}
	
	public static interface ReturnAction {
		
		void onSelect(NamedImage texture);
	}
}