package nl.knokko.customitems.editor.menu.edit.drops.mob;

import nl.knokko.customitems.drops.CIEntityType;
import nl.knokko.customitems.drops.Drop;
import nl.knokko.customitems.drops.EntityDrop;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.drops.SelectDrop;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditMobDrop extends GuiMenu {
	
	private final ItemSet set;
	private final GuiComponent returnMenu;
	private final EntityDrop drop;
	
	private final NameField nameField;
	private final CheckboxComponent requiresName;
	
	private Drop selectedDrop;
	private CIEntityType selectedType;

	public EditMobDrop(ItemSet set, GuiComponent returnMenu, EntityDrop drop) {
		this.set = set;
		this.returnMenu = returnMenu;
		this.drop = drop;
		if (drop == null) {
			selectedDrop = null;
			selectedType = CIEntityType.ZOMBIE;
			nameField = new NameField("");
			requiresName = new CheckboxComponent(false);
		} else {
			selectedDrop = drop.getDrop();
			selectedType = drop.getEntityType();
			if (drop.getRequiredName() == null) {
				nameField = new NameField("");
				requiresName = new CheckboxComponent(false);
			}
			else {
				nameField = new NameField(drop.getRequiredName());
				requiresName = new CheckboxComponent(true);
			}
		}
	}

	@Override
	protected void addComponents() {
		
		DynamicTextComponent errorComponent = new DynamicTextComponent("", EditProps.ERROR);
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
		
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.8f, 0.2f, 0.9f);
		
		DynamicTextComponent[] changeButtons = { null, null };
		
		addComponent(new DynamicTextComponent("Drop:", EditProps.LABEL), 0.3f, 0.7f, 0.45f, 0.8f);
		SelectDrop selectDrop = new SelectDrop(set, this, selectedDrop, (Drop newDrop) -> {
			selectedDrop = newDrop;
			changeButtons[0].setText(newDrop.toString());
		});
		changeButtons[0] = new DynamicTextButton(selectedDrop + "", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(selectDrop);
		});
		addComponent(changeButtons[0], 0.5f, 0.7f, 0.8f, 0.8f);
		
		addComponent(new DynamicTextComponent("Entity:", EditProps.LABEL), 0.28f, 0.5f, 0.45f, 0.6f);
		/*
		SelectEntityType selectEntity = new SelectEntityType(this, (CIEntityType newType) -> {
			selectedType = newType;
			changeButtons[1].setText(newType.toString());
		});
		changeButtons[1] = new DynamicTextButton(selectedType + "", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(selectEntity);
		});
		addComponent(changeButtons[1], 0.5f, 0.5f, 0.7f, 0.6f);*/
		addComponent(EnumSelect.createSelectButton(CIEntityType.class, (CIEntityType newType) -> {
			selectedType = newType;
		}, selectedType), 0.5f, 0.5f, 0.7f, 0.6f);
		
		addComponent(requiresName, 0.25f, 0.35f, 0.275f, 0.375f);
		addComponent(new DynamicTextComponent("Requires specific name", EditProps.LABEL), 0.3f, 0.3f, 0.55f, 0.4f);
		addComponent(nameField, 0.6f, 0.3f, 0.8f, 0.4f);
		
		DynamicTextButton doneButton;
		if (drop == null) {
			doneButton = new DynamicTextButton("Create", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				if (selectedDrop == null) {
					errorComponent.setText("You need to choose a custom item to drop");
					return;
				}
				String error = set.addMobDrop(new EntityDrop(selectedType, 
						requiresName.isChecked() ? nameField.getComponent().getText() : null, selectedDrop));
				if (error == null) {
					state.getWindow().setMainComponent(returnMenu);
				} else {
					errorComponent.setText(error);
				}
			});
		} else {
			doneButton = new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				String error = set.changeMobDrop(drop, selectedType, requiresName.isChecked() ? nameField.getComponent().getText() : null, selectedDrop);
				if (error == null)
					state.getWindow().setMainComponent(returnMenu);
				else 
					errorComponent.setText(error);
			});
		}
		addComponent(doneButton, 0.025f, 0.1f, 0.2f, 0.2f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	private class NameField extends WrapperComponent<TextEditField> {
		
		public NameField(String initialText) {
			super(new TextEditField(initialText, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE), EditProps.BACKGROUND);;
		}

		@Override
		public boolean isActive() {
			return requiresName.isChecked();
		}
	}
}
