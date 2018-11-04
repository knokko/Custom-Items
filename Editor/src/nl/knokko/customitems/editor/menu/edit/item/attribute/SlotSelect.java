package nl.knokko.customitems.editor.menu.edit.item.attribute;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.item.AttributeModifier.Slot;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;

public class SlotSelect extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final Receiver receiver;

	public SlotSelect(GuiComponent returnMenu, Receiver receiver) {
		this.returnMenu = returnMenu;
		this.receiver = receiver;
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		addComponent(new TextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		Slot[] values = Slot.values();
		for (int index = 0; index < values.length; index++) {
			Slot slot = values[index];
			addComponent(new TextButton(slot.getSlot(), EditProps.BUTTON, EditProps.HOVER, () -> {
				receiver.onSelect(slot);
				state.getWindow().setMainComponent(returnMenu);
			}), 0.4f, 0.8f - index * 0.125f, 0.6f, 0.9f - index * 0.125f);
		}
	}
	
	public static interface Receiver {
		
		void onSelect(Slot slot);
	}
}