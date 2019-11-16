package nl.knokko.customitems.editor.menu.edit.projectile;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.editor.menu.edit.CollectionEdit;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.projectile.Projectile;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ProjectileCollectionEdit extends CollectionEdit<Projectile> {
	
	private final EditMenu menu;

	public ProjectileCollectionEdit(EditMenu menu) {
		super(new ProjectileActionHandler(menu), menu.getSet().getBackingProjectiles());
		this.menu = menu;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Create", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditProjectile(menu, null));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
	}
	
	private static class ProjectileActionHandler implements ActionHandler<Projectile> {
		
		private final EditMenu menu;
		
		private ProjectileActionHandler(EditMenu menu) {
			this.menu = menu;
		}

		@Override
		public void goBack() {
			menu.getState().getWindow().setMainComponent(menu.getProjectileMenu());
		}

		@Override
		public BufferedImage getImage(Projectile item) {
			return null;
		}

		@Override
		public String getLabel(Projectile item) {
			return item.name;
		}

		@Override
		public GuiComponent createEditMenu(Projectile itemToEdit, GuiComponent returnMenu) {
			return new EditProjectile(menu, itemToEdit);
		}

		@Override
		public String deleteItem(Projectile itemToDelete) {
			return menu.getSet().removeProjectile(itemToDelete);
		}
	}
}
