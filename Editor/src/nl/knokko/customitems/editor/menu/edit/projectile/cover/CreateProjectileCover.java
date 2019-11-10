package nl.knokko.customitems.editor.menu.edit.projectile.cover;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class CreateProjectileCover extends GuiMenu {
	
	private final EditMenu menu;
	
	public CreateProjectileCover(EditMenu menu) {
		this.menu = menu;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(menu.getProjectileCoverOverview());
		}), 0.025f, 0.7f, 0.2f, 0.8f);
		
		addComponent(new DynamicTextButton("Sphere", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditSphereProjectileCover(menu, null));
		}), 0.5f, 0.7f, 0.65f, 0.8f);
		addComponent(new DynamicTextButton("Custom", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditCustomProjectileCover(menu, null));
		}), 0.5f, 0.55f, 0.65f, 0.65f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
