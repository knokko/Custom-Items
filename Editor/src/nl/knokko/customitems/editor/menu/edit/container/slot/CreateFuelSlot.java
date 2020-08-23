package nl.knokko.customitems.editor.menu.edit.container.slot;

import java.util.function.Consumer;

import nl.knokko.customitems.container.fuel.CustomFuelRegistry;
import nl.knokko.customitems.container.slot.CustomSlot;
import nl.knokko.customitems.container.slot.FuelCustomSlot;
import nl.knokko.customitems.editor.HelpButtons;
import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.container.fuel.EditFuelRegistry;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class CreateFuelSlot extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final Consumer<CustomSlot> submitSlot;
	private final ItemSet set;
	private final Iterable<CustomSlot> existingSlots;
	
	public CreateFuelSlot(GuiComponent returnMenu, Consumer<CustomSlot> submitSlot,
			ItemSet set, Iterable<CustomSlot> existingSlots) {
		this.returnMenu = returnMenu;
		this.submitSlot = submitSlot;
		this.set = set;
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
		
		CustomFuelRegistry[] pChosenRegistry = { null };
		
		addComponent(new DynamicTextComponent("Registry:", EditProps.LABEL), 0.25f, 0.6f, 0.4f, 0.65f);
		addComponent(CollectionSelect.createButton(
				set.getBackingFuelRegistries(), chosenRegistry -> {
					pChosenRegistry[0] = chosenRegistry;
				}, registry -> registry == null ? "Select..." : registry.getName(), 
				null
			), 0.425f, 0.6f, 0.55f, 0.65f
		);
		addComponent(new DynamicTextButton("Create new", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditFuelRegistry(this, set, null, null));
		}), 0.6f, 0.6f, 0.75f, 0.65f);
		
		addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			if (nameField.getText().isEmpty()) {
				errorComponent.setText("You need to give this fuel slot a name");
				return;
			}
			for (CustomSlot existingSlot : existingSlots) {
				if (existingSlot instanceof FuelCustomSlot) {
					FuelCustomSlot existingFuelSlot = (FuelCustomSlot) existingSlot;
					if (existingFuelSlot.getName().equals(nameField.getText())) {
						errorComponent.setText("There is an existing fuel slot with name " + nameField.getText());
						return;
					}
				}
			}
			if (pChosenRegistry[0] == null) {
				errorComponent.setText("You need to select a fuel registry");
				return;
			}
			submitSlot.accept(new FuelCustomSlot(nameField.getText(), pChosenRegistry[0]));
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		HelpButtons.addHelpLink(this, "edit menu/containers/slots/fuel.html");
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
