package nl.knokko.customitems.editor.menu.edit.item;

import java.awt.Color;
import java.util.Collection;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.set.CustomItem;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;
import nl.knokko.gui.util.TextBuilder.Properties;

public class ItemOverview extends GuiMenu {
	
	public static final Properties BUTTON_PROPERTIES = Properties.createButton(new Color(0, 100, 200), new Color(0, 25, 50));
	public static final Properties HOVER_PROPERTIES = Properties.createButton(new Color(0, 125, 250), new Color(0, 40, 80));
	
	protected final EditMenu menu;
	
	protected ItemList itemList;

	public ItemOverview(EditMenu menu) {
		this.menu = menu;
	}
	
	@Override
	public void init() {
		if(didInit) itemList.refresh();
		super.init();
	}

	@Override
	protected void addComponents() {
		itemList = new ItemList();
		addComponent(itemList, 0.3f, 0f, 1f, 1f);
		addComponent(new TextButton("Back", BUTTON_PROPERTIES, HOVER_PROPERTIES, () -> {
			state.getWindow().setMainComponent(menu);
		}), 0.05f, 0.7f, 0.2f, 0.8f);
		addComponent(new TextButton("Create item", BUTTON_PROPERTIES, HOVER_PROPERTIES, () -> {
			state.getWindow().setMainComponent(new ItemEdit(menu, null));
		}), 0.05f, 0.4f, 0.25f, 0.5f);
	}
	
	public static final Properties ITEM_LIST_NAME = Properties.createLabel();
	public static final Properties EDIT_PROPERTIES = Properties.createButton(new Color(0, 100, 200), new Color(0, 25, 50));
	public static final Properties EDIT_HOVER_PROPERTIES = Properties.createButton(new Color(0, 125, 250), new Color(0, 40, 80));
	public static final Properties DELETE_PROPERTIES = Properties.createButton(new Color(200, 0, 0), new Color(50, 0, 0));
	public static final Properties DELETE_HOVER_PROPERTIES = Properties.createButton(new Color(250, 0, 0), new Color(65, 0, 0));
	
	private class ItemList extends GuiMenu {

		@Override
		protected void addComponents() {
			refresh();
		}
		
		private void refresh() {
			components.clear();
			Collection<CustomItem> items = menu.getSet().getItems();
			int index = 0;
			for(CustomItem item : items) {
				float minY = 0.9f - index * 0.1f;
				float maxY = 1f - index * 0.1f;
				addComponent(new TextComponent(item.getName(), ITEM_LIST_NAME), 0f, minY, 0.5f, maxY);
				addComponent(new TextButton("Edit", EDIT_PROPERTIES, EDIT_HOVER_PROPERTIES, () -> {
					state.getWindow().setMainComponent(new ItemEdit(menu, item));
				}), 0.6f, minY, 0.7f, maxY);
				addComponent(new TextButton("Delete", DELETE_PROPERTIES, DELETE_HOVER_PROPERTIES, () -> {
					menu.getSet().removeItem(item);
					refresh();
				}), 0.8f, minY, 0.95f, maxY);
				index++;
			}
		}
	}
}