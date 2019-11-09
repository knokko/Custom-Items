package nl.knokko.customitems.editor.menu.edit.projectilecover;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.item.EditCustomModel;
import nl.knokko.customitems.editor.set.projectile.cover.CustomProjectileCover;
import nl.knokko.customitems.editor.set.projectile.cover.ProjectileCover;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditCustomProjectileCover extends EditProjectileCover {
	
	private final CustomProjectileCover original;
	
	protected byte[] customModel;

	public EditCustomProjectileCover(EditMenu menu, CustomProjectileCover original) {
		super(menu);
		this.original = original;
		if (original != null) {
			customModel = original.model;
		} // else customModel remains null
	}

	@Override
	protected ProjectileCover getOriginal() {
		return original;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		
		addComponent(new DynamicTextComponent("Item model:", EditProps.LABEL), 0.45f, 0.1f, 0.59f, 0.2f);
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditCustomModel(null, this, (byte[] array) -> {
				customModel = array;
			}));
		}), 0.6f, 0.11f, 0.7f, 0.19f);
	}

	@Override
	protected void tryCreate(String name, CustomItemType internalType, short internalDamage) {
		handleError(menu.getSet().addCustomProjectileCover(new CustomProjectileCover(internalType, internalDamage, name, customModel)));
	}

	@Override
	protected void tryApply(String name, CustomItemType internalType, short internalDamage) {
		handleError(menu.getSet().changeCustomProjectileCover(original, internalType, internalDamage, name, customModel));
	}
}
