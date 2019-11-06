package nl.knokko.customitems.editor.menu.edit.projectilecover;

import java.util.Collection;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.projectile.*;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class ProjectileCoverOverview extends GuiMenu {
	
	protected final EditMenu menu;
	
	protected final CoverList list;
	protected final DynamicTextComponent errorComponent;
	
	public ProjectileCoverOverview(EditMenu menu) {
		this.menu = menu;
		this.list = new CoverList();
		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(menu);
		}), 0.025f, 0.7f, 0.175f, 0.8f);
		addComponent(new DynamicTextButton("Create", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new CreateProjectileCover(menu));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		addComponent(errorComponent, 0.025f, 0.9f, 0.975f, 1f);
		addComponent(list, 0.5f, 0f, 1f, 0.9f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	@Override
	public void init() {
		if(didInit) list.refresh();
		super.init();
	}
	
	private class CoverList extends GuiMenu {

		@Override
		protected void addComponents() {
			Collection<ProjectileCover> covers = menu.getSet().getBackingProjectileCovers();
			int index = 0;
			for (ProjectileCover cover : covers) {
				float minY = 0.9f - 0.15f * index;
				float maxY = 1f - 0.15f * index;
				addComponent(new DynamicTextComponent(cover.name, EditProps.LABEL), 
						0f, minY, Math.min(0.05f * cover.name.length(), 0.6f), maxY);
				addComponent(new DynamicTextButton("Edit", EditProps.BUTTON, EditProps.HOVER, () -> {
					if (cover instanceof SphereProjectileCover) {
						state.getWindow().setMainComponent(new EditSphereProjectileCover(menu, (SphereProjectileCover) cover));
					} else if (cover instanceof CustomProjectileCover) {
						state.getWindow().setMainComponent(new EditCustomProjectileCover(menu, (CustomProjectileCover) cover));
					} else {
						errorComponent.setText("It looks like we forgot the edit menu for this projectile cover type. Please report on discord or BukkitDev");
					}
				}), 0.65f, minY, 0.75f, maxY);
				addComponent(new DynamicTextButton("Delete", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
					String error = menu.getSet().removeProjectileCover(cover);
					if (error == null) {
						list.refresh();
					} else {
						errorComponent.setText(error);
					}
				}), 0.8f, minY, 0.925f, maxY);
				index++;
			}
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND2;
		}
		
		protected void refresh() {
			clearComponents();
			addComponents();
		}
	}
}
