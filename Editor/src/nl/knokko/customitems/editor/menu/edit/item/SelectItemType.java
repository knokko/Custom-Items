package nl.knokko.customitems.editor.menu.edit.item;

import java.awt.Color;

import nl.knokko.customitems.item.ItemType;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.util.TextBuilder.Properties;

public class SelectItemType extends GuiMenu {
	
	public static final Properties BUTTON_PROPERTIES = Properties.createLabel(Color.BLACK, new Color(0, 200, 200));
	public static final Properties HOVER_PROPERTIES = Properties.createLabel(new Color(50, 50, 50), new Color(0, 250, 250));
	
	private final ReturnAction returnAction;
	private final GuiComponent returnMenu;
	
	public SelectItemType(GuiComponent returnMenu, ReturnAction returnAction) {
		this.returnMenu = returnMenu;
		this.returnAction = returnAction;
	}

	@Override
	protected void addComponents() {
		addComponent(new TextButton("Cancel", Properties.createButton(new Color(200, 150, 0), new Color(40, 30, 0)), Properties.createButton(new Color(250, 200, 0), new Color(50, 40, 0)), () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.1f, 0.6f, 0.3f, 0.7f);
		ItemType[] types = ItemType.values();
		for(int index = 0; index < types.length; index++) {
			final ItemType type = types[index];
			addComponent(new TextButton(type.toString(), BUTTON_PROPERTIES, HOVER_PROPERTIES, () -> {
				returnAction.onSelect(type);
				state.getWindow().setMainComponent(returnMenu);
			}), 0.5f, 0.9f - index * 0.1f, 0.8f, 1 - index * 0.1f);
		}
	}
	
	public static interface ReturnAction {
		
		void onSelect(ItemType type);
	}
}