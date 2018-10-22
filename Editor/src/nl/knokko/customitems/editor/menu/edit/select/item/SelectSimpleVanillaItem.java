package nl.knokko.customitems.editor.menu.edit.select.item;

import java.util.Arrays;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.Material;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;

public class SelectSimpleVanillaItem extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final Receiver receiver;
	private final List list;

	public SelectSimpleVanillaItem(GuiComponent returnMenu, Receiver receiver) {
		this.returnMenu = returnMenu;
		this.receiver = receiver;
		this.list = new List();
	}

	@Override
	protected void addComponents() {
		addComponent(new TextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.1f, 0.7f, 0.25f, 0.8f);
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
				addComponent(new SelectItemButton(state.getWindow().getTextureLoader(), material, () -> {
					receiver.onSelect(material);
					state.getWindow().setMainComponent(returnMenu);
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
		
		void onSelect(Material material);
	}
}