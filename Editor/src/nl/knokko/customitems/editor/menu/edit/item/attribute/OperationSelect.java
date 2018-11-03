package nl.knokko.customitems.editor.menu.edit.item.attribute;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.item.AttributeModifier.Operation;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;

public class OperationSelect extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final Receiver receiver;

	public OperationSelect(GuiComponent returnMenu, Receiver receiver) {
		this.returnMenu = returnMenu;
		this.receiver = receiver;
	}

	@Override
	protected void addComponents() {
		addComponent(new TextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		Operation[] values = Operation.values();
		for (int index = 0; index < values.length; index++) {
			Operation op = values[index];
			addComponent(new TextButton(op.toString(), EditProps.BUTTON, EditProps.HOVER, () -> {
				receiver.onSelect(op);
				state.getWindow().setMainComponent(returnMenu);
			}), 0.4f, 0.8f - index * 0.15f, 0.65f, 0.9f - index * 0.15f);
		}
	}
	
	public static interface Receiver {
		
		void onSelect(Operation operation);
	}
}