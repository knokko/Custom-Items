package nl.knokko.customitems.editor.menu.edit.container.slot;

import java.util.function.Consumer;

import nl.knokko.customitems.container.slot.display.CustomItemDisplayItem;
import nl.knokko.customitems.container.slot.display.SimpleVanillaDisplayItem;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.container.slot.display.SlotDisplayItem;
import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class CreateDisplay extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final Consumer<SlotDisplay> setDisplay;
	private final boolean selectAmount;
	private final Iterable<CustomItem> customItems;
	
	public CreateDisplay(GuiComponent returnMenu, Consumer<SlotDisplay> setDisplay,
			boolean selectAmount, Iterable<CustomItem> customItems) {
		this.returnMenu = returnMenu;
		this.setDisplay = setDisplay;
		this.selectAmount = selectAmount;
		this.customItems = customItems;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.2f, 0.8f);
		
		SlotDisplayItem[] pDisplayItem = { null };
		DynamicTextComponent selectedItemDisplay = new DynamicTextComponent("", EditProps.LABEL);
		addComponent(new DynamicTextComponent("Choose item:", EditProps.LABEL), 0.6f, 0.7f, 0.75f, 0.8f);
		addComponent(selectedItemDisplay, 0.775f, 0.7f, 0.975f, 0.75f);
		addComponent(new DynamicTextButton("Custom item", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new CollectionSelect<CustomItem>(
					customItems, selectedItem -> {
						selectedItemDisplay.setText(selectedItem.getName());
						pDisplayItem[0] = new CustomItemDisplayItem(selectedItem);
					}, candidate -> true, selectedItem -> selectedItem.getName(), this));
		}), 0.6f, 0.6f, 0.75f, 0.65f);
		addComponent(new DynamicTextButton("Simple vanilla item", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EnumSelect<CIMaterial>(
					CIMaterial.class, selectedMaterial -> {
						selectedItemDisplay.setText(selectedMaterial.toString());
						pDisplayItem[0] = new SimpleVanillaDisplayItem(selectedMaterial);
					}, candidate -> true, this
			));
		}), 0.6f, 0.525f, 0.85f, 0.575f);
		addComponent(new DynamicTextButton("Data vanilla item", EditProps.BUTTON, EditProps.HOVER, () -> {
			
		}), 0.6f, 0.45f, 0.8f, 0.5f);
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
