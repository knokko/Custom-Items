package nl.knokko.customitems.editor.menu.edit.recipe.result;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.select.item.SelectCustomItem;
import nl.knokko.customitems.editor.menu.edit.select.item.SelectDataVanillaItem;
import nl.knokko.customitems.editor.menu.edit.select.item.SelectSimpleVanillaItem;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.item.Material;
import nl.knokko.customitems.editor.set.recipe.result.CustomItemResult;
import nl.knokko.customitems.editor.set.recipe.result.DataVanillaResult;
import nl.knokko.customitems.editor.set.recipe.result.Result;
import nl.knokko.customitems.editor.set.recipe.result.SimpleVanillaResult;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.ConditionalTextButton;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;
import nl.knokko.gui.component.text.TextEditField;

public class ChooseResult extends GuiMenu {
	
	private final ResultListener listener;
	private final GuiComponent returnMenu;
	private final ItemSet set;
	
	private final TextComponent errorComponent;
	private final TextEditField amountField;
	
	private Result current;

	public ChooseResult(GuiComponent returnMenu, ResultListener listener, ItemSet set) {
		this.listener = listener;
		this.returnMenu = returnMenu;
		this.set = set;
		errorComponent = new TextComponent("", EditProps.ERROR);
		amountField = new TextEditField("1", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
	}

	@Override
	protected void addComponents() {
		addComponent(new TextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.2f, 0.3f, 0.35f, 0.4f);
		addComponent(new TextButton("Custom Item", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectCustomItem(this, (CustomItem item) -> {
				// Fix the amount with the Choose button
				current = new CustomItemResult(item, (byte) 1);
			}, set));
		}), 0.6f, 0.7f, 0.8f, 0.8f);
		addComponent(new TextButton("Simple vanilla item", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectSimpleVanillaItem(this, (Material material) -> {
				// Fix the amount with the Choose button
				current = new SimpleVanillaResult(material, (byte) 1);
			}));
		}), 0.6f, 0.55f, 0.8f, 0.65f);
		addComponent(new TextButton("Vanilla item with datavalue", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectDataVanillaItem(this, (Material material, byte data) -> {
				// Fix the amount with the Choose button
				current = new DataVanillaResult(material, data, (byte) 1);
			}));
		}), 0.6f, 0.4f, 0.8f, 0.5f);
		addComponent(new TextComponent("Amount: ", EditProps.LABEL), 0.4f, 0.25f, 0.55f, 0.35f);
		addComponent(amountField, 0.6f, 0.25f, 0.7f, 0.35f);
		addComponent(new ConditionalTextButton("Select", EditProps.BUTTON, EditProps.HOVER, () -> {
			try {
				int amount = Integer.parseInt(amountField.getText());
				if (amount > 0 && amount < 64) {
					listener.onSelect(current.amountClone((byte) amount));
					state.getWindow().setMainComponent(returnMenu);
				} else {
					errorComponent.setText("The amount must be between 1 and 64");
				}
			} catch (NumberFormatException nfe) {
				errorComponent.setText("The amount must be an integer.");
			}
		}, () -> {
			return current != null;
		}), 0.2f, 0.1f, 0.35f, 0.2f);
		addComponent(errorComponent, 0.05f, 0.85f, 0.95f, 0.95f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}