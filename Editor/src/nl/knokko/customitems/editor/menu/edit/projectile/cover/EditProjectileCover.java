package nl.knokko.customitems.editor.menu.edit.projectile.cover;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.item.ItemTypeSelectButton;
import nl.knokko.customitems.editor.set.ItemDamageClaim;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.projectile.cover.ProjectileCover;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.CustomItemType.Category;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;
import nl.knokko.gui.util.Option.Short;

public abstract class EditProjectileCover extends GuiMenu {
	
	protected final EditMenu menu;
	
	protected DynamicTextComponent errorComponent;
	
	protected TextEditField nameField;
	protected ItemTypeSelect internalTypeSelect;
	protected IntEditField internalDamageField;
	
	public EditProjectileCover(EditMenu menu) {
		this.menu = menu;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(menu.getProjectileCoverOverview());
		}), 0.025f, 0.7f, 0.2f, 0.8f);
		
		ProjectileCover original = getOriginal();
		
		errorComponent = new DynamicTextComponent("", EditProps.ERROR);
		
		if (original == null) {
			nameField = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			internalTypeSelect = new ItemTypeSelect(menu.getSet(), Category.PROJECTILE_COVER, 
					CustomItemType.DIAMOND_HOE, null);
			internalDamageField = new IntEditField(menu.getSet().nextAvailableDamage(internalTypeSelect.getCurrentType(), null), 1, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		} else {
			nameField = new TextEditField(original.name, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			internalTypeSelect = new ItemTypeSelect(original.itemType, Category.PROJECTILE_COVER);
			internalDamageField = new IntEditField(original.itemDamage, 1, original.itemType.getMaxDurability(), EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		}
		
		addComponent(errorComponent, 0.025f, 0.9f, 0.975f, 1f);
		addComponent(new DynamicTextComponent("Name:", EditProps.LABEL), 0.5f, 0.8f, 0.59f, 0.9f);
		addComponent(nameField, 0.6f, 0.81f, 0.9f, 0.89f);
		addComponent(new DynamicTextComponent("Internal item type:", EditProps.LABEL), 0.25f, 0.7f, 0.59f, 0.8f);
		addComponent(internalTypeSelect, 0.6f, 0.71f, 0.8f, 0.79f);
		addComponent(new DynamicTextComponent("Internal item damage:", EditProps.LABEL), 0.25f, 0.6f, 0.59f, 0.7f);
		addComponent(internalDamageField, 0.6f, 0.61f, 0.8f, 0.69f);
		
		if (original == null) {
			addComponent(new DynamicTextButton("Create", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				Option.Short internalDamage = internalDamageField.getInt().toShort();
				if (internalDamage.hasValue()) {
					tryCreate(nameField.getText(), internalTypeSelect.getCurrentType(), internalDamage.getValue());
				} else {
					errorComponent.setText("The internal item damage must be a positive integer");
				}
			}), 0.025f, 0.2f, 0.2f, 0.3f);
		} else {
			addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				Option.Short internalDamage = internalDamageField.getInt().toShort();
				if (internalDamage.hasValue()) {
					tryApply(nameField.getText(), internalTypeSelect.getCurrentType(), internalDamage.getValue());
				} else {
					errorComponent.setText("The internal item damage must be a positive integer");
				}
			}), 0.025f, 0.2f, 0.2f, 0.3f);
		}
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	protected abstract ProjectileCover getOriginal();
	
	protected abstract void tryCreate(String name, CustomItemType internalType, short internalDamage);
	
	protected abstract void tryApply(String name, CustomItemType internalType, short internalDamage);
	
	protected void handleError(String error) {
		if (error == null) {
			state.getWindow().setMainComponent(menu.getProjectileCoverOverview());
		} else {
			errorComponent.setText(error);
		}
	}
	
	protected class ItemTypeSelect extends ItemTypeSelectButton {

		public ItemTypeSelect(ItemSet set, Category category, CustomItemType preferred, ItemDamageClaim original) {
			super(set, category, preferred, original);
		}
		
		public ItemTypeSelect(CustomItemType type, Category category) {
			super(type, category);
		}

		@Override
		protected void setErrorText(String error) {
			errorComponent.setText(error);
		}

		@Override
		protected Short getInternalDamage() {
			return internalDamageField.getInt().toShort();
		}

		@Override
		protected void setInternalDamage(short newDamage) {
			internalDamageField.setText(newDamage + "");
		}

		@Override
		protected ItemSet getSet() {
			return menu.getSet();
		}

		@Override
		protected ItemDamageClaim getOriginal() {
			return EditProjectileCover.this.getOriginal();
		}
	}
}
