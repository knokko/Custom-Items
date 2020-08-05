package nl.knokko.customitems.editor.menu.edit.container;

import java.awt.image.BufferedImage;
import java.util.Collection;

import nl.knokko.customitems.container.fuel.CustomFuelRegistry;
import nl.knokko.customitems.editor.menu.edit.CollectionEdit;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.gui.component.GuiComponent;

public class FuelRegistryCollectionEdit extends CollectionEdit<CustomFuelRegistry> {

	public FuelRegistryCollectionEdit(EditMenu menu) {
		super(new FuelRegistryActionHandler(), collectionToModify);
	}

	private static class FuelRegistryActionHandler implements ActionHandler<CustomFuelRegistry> {

		@Override
		public void goBack() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public BufferedImage getImage(CustomFuelRegistry item) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getLabel(CustomFuelRegistry item) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public GuiComponent createEditMenu(CustomFuelRegistry itemToEdit, GuiComponent returnMenu) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public GuiComponent createCopyMenu(CustomFuelRegistry itemToCopy, GuiComponent returnMenu) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String deleteItem(CustomFuelRegistry itemToDelete) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
