package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import java.util.Collection;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.projectile.effects.ProjectileEffect;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class CreateProjectileEffect extends GuiMenu {
	
	private final Collection<ProjectileEffect> backingCollection;
	private final ItemSet set;
	private final GuiComponent returnMenu;

	public CreateProjectileEffect(Collection<ProjectileEffect> backingCollection, ItemSet set, 
			GuiComponent returnMenu) {
		this.backingCollection = backingCollection;
		this.set = set;
		this.returnMenu = returnMenu;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.175f, 0.8f);
		
		addComponent(new DynamicTextButton("Spawn colored redstone dust", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditColoredRedstone(null, backingCollection, returnMenu));
		}), 0.5f, 0.8f, 0.85f, 0.9f);
		addComponent(new DynamicTextButton("Execute command", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditExecuteCommand(null, backingCollection, returnMenu));
		}), 0.5f, 0.68f, 0.7f, 0.78f);
		addComponent(new DynamicTextButton("Create explosion", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditExplosion(null, backingCollection, returnMenu));
		}), 0.5f, 0.56f, 0.7f, 0.66f);
		addComponent(new DynamicTextButton("Accellerate in random direction", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditRandomAccelleration(null, backingCollection, returnMenu));
		}), 0.5f, 0.44f, 0.9f, 0.54f);
		addComponent(new DynamicTextButton("Accellerate in move direction", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditStraightAccelleration(null, backingCollection, returnMenu));
		}), 0.5f, 0.32f, 0.9f, 0.42f);
		addComponent(new DynamicTextButton("Spawn simple particle", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditSimpleParticles(null, backingCollection, returnMenu));
		}), 0.5f, 0.2f, 0.8f, 0.3f);
		addComponent(new DynamicTextButton("Launch (another) projectile", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditSubProjectiles(null, set, backingCollection, returnMenu));
		}), 0.5f, 0.08f, 0.9f, 0.18f);
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
