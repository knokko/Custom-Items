package nl.knokko.customitems.editor.menu.edit.select.item;

import java.util.Collection;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;

public class SelectCustomItem extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final Receiver receiver;
	private final ItemSet set;

	public SelectCustomItem(GuiComponent returnMenu, Receiver receiver, ItemSet set) {
		this.returnMenu = returnMenu;
		this.receiver = receiver;
		this.set = set;
	}

	@Override
	protected void addComponents() {
		addComponent(new TextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		
		Collection<CustomItem> items = set.getItems();
		int index = 0;
		for (CustomItem item : items) {
			addComponent(new TextButton(item.getName(), EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				receiver.onSelect(item);
				state.getWindow().setMainComponent(returnMenu);
			}), 0.35f, 0.9f - index * 0.1f, 0.7f, 1f - index * 0.1f);
			index++;
		}
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	public static interface Receiver {
		
		void onSelect(CustomItem item);
	}
}