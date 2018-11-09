package nl.knokko.customitems.editor.menu.edit.select.item;

import java.util.Arrays;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.item.Material;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.ConditionalTextButton;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicActivatableTextButton;

public class SelectDataVanillaItem extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final Receiver receiver;
	private final TextEditField dataField;
	private final TextComponent errorComponent;
	private final List list;
	
	private Material selected;

	public SelectDataVanillaItem(GuiComponent returnMenu, Receiver receiver) {
		this.returnMenu = returnMenu;
		this.receiver = receiver;
		this.dataField = new TextEditField("0", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		this.errorComponent = new TextComponent("", EditProps.ERROR);
		this.list = new List();
	}

	@Override
	protected void addComponents() {
		addComponent(new TextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		addComponent(new TextComponent("Data value: ", EditProps.LABEL), 0.1f, 0.45f, 0.25f, 0.55f);
		addComponent(dataField, 0.1f, 0.3f, 0.25f, 0.4f);
		addComponent(errorComponent, 0.05f, 0.85f, 0.95f, 0.95f);
		addComponent(new ConditionalTextButton("OK", EditProps.BUTTON, EditProps.HOVER, () -> {
			try {
				int data = Integer.parseInt(dataField.getText());
				if (data >= 0 && data < 16) {
					receiver.onSelect(selected, (byte) data);
					state.getWindow().setMainComponent(returnMenu);
				} else {
					errorComponent.setText("The data value should be between 0 and 15");
				}
			} catch (NumberFormatException nfe) {
				errorComponent.setText("The data value should be an integer");
			}
		}, () -> {
			return selected != null;
		}), 0.1f, 0.1f, 0.2f, 0.2f);
		addComponent(list, 0.35f, 0f, 1f, 1f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	private class List extends GuiMenu {

		@Override
		protected void addComponents() {
			setScrollSpeed(13f);
			int index = 0;
			Material[] materials = Material.values();
			Arrays.sort(materials, (Material a, Material b) -> {
				return a.name().compareTo(b.name());
			});
			for (Material material : materials) {
				addComponent(new DynamicActivatableTextButton(material.name(), EditProps.SELECT_BASE, EditProps.SELECT_HOVER, EditProps.SELECT_ACTIVE, () -> {
					selected = material;
				}, () -> {
					return selected == material;
				}), 0f, 0.9f - index * 0.1f, 1f, 1f - index * 0.1f);
				index++;
			}
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND;
		}
	}
	
	public static interface Receiver {
		
		void onSelect(Material material, byte data);
	}
}