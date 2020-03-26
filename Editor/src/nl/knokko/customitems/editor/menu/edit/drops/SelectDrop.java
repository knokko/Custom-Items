package nl.knokko.customitems.editor.menu.edit.drops;

import nl.knokko.customitems.drops.Drop;
import nl.knokko.customitems.editor.HelpButtons;
import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option.Int;

public class SelectDrop extends GuiMenu {
	
	private final ItemSet set;
	private final GuiComponent returnMenu;
	private final Receiver receiver;
	
	private final Drop previous;
	
	private CustomItem selectedItem;
	
	public SelectDrop(ItemSet set, GuiComponent returnMenu, Drop previous, Receiver receiver) {
		this.set = set;
		this.returnMenu = returnMenu;
		this.previous = previous;
		this.receiver = receiver;
		
		if (previous != null) {
			selectedItem = (CustomItem) previous.getItemToDrop();
		}
	}

	@Override
	protected void addComponents() {
		
		DynamicTextComponent errorComponent = new DynamicTextComponent("", EditProps.ERROR);
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
		
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.8f, 0.2f, 0.9f);
		
		addComponent(new DynamicTextComponent("Item to drop:", EditProps.LABEL), 0.3f, 0.7f, 0.5f, 0.8f);
		addComponent(CollectionSelect.createButton(set.getBackingItems(), (CustomItem newItem) -> {
			selectedItem = newItem;
		}, (CustomItem item) -> { return item.getName(); }, selectedItem), 0.55f, 0.7f, 0.8f, 0.8f);
		
		addComponent(new DynamicTextComponent("Drop chance:", EditProps.LABEL), 0.3f, 0.575f, 0.5f, 0.675f);
		IntEditField dropChance = new IntEditField(previous == null ? 100 : previous.getDropChance(), 0, 100, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		addComponent(dropChance, 0.55f, 0.575f, 0.65f, 0.675f);
		addComponent(new DynamicTextComponent("%", EditProps.LABEL), 0.66f, 0.575f, 0.68f, 0.675f);
		
		addComponent(new DynamicTextComponent("Minimum amount to drop:", EditProps.LABEL), 0.2f, 0.45f, 0.5f, 0.55f);
		IntEditField minAmount = new IntEditField(previous == null ? 1 : previous.getMinDropAmount(), 1, 64, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		addComponent(minAmount, 0.55f, 0.45f, 0.65f, 0.55f);
		
		addComponent(new DynamicTextComponent("Maximum amount to drop:", EditProps.LABEL), 0.2f, 0.35f, 0.5f, 0.45f);
		IntEditField maxAmount = new IntEditField(previous == null ? 1 : previous.getMaxDropAmount(), 1, 64, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		addComponent(maxAmount, 0.55f, 0.35f, 0.65f, 0.45f);
		
		addComponent(new DynamicTextComponent("Prevent other drops", EditProps.LABEL), 0.3f, 0.225f, 0.55f, 0.325f);
		CheckboxComponent preventOtherDrops = new CheckboxComponent(previous != null && previous.cancelNormalDrop());
		addComponent(preventOtherDrops, 0.25f, 0.225f, 0.275f, 0.25f);
		
		addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			String error = null;
			if (selectedItem == null) error = "You need to select the custom item to drop";
			Int maybeDropChance = dropChance.getInt();
			if (!maybeDropChance.hasValue()) error = "The drop chance should be an integer between 1 and 100";
			Int maybeMinAmount = minAmount.getInt();
			if (!maybeMinAmount.hasValue()) error = "The minimum amount to drop should be an integer between 1 and 64";
			Int maybeMaxAmount = maxAmount.getInt();
			if (!maybeMaxAmount.hasValue()) error = "The maximum amount to drop should be an integer between 1 and 64";
			if (error == null) {
				receiver.onSelect(new Drop(selectedItem, maybeMinAmount.getValue(), maybeMaxAmount.getValue(), maybeDropChance.getValue(), preventOtherDrops.isChecked()));
				state.getWindow().setMainComponent(returnMenu);
			} else {
				errorComponent.setText(error);
			}
		}), 0.025f, 0.1f, 0.2f, 0.2f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/drops/drop.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	public static interface Receiver {
		
		void onSelect(Drop newDrop);
	}
}
