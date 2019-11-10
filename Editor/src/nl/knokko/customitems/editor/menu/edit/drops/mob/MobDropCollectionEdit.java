package nl.knokko.customitems.editor.menu.edit.drops.mob;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.drops.EntityDrop;
import nl.knokko.customitems.editor.menu.edit.*;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class MobDropCollectionEdit extends CollectionEdit<EntityDrop> {
	
	private final EditMenu menu;

	public MobDropCollectionEdit(EditMenu menu) {
		super(new MobDropActionHandler(menu), menu.getSet().getBackingMobDrops());
		this.menu = menu;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("New mob drop", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditMobDrop(menu.getSet(), this, null));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
	}
	
	private static class MobDropActionHandler implements ActionHandler<EntityDrop> {
		
		private final EditMenu menu;
		
		private MobDropActionHandler(EditMenu menu) {
			this.menu = menu;
		}

		@Override
		public void goBack() {
			menu.getState().getWindow().setMainComponent(menu.getDropsMenu());
		}

		@Override
		public BufferedImage getImage(EntityDrop item) {
			return ((CustomItem) item.getDrop().getItemToDrop()).getTexture().getImage();
		}

		@Override
		public String getLabel(EntityDrop item) {
			return item.toString();
		}

		@Override
		public GuiComponent createEditMenu(EntityDrop itemToEdit, GuiComponent returnMenu) {
			return new EditMobDrop(menu.getSet(), returnMenu, itemToEdit);
		}

		@Override
		public String deleteItem(EntityDrop itemToDelete) {
			menu.getSet().removeMobDrop(itemToDelete);
			
			// Not much to go wrong here
			return null;
		}
	}
}
