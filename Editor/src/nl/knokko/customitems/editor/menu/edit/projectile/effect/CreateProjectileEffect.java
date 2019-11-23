package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import java.util.Collection;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.projectile.effects.ProjectileEffect;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class CreateProjectileEffect extends GuiMenu {
	
	private final Collection<ProjectileEffect> backingCollection;
	private final GuiComponent returnMenu;

	public CreateProjectileEffect(Collection<ProjectileEffect> backingCollection, GuiComponent returnMenu) {
		this.backingCollection = backingCollection;
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
		}), 0.5f, 0.65f, 0.7f, 0.75f);
		addComponent(new DynamicTextButton("Create explosion", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditExplosion(null, backingCollection, returnMenu));
		}), 0.5f, 0.5f, 0.7f, 0.6f);
		addComponent(new DynamicTextButton("Accellerate in random direction", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			
		}), 0.5f, 0.35f, 0.9f, 0.45f);
		addComponent(new DynamicTextButton("Accellerate in move direction", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			
		}), 0.5f, 0.2f, 0.9f, 0.3f);
		addComponent(new DynamicTextButton("Launch (another) projectile", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			
		}), 0.5f, 0.05f, 0.9f, 0.15f);
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
