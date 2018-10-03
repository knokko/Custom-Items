package nl.knokko.customitems.editor.menu.edit;

import java.awt.Color;

import nl.knokko.customitems.editor.menu.edit.item.ItemOverview;
import nl.knokko.customitems.editor.menu.edit.texture.TextureOverview;
import nl.knokko.customitems.editor.menu.main.MainMenu;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.util.TextBuilder.Properties;

public class EditMenu extends GuiMenu {
	
	public static final Properties SAVE_BUTTON_PROPERTIES = Properties.createButton(new Color(0, 200, 0), new Color(0, 50, 0));
	public static final Properties SAVE_HOVER_PROPERTIES = Properties.createButton(new Color(0, 250, 0), new Color(0, 65, 0));
	
	public static final Properties BUTTON_PROPERTIES = Properties.createButton(new Color(0, 200, 200), new Color(0, 50, 50));
	public static final Properties HOVER_PROPERTIES = Properties.createButton(new Color(0, 250, 250), new Color(0, 65, 65));
	
	protected final ItemSet set;
	
	protected ItemOverview itemOverview;
	protected TextureOverview textureOverview;

	public EditMenu(ItemSet set) {
		this.set = set;
	}
	
	public ItemSet getSet() {
		return set;
	}
	
	public ItemOverview getItemOverview() {
		return itemOverview;
	}
	
	public TextureOverview getTextureOverview() {
		return textureOverview;
	}

	@Override
	protected void addComponents() {
		itemOverview = new ItemOverview(this);
		textureOverview = new TextureOverview(this);
		addComponent(new TextButton("Quit", Properties.createButton(new Color(200, 0, 0), new Color(50, 0, 0)), Properties.createButton(new Color(250, 0, 0), new Color(65, 0, 0)), () -> {
			state.getWindow().setMainComponent(MainMenu.INSTANCE);
		}), 0.1f, 0.7f, 0.3f, 0.8f);
		addComponent(new TextButton("Save", SAVE_BUTTON_PROPERTIES, SAVE_HOVER_PROPERTIES, () -> {
			set.save();
		}), 0.1f, 0.4f, 0.25f, 0.5f);
		addComponent(new TextButton("Save and quit", SAVE_BUTTON_PROPERTIES, SAVE_HOVER_PROPERTIES, () -> {
			set.save();
			state.getWindow().setMainComponent(MainMenu.INSTANCE);
		}), 0.1f, 0.25f, 0.35f, 0.35f);
		addComponent(new TextButton("Items", BUTTON_PROPERTIES, HOVER_PROPERTIES, () -> {
			state.getWindow().setMainComponent(itemOverview);
		}), 0.6f, 0.75f, 0.8f, 0.85f);
		addComponent(new TextButton("Textures", BUTTON_PROPERTIES, HOVER_PROPERTIES, () -> {
			state.getWindow().setMainComponent(textureOverview);
		}), 0.6f, 0.6f, 0.8f, 0.7f);
	}
}