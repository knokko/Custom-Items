package nl.knokko.customitems.editor.menu.edit.container.slot;

import java.util.function.Consumer;

import nl.knokko.customitems.container.slot.CustomSlot;
import nl.knokko.customitems.container.slot.InputCustomSlot;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class CreateInputSlot extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final Consumer<CustomSlot> submitSlot;
	private final Iterable<CustomSlot> existingSlots;
	
	public CreateInputSlot(GuiComponent returnMenu, Consumer<CustomSlot> submitSlot,
			Iterable<CustomSlot> existingSlots) {
		this.returnMenu = returnMenu;
		this.submitSlot = submitSlot;
		this.existingSlots = existingSlots;
	}

	@Override
	protected void addComponents() {
		DynamicTextComponent errorComponent = new DynamicTextComponent("", EditProps.ERROR);
		addComponent(errorComponent, 0.025f, 0.9f, 0.975f, 1f);
		
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.15f, 0.8f);
		
		TextEditField nameField = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		addComponent(new DynamicTextComponent("Name:", EditProps.LABEL), 0.25f, 0.7f, 0.35f, 0.75f);
		addComponent(nameField, 0.375f, 0.7f, 0.5f, 0.75f);
		
		addComponent(new DynamicTextButton("Done", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			
			if (nameField.getText().isEmpty()) {
				errorComponent.setText("You need to choose a name");
				return;
			}
			
			for (CustomSlot existingSlot : existingSlots) {
				if (existingSlot instanceof InputCustomSlot) {
					InputCustomSlot existingInputSlot = (InputCustomSlot) existingSlot;
					if (existingInputSlot.getName().equals(nameField.getText())) {
						errorComponent.setText("There is already an input slot with name " + nameField.getText());
						return;
					}
				}
			}
			
			submitSlot.accept(new InputCustomSlot(nameField.getText()));
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.3f, 0.15f, 0.4f);
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
