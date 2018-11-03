package nl.knokko.customitems.editor.menu.edit.item.attribute;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.AttributeModifier.Attribute;
import nl.knokko.customitems.item.AttributeModifier.Operation;
import nl.knokko.customitems.item.AttributeModifier.Slot;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextEditField;

public class AttributesOverview extends GuiMenu {
	
	private final AttributeModifier[] current;
	private final Receiver receiver;
	private final GuiComponent returnMenu;

	public AttributesOverview(AttributeModifier[] currentAttributes, Receiver receiver, GuiComponent returnMenu) {
		current = currentAttributes;
		this.receiver = receiver;
		this.returnMenu = returnMenu;
	}

	@Override
	protected void addComponents() {
		// TODO Auto-generated method stub

	}
	
	private class Entry extends GuiMenu {
		
		private Attribute attribute;
		private Slot slot;
		private Operation operation;
		private TextEditField valueField;
		
		private TextButton attributeButton;
		
		private Entry(AttributeModifier attribute) {
			this.attribute = attribute.getAttribute();
			this.slot = attribute.getSlot();
			this.operation = attribute.getOperation();
			this.valueField = new TextEditField(attribute.getValue() + "", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		}

		@Override
		protected void addComponents() {
			attributeButton = new TextButton(attribute.getName(), EditProps.BUTTON, EditProps.HOVER, () -> {
				state.getWindow().setMainComponent(new AttributeSelect((Attribute newAttribute) -> {
					this.attribute = newAttribute;
					attributeButton.setText(attribute.getName());
				}, AttributesOverview.this));
			});
			addComponent(attributeButton, 0.05f, 0f, 0.3f, 1f);
			addComponent(valueField, 0.8f, 0f, 0.9f, 1f);
		}
	}
	
	public static interface Receiver {
		
		void onComplete(AttributeModifier[] newAttributes);
	}
}