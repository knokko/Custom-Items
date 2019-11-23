package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import java.awt.image.BufferedImage;
import java.util.Collection;

import nl.knokko.customitems.editor.menu.edit.CollectionEdit;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.projectile.effects.*;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ProjectileEffectCollectionEdit extends CollectionEdit<ProjectileEffect> {
	
	private final Collection<ProjectileEffect> collection;

	public ProjectileEffectCollectionEdit(Collection<ProjectileEffect> collectionToModify, GuiComponent returnMenu) {
		super(new ProjectileEffectActionHandler(collectionToModify, returnMenu), collectionToModify);
		this.collection = collectionToModify;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Add", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			state.getWindow().setMainComponent(new CreateProjectileEffect(collection, this));
		}), 0.025f, 0.2f, 0.15f, 0.3f);
	}
	
	private static class ProjectileEffectActionHandler implements ActionHandler<ProjectileEffect> {
		
		private final Collection<ProjectileEffect> backingCollection;
		private final GuiComponent returnMenu;
		
		private ProjectileEffectActionHandler(Collection<ProjectileEffect> backingCollection, GuiComponent returnMenu) {
			this.backingCollection = backingCollection;
			this.returnMenu = returnMenu;
		}

		@Override
		public void goBack() {
			returnMenu.getState().getWindow().setMainComponent(returnMenu);
		}

		@Override
		public BufferedImage getImage(ProjectileEffect item) {
			return null;
		}

		@Override
		public String getLabel(ProjectileEffect item) {
			return item.toString();
		}

		@Override
		public GuiComponent createEditMenu(ProjectileEffect itemToEdit, GuiComponent returnMenu) {
			GuiComponent currentMenu = returnMenu.getState().getWindow().getMainComponent();
			if (itemToEdit instanceof ColoredRedstone) {
				return new EditColoredRedstone((ColoredRedstone) itemToEdit, backingCollection, currentMenu);
			} else if (itemToEdit instanceof ExecuteCommand) {
				return new EditExecuteCommand((ExecuteCommand) itemToEdit, backingCollection, currentMenu);
			} else if (itemToEdit instanceof Explosion) {
				return new EditExplosion((Explosion) itemToEdit, backingCollection, currentMenu);
			} else if (itemToEdit instanceof RandomAccelleration) {
				
			} else if (itemToEdit instanceof SimpleParticles) {
				
			} else if (itemToEdit instanceof StraightAccelleration) {
				
			} else if (itemToEdit instanceof SubProjectiles) {
				
			}
			throw new Error("Unknown projectile effect class: " + itemToEdit.getClass());
		}

		@Override
		public String deleteItem(ProjectileEffect itemToDelete) {
			return backingCollection.remove(itemToDelete) ? null : "That effect wasn't in the list of effects";
		}
	}
}
