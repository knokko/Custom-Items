package nl.knokko.customitems.editor.menu.edit.texture;

import java.awt.Color;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.util.TextBuilder.Properties;

public class TextureOverview extends GuiMenu {
	
	public static final Properties BUTTON_PROPERTIES = Properties.createButton(new Color(0, 100, 200), new Color(0, 25, 50));
	public static final Properties HOVER_PROPERTIES = Properties.createButton(new Color(0, 125, 250), new Color(0, 40, 80));
	
	protected final EditMenu menu;
	protected final TextureList textureList;

	public TextureOverview(EditMenu menu) {
		this.menu = menu;
		textureList = new TextureList();
	}
	
	@Override
	public void init() {
		if(didInit) textureList.refresh();
	}

	@Override
	protected void addComponents() {
		addComponent(textureList, 0.3f, 0f, 1f, 1f);
		addComponent(new TextButton("Back", BUTTON_PROPERTIES, HOVER_PROPERTIES, () -> {
			state.getWindow().setMainComponent(menu);
		}), 0.05f, 0.7f, 0.2f, 0.8f);
		addComponent(new TextButton("Create texture", BUTTON_PROPERTIES, HOVER_PROPERTIES, () -> {
			state.getWindow().setMainComponent(new TextureEdit(menu, null));
		}), 0.05f, 0.4f, 0.25f, 0.5f);
	}
	
	private class TextureList extends GuiMenu {

		@Override
		protected void addComponents() {
			refresh();
		}
		
		private void refresh() {
			
		}
	}
}