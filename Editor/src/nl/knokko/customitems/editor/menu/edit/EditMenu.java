package nl.knokko.customitems.editor.menu.edit;

import java.awt.Color;

import nl.knokko.customitems.editor.menu.edit.item.ItemOverview;
import nl.knokko.customitems.editor.menu.edit.texture.TextureOverview;
import nl.knokko.customitems.editor.menu.main.MainMenu;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.util.TextBuilder.Properties;

public class EditMenu extends GuiMenu {
	
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
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		itemOverview = new ItemOverview(this);
		textureOverview = new TextureOverview(this);
		addComponent(new TextButton("Quit", Properties.createButton(new Color(200, 0, 0), new Color(50, 0, 0)), Properties.createButton(new Color(250, 0, 0), new Color(65, 0, 0)), () -> {
			state.getWindow().setMainComponent(MainMenu.INSTANCE);
		}), 0.1f, 0.7f, 0.3f, 0.8f);
		addComponent(new TextButton("Save", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			set.save();
		}), 0.1f, 0.4f, 0.25f, 0.5f);
		addComponent(new TextButton("Save and quit", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			set.save();
			state.getWindow().setMainComponent(MainMenu.INSTANCE);
		}), 0.1f, 0.25f, 0.35f, 0.35f);
		addComponent(new TextButton("Items", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(itemOverview);
		}), 0.6f, 0.75f, 0.8f, 0.85f);
		addComponent(new TextButton("Textures", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(textureOverview);
		}), 0.6f, 0.6f, 0.8f, 0.7f);
	}
}