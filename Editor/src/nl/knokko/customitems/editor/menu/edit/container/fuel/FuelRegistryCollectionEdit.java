package nl.knokko.customitems.editor.menu.edit.container.fuel;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.container.fuel.CustomFuelRegistry;
import nl.knokko.customitems.editor.menu.edit.CollectionEdit;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class FuelRegistryCollectionEdit extends CollectionEdit<CustomFuelRegistry> {
	
	private final EditMenu menu;

	public FuelRegistryCollectionEdit(EditMenu menu) {
		super(new FuelRegistryActionHandler(menu), menu.getSet().getBackingFuelRegistries());
		this.menu = menu;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Add new", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditFuelRegistry(menu, null, null));
		}), 0.05f, 0.2f, 0.2f, 0.3f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	private static class FuelRegistryActionHandler implements ActionHandler<CustomFuelRegistry> {
		
		private final EditMenu menu;
		
		FuelRegistryActionHandler(EditMenu menu) {
			this.menu = menu;
		}

		@Override
		public void goBack() {
			menu.getState().getWindow().setMainComponent(menu.getContainerPortal());
		}

		@Override
		public BufferedImage getImage(CustomFuelRegistry item) {
			return null;
		}

		@Override
		public String getLabel(CustomFuelRegistry item) {
			return item.getName();
		}

		@Override
		public GuiComponent createEditMenu(CustomFuelRegistry itemToEdit, GuiComponent returnMenu) {
			return new EditFuelRegistry(menu, itemToEdit, itemToEdit);
		}

		@Override
		public GuiComponent createCopyMenu(CustomFuelRegistry itemToCopy, GuiComponent returnMenu) {
			return new EditFuelRegistry(menu, itemToCopy, null);
		}

		@Override
		public String deleteItem(CustomFuelRegistry itemToDelete) {
			return menu.getSet().removeFuelRegistry(itemToDelete);
		}
	}
}
