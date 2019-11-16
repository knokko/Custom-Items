package nl.knokko.customitems.editor.menu.edit.projectile;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.projectile.Projectile;
import nl.knokko.customitems.projectile.ProjectileType;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.FloatEditField;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditProjectile extends GuiMenu {
	
	private static final float BUTTON_X = 0.425f;
	private static final float LABEL_X = BUTTON_X - 0.01f;
	
	private static final float BUTTON_X2 = 0.9f;
	private static final float LABEL_X2 = BUTTON_X2 - 0.01f;
	
	private final EditMenu menu;
	private final Projectile previous;
	
	private DamageSource impactDamageSource;
	private ProjectileType minecraftType;

	public EditProjectile(EditMenu menu, Projectile previous) {
		this.menu = menu;
		this.previous = previous;
		
		if (previous == null) {
			impactDamageSource = DamageSource.PROJECTILE;
			minecraftType = ProjectileType.SNOWBALL;
		} else {
			impactDamageSource = previous.damageSource;
			minecraftType = previous.minecraftType;
		}
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(menu.getProjectileMenu().getProjectileOverview());
		}), 0.025f, 0.75f, 0.15f, 0.85f);
		
		// The initial values of the edit fields
		String name;
		float damage;
		float minLaunchAngle, maxLaunchAngle;
		float minLaunchSpeed, maxLaunchSpeed;
		int maxLifeTime;
		
		// Give them different values depending on whether we are editing an existing projectile or creating a new one
		if (previous == null) {
			name = "";
			damage = 5f;
			minLaunchAngle = 0f;
			maxLaunchAngle = 5f;
			minLaunchSpeed = 0.125f;
			maxLaunchSpeed = 0.1875f;
			maxLifeTime = 200;
		} else {
			name = previous.name;
			damage = previous.damage;
			minLaunchAngle = previous.minLaunchAngle;
			maxLaunchAngle = previous.maxLaunchAngle;
			minLaunchSpeed = previous.minLaunchSpeed;
			maxLaunchSpeed = previous.maxLaunchSpeed;
			maxLifeTime = previous.maxLifeTime;
		}
		
		// The edit fields
		TextEditField nameField = new TextEditField(name, EDIT_BASE, EDIT_ACTIVE);
		FloatEditField damageField = new FloatEditField(damage, 0f, EDIT_BASE, EDIT_ACTIVE);
		FloatEditField minLaunchAngleField = new FloatEditField(minLaunchAngle, 0f, EDIT_BASE, EDIT_ACTIVE);
		FloatEditField maxLaunchAngleField = new FloatEditField(maxLaunchAngle, 0f, EDIT_BASE, EDIT_ACTIVE);
		FloatEditField minLaunchSpeedField = new FloatEditField(minLaunchSpeed, 0f, EDIT_BASE, EDIT_ACTIVE);
		FloatEditField maxLaunchSpeedField = new FloatEditField(maxLaunchSpeed, 0f, EDIT_BASE, EDIT_ACTIVE);
		IntEditField lifeTimeField = new IntEditField(maxLifeTime, 1, EDIT_BASE, EDIT_ACTIVE);
		
		// First column of the form
		addComponent(new DynamicTextComponent("Name:", LABEL), LABEL_X - 0.125f, 0.8f, LABEL_X, 0.88f);
		addComponent(nameField, BUTTON_X, 0.8f, BUTTON_X + 0.2f, 0.87f);
		addComponent(new DynamicTextComponent("Impact damage:", LABEL), LABEL_X - 0.25f, 0.72f, LABEL_X, 0.8f);
		addComponent(damageField, BUTTON_X, 0.72f, BUTTON_X + 0.1f, 0.79f);
		addComponent(new DynamicTextComponent("Minimum launch angle:", LABEL), LABEL_X - 0.3f, 0.64f, LABEL_X, 0.72f);
		addComponent(minLaunchAngleField, BUTTON_X, 0.64f, BUTTON_X + 0.15f, 0.71f);
		addComponent(new DynamicTextComponent("Maximum launch angle:", LABEL), LABEL_X - 0.3f, 0.56f, LABEL_X, 0.64f);
		addComponent(maxLaunchAngleField, BUTTON_X, 0.56f, BUTTON_X + 0.15f, 0.63f);
		addComponent(new DynamicTextComponent("Minimum launch speed:", LABEL), LABEL_X - 0.3f, 0.48f, LABEL_X, 0.56f);
		addComponent(minLaunchSpeedField, BUTTON_X, 0.48f, BUTTON_X + 0.125f, 0.55f);
		addComponent(new DynamicTextComponent("Maximum launch speed:", LABEL), LABEL_X - 0.3f, 0.40f, LABEL_X, 0.48f);
		addComponent(maxLaunchSpeedField, BUTTON_X, 0.40f, BUTTON_X + 0.125f, 0.47f);
		addComponent(new DynamicTextComponent("Maximum lifetime:", LABEL), LABEL_X - 0.22f, 0.32f, LABEL_X, 0.40f);
		addComponent(lifeTimeField, BUTTON_X, 0.32f, BUTTON_X + 0.1f, 0.39f);
		addComponent(new DynamicTextComponent("Impact damage source:", LABEL), LABEL_X - 0.3f, 0.24f, LABEL_X, 0.32f);
		addComponent(EnumSelect.createSelectButton(DamageSource.class, (DamageSource newImpactSource) -> {
			impactDamageSource = newImpactSource;
		}, impactDamageSource), BUTTON_X, 0.24f, BUTTON_X + 0.2f, 0.31f);
		addComponent(new DynamicTextComponent("Projectile type:", LABEL), LABEL_X - 0.18f, 0.16f, LABEL_X, 0.24f);
		addComponent(EnumSelect.createSelectButton(ProjectileType.class, (ProjectileType newType) -> {
			minecraftType = newType;
		}, minecraftType), BUTTON_X, 0.16f, BUTTON_X + 0.2f, 0.23f);
		
		// Second column of the form
		addComponent(new DynamicTextComponent("In flight effects:", LABEL), LABEL_X2 - 0.25f, 0.8f, LABEL_X2, 0.88f);
		addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
			// TODO CollectionEdit for ProjectileEffectS
		}), BUTTON_X2, 0.8f, BUTTON_X2 + 0.09f, 0.87f);
		addComponent(new DynamicTextComponent("Impact effects:", LABEL), LABEL_X2 - 0.2f, 0.72f, LABEL_X2, 0.8f);
		addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
			// TODO CollectionEdit for ProjectileEffect
		}), BUTTON_X2, 0.72f, BUTTON_X2 + 0.09f, 0.79f);
		addComponent(new DynamicTextComponent("Projectile cover:", LABEL), LABEL_X2 - 0.2f, 0.64f, LABEL_X2, 0.72f);
		addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
			// TODO Projectile cover selection menu
		}), BUTTON_X2, 0.64f, BUTTON_X2 + 0.09f, 0.71f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return BACKGROUND;
	}
}
