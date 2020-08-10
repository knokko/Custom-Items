package nl.knokko.customitems.editor.menu.edit.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.container.VanillaContainerType;
import nl.knokko.customitems.container.fuel.FuelMode;
import nl.knokko.customitems.container.slot.CustomSlot;
import nl.knokko.customitems.container.slot.EmptyCustomSlot;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.recipe.ContainerRecipe;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditContainer extends GuiMenu {
	
	private final EditMenu menu;
	private final CustomContainer toModify;
	
	private final Collection<ContainerRecipe> recipes;
	private CustomSlot[][] slots;
	private final TextEditField nameField;
	private final TextEditField displayNameField;
	private final CheckboxComponent persistentStorage;
	private FuelMode fuelMode;
	private VanillaContainerType vanillaType;
	
	public EditContainer(EditMenu menu, 
			CustomContainer oldValues, CustomContainer toModify) {
		this.menu = menu;
		this.toModify = toModify;
		
		String initialName;
		String initialDisplayName;
		boolean initialPersistentStorage;
		this.recipes = new ArrayList<>();
		if (oldValues != null) {
			initialName = oldValues.getName();
			initialDisplayName = oldValues.getDisplayName();
			initialPersistentStorage = oldValues.hasPersistentStorage();
			this.fuelMode = oldValues.getFuelMode();
			this.vanillaType = oldValues.getVanillaType();
			// Add all recipes from oldValues
			// Perform a deep copy because container recipes are mutable
			for (ContainerRecipe recipe : oldValues.getRecipes()) {
				this.recipes.add(recipe.clone());
			}
			this.slots = new CustomSlot[9][oldValues.getHeight()];
			for (int x = 0; x < 9; x++) {
				for (int y = 0; y < oldValues.getHeight(); y++) {
					this.slots[x][y] = oldValues.getSlot(x, y);
				}
			}
		} else {
			initialName = "";
			initialDisplayName = "";
			initialPersistentStorage = true;
			this.fuelMode = FuelMode.ALL;
			this.vanillaType = VanillaContainerType.FURNACE;
			// Keep this.recipes empty
			this.slots = new CustomSlot[9][3];
			for (CustomSlot[] column : this.slots) {
				Arrays.fill(column, new EmptyCustomSlot());
			}
		}
		
		this.nameField = new TextEditField(initialName, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		this.displayNameField = new TextEditField(initialDisplayName, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		this.persistentStorage = new CheckboxComponent(initialPersistentStorage);
	}

	@Override
	protected void addComponents() {
		DynamicTextComponent errorComponent = new DynamicTextComponent("", EditProps.ERROR);
		addComponent(errorComponent, 0.025f, 0.9f, 0.975f, 1f);
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(menu.getContainerPortal());
		}), 0.025f, 0.7f, 0.2f, 0.8f);
		
		addComponent(new DynamicTextComponent("Name:", EditProps.LABEL), 0.05f, 0.6f, 0.15f, 0.65f);
		addComponent(nameField, 0.175f, 0.6f, 0.3f, 0.65f);
		addComponent(new DynamicTextComponent("Display name:", EditProps.LABEL), 0.05f, 0.525f, 0.2f, 0.575f);
		addComponent(displayNameField, 0.225f, 0.525f, 0.35f, 0.575f);
		addComponent(new DynamicTextComponent("Fuel mode:", EditProps.LABEL), 0.05f, 0.45f, 0.175f, 0.5f);
		addComponent(EnumSelect.createSelectButton(FuelMode.class, newFuelMode -> {
			this.fuelMode = newFuelMode;
		}, fuelMode), 0.2f, 0.45f, 0.3f, 0.5f);
		addComponent(new DynamicTextComponent("Vanilla type:", EditProps.LABEL), 0.05f, 0.375f, 0.2f, 0.425f);
		addComponent(EnumSelect.createSelectButton(VanillaContainerType.class, newVanillaType -> {
			this.vanillaType = newVanillaType;
		}, vanillaType), 0.225f, 0.375f, 0.35f, 0.425f);
		addComponent(new DynamicTextComponent("Persistent storage", EditProps.LABEL), 0.05f, 0.3f, 0.25f, 0.35f);
		addComponent(persistentStorage, 0.275f, 0.3f, 0.3f, 0.325f);
		addComponent(new DynamicTextButton("Recipes...", EditProps.BUTTON, EditProps.HOVER, () -> {
			// TODO Go to the recipe overview for this container
		}), 0.05f, 0.225f, 0.2f, 0.275f);
		
		// TODO Add grid for the custom slots, plus buttons to insert/delete rows
		
		if (toModify != null) {
			addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				String error = menu.getSet().changeContainer(toModify, nameField.getText(), 
						displayNameField.getText(), recipes, fuelMode, slots, 
						vanillaType, persistentStorage.isChecked()
				);
				if (error != null) {
					errorComponent.setText(error);
				} else {
					state.getWindow().setMainComponent(menu.getContainerPortal());
				}
			}), 0.025f, 0.1f, 0.175f, 0.2f);
		} else {
			addComponent(new DynamicTextButton("Create", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				String error = menu.getSet().addContainer(new CustomContainer(
						nameField.getText(), displayNameField.getText(), recipes, 
						fuelMode, slots, vanillaType, persistentStorage.isChecked())
				);
				if (error != null) {
					errorComponent.setText(error);
				} else {
					state.getWindow().setMainComponent(menu.getContainerPortal());
				}
			}), 0.025f, 0.1f, 0.175f, 0.2f);
		}
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
