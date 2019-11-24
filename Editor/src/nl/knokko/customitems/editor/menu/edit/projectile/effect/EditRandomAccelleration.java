package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import java.util.Collection;

import nl.knokko.customitems.projectile.effects.ProjectileAccelleration;
import nl.knokko.customitems.projectile.effects.ProjectileEffect;
import nl.knokko.customitems.projectile.effects.RandomAccelleration;
import nl.knokko.gui.component.GuiComponent;

public class EditRandomAccelleration extends EditAccelleration {

	public EditRandomAccelleration(ProjectileAccelleration original, Collection<ProjectileEffect> backingCollection,
			GuiComponent returnMenu) {
		super(original, backingCollection, returnMenu);
	}

	@Override
	protected ProjectileAccelleration create(float minAccelleration, float maxAccelleration) {
		return new RandomAccelleration(minAccelleration, maxAccelleration);
	}
}
