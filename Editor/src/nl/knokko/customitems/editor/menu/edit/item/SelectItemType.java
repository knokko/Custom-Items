package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.item.ItemType;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;

public class SelectItemType extends GuiMenu {
	
	private final ReturnAction returnAction;
	private final GuiComponent returnMenu;
	
	public SelectItemType(GuiComponent returnMenu, ReturnAction returnAction) {
		this.returnMenu = returnMenu;
		this.returnAction = returnAction;
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		addComponent(new TextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_ACTIVE, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.1f, 0.6f, 0.3f, 0.7f);
		ItemType[] types = ItemType.values();
		for(int index = 0; index < types.length; index++) {
			final ItemType type = types[index];
			addComponent(new TextButton(type.toString(), EditProps.SELECT_BASE, EditProps.SAVE_HOVER, () -> {
				returnAction.onSelect(type);
				state.getWindow().setMainComponent(returnMenu);
			}), 0.5f, 0.9f - index * 0.1f, 0.8f, 1 - index * 0.1f);
		}
	}
	
	public static interface ReturnAction {
		
		void onSelect(ItemType type);
	}
}