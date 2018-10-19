package nl.knokko.customitems.editor.menu.edit.select.item;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.Material;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;

public class SelectSimpleVanillaItem extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final Receiver receiver;

	public SelectSimpleVanillaItem(GuiComponent returnMenu, Receiver receiver) {
		this.returnMenu = returnMenu;
		this.receiver = receiver;
	}

	@Override
	protected void addComponents() {
		addComponent(new TextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		
		int index = 0;
		Material[] materials = Material.values();
		for (Material material : materials) {
			addComponent(new TextButton(material.name().toLowerCase(), EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				receiver.onSelect(material);
				state.getWindow().setMainComponent(returnMenu);
			}), 0.35f, 0.9f - index * 0.1f, 0.7f, 1f - index * 0.1f);
			index++;
		}
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	public static interface Receiver {
		
		void onSelect(Material material);
	}
}