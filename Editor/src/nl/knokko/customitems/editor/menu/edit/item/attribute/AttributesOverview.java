package nl.knokko.customitems.editor.menu.edit.item.attribute;

import java.util.List;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.AttributeModifier.Attribute;
import nl.knokko.customitems.item.AttributeModifier.Operation;
import nl.knokko.customitems.item.AttributeModifier.Slot;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.texture.loader.GuiTextureLoader;

public class AttributesOverview extends GuiMenu {
	
	private static final AttributeModifier EXAMPLE_MODIFIER = new AttributeModifier(Attribute.ATTACK_DAMAGE, Slot.MAINHAND, Operation.ADD, 7);
	
	private final AttributeModifier[] current;
	private final Receiver receiver;
	private final GuiComponent returnMenu;
	
	private final TextComponent errorComponent;
	
	private GuiTexture deleteBase;
	private GuiTexture deleteHover;

	public AttributesOverview(AttributeModifier[] currentAttributes, GuiComponent returnMenu, Receiver receiver) {
		this.current = currentAttributes;
		this.receiver = receiver;
		this.returnMenu = returnMenu;
		this.errorComponent = new TextComponent("", EditProps.ERROR);
	}

	@Override
	protected void addComponents() {
		GuiTextureLoader loader = state.getWindow().getTextureLoader();
		deleteBase = loader.loadTexture("nl/knokko/gui/images/icons/delete.png");
		deleteHover = loader.loadTexture("nl/knokko/gui/images/icons/delete_hover.png");
		addComponent(new TextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.05f, 0.8f, 0.2f, 0.9f);
		addComponent(new TextButton("New Attribute", EditProps.BUTTON, EditProps.HOVER, () -> {
			float y = 0.8f - (getComponents().size() - 4) * 0.125f;
			addComponent(new Entry(EXAMPLE_MODIFIER), 0.4f, y, 1f, y + 0.1f);
		}), 0.05f, 0.5f, 0.3f, 0.6f);
		addComponent(new TextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			List<SubComponent> components = getComponents();
			AttributeModifier[] result = new AttributeModifier[components.size() - 4];
			int index = 0;
			for (SubComponent component : components) {
				if (component.getComponent() instanceof Entry) {
					Entry entry = (Entry) component.getComponent();
					try {
						double value = Double.parseDouble(entry.valueField.getText());
						result[index++] = new AttributeModifier(entry.attribute, entry.slot, entry.operation, value);
					} catch (NumberFormatException ex) {
						errorComponent.setText("All values must be numbers");
						return;
					}
				}
			}
			receiver.onComplete(result);
			state.getWindow().setMainComponent(returnMenu);
		}), 0.05f, 0.2f, 0.2f, 0.3f);
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
		for (int index = 0; index < current.length; index++) {
			float y = 0.8f - index * 0.125f;
			addComponent(new Entry(current[index]), 0.4f, y, 1f, y + 0.1f);
		}
	}
	
	private class Entry extends GuiMenu {
		
		private Attribute attribute;
		private Slot slot;
		private Operation operation;
		private TextEditField valueField;
		
		private TextButton attributeButton;
		private TextButton slotButton;
		private TextButton operationButton;
		
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
			slotButton = new TextButton(slot.getSlot(), EditProps.BUTTON, EditProps.HOVER, () -> {
				state.getWindow().setMainComponent(new SlotSelect(AttributesOverview.this, (Slot slot) -> {
					this.slot = slot;
					slotButton.setText(slot.getSlot());
				}));
			});
			operationButton = new TextButton(operation.toString(), EditProps.BUTTON, EditProps.HOVER, () -> {
				state.getWindow().setMainComponent(new OperationSelect(AttributesOverview.this, (Operation operation) -> {
					this.operation = operation;
					operationButton.setText(operation.toString());
				}));
			});
			addComponent(new ImageButton(deleteBase, deleteHover, () -> {
				AttributesOverview.this.removeComponent(this);
			}), 0.025f, 0f, 0.075f, 1f);
			addComponent(attributeButton, 0.1f, 0f, 0.35f, 1f);
			addComponent(slotButton, 0.375f, 0f, 0.5f, 1f);
			addComponent(operationButton, 0.55f, 0f, 0.75f, 1f);
			addComponent(new TextComponent("Value: ", EditProps.LABEL), 0.775f, 0f, 0.85f, 1f);
			addComponent(valueField, 0.875f, 0f, 0.975f, 1f);
		}
	}
	
	public static interface Receiver {
		
		void onComplete(AttributeModifier[] newAttributes);
	}
}