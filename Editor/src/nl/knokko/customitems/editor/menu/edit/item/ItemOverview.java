package nl.knokko.customitems.editor.menu.edit.item;

import java.util.Collection;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.item.CustomTool;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;

public class ItemOverview extends GuiMenu {
	
	protected final EditMenu menu;
	private final TextComponent errorComponent;
	
	protected ItemList itemList;

	public ItemOverview(EditMenu menu) {
		this.menu = menu;
		errorComponent = new TextComponent("", EditProps.ERROR);
	}
	
	@Override
	public void init() {
		if(didInit) itemList.refresh();
		super.init();
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		itemList = new ItemList();
		addComponent(itemList, 0.3f, 0f, 1f, 0.9f);
		addComponent(new TextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(menu);
		}), 0.05f, 0.7f, 0.2f, 0.8f);
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
		addComponent(new TextButton("Create item", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new CreateItem(menu));
		}), 0.05f, 0.4f, 0.25f, 0.5f);
	}
	
	private class ItemList extends GuiMenu {

		@Override
		protected void addComponents() {
			refresh();
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND;
		}
		
		private void refresh() {
			clearComponents();
			Collection<CustomItem> items = menu.getSet().getItems();
			int index = 0;
			for(CustomItem item : items) {
				float minY = 0.9f - index * 0.1f;
				float maxY = 1f - index * 0.1f;
				addComponent(new TextComponent(item.getName(), EditProps.LABEL), 0f, minY, 0.5f, maxY);
				addComponent(new TextButton("Edit", EditProps.BUTTON, EditProps.HOVER, () -> {
					if (item instanceof CustomTool)
						state.getWindow().setMainComponent(new EditItemTool(menu, (CustomTool) item, item.getItemType().getMainCategory()));
					else
						state.getWindow().setMainComponent(new EditItemSimple(menu, item));
				}), 0.6f, minY, 0.7f, maxY);
				addComponent(new TextButton("Delete", EditProps.QUIT_BASE, EditProps.QUIT_HOVER, () -> {
					String error = menu.getSet().removeItem(item);
					if (error != null)
						errorComponent.setText(error);
					else
						refresh();
				}), 0.8f, minY, 0.95f, maxY);
				index++;
			}
		}
	}
}