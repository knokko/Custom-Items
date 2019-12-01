package nl.knokko.customitems.editor.menu.edit.item;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.item.CustomWand;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.AttributeModifier.Attribute;
import nl.knokko.customitems.item.AttributeModifier.Operation;
import nl.knokko.customitems.item.AttributeModifier.Slot;
import nl.knokko.customitems.item.CustomItemType.Category;
import nl.knokko.customitems.item.WandCharges;
import nl.knokko.customitems.projectile.CIProjectile;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public class EditItemWand extends EditItemBase {
	
	private static final AttributeModifier EXAMPLE = new AttributeModifier(Attribute.MOVEMENT_SPEED, Slot.OFFHAND, Operation.MULTIPLY, 1.2);
	
	private static final float BUTTON_X2 = 0.75f;
	private static final float LABEL_X2 = BUTTON_X2 - 0.01f;
	
	private final CustomWand previous;
	
	private final IntEditField cooldownField;
	private final IntEditField maxChargesField;
	private final IntEditField rechargeTimeField;
	private final IntEditField amountField;
	
	private CIProjectile projectile;

	public EditItemWand(EditMenu menu, CustomWand previous) {
		super(menu, previous, Category.WAND);
		this.previous = previous;
		
		if (previous != null)
			projectile = previous.projectile;
		
		cooldownField = new IntEditField(previous == null ? 40 : previous.cooldown, 0, EDIT_BASE, EDIT_ACTIVE);
		maxChargesField = new IntEditField(previous == null || previous.charges == null ? 1 : previous.charges.maxCharges, 1, EDIT_BASE, EDIT_ACTIVE);
		rechargeTimeField = new IntEditField(previous == null || previous.charges == null ? 20 : previous.charges.rechargeTime, 0, EDIT_BASE, EDIT_ACTIVE);
		amountField = new IntEditField(previous == null ? 1 : previous.amountPerShot, 1, EDIT_BASE, EDIT_ACTIVE);
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		
		addComponent(new DynamicTextComponent("Projectile:", LABEL), LABEL_X2 - 0.15f, 0.8f, LABEL_X2, 0.85f);
		addComponent(CollectionSelect.createButton(menu.getSet().getBackingProjectiles(), (CIProjectile newProjectile) -> {
			projectile = newProjectile;
		}, (CIProjectile toName) -> {
			return toName.name;
		}, projectile), BUTTON_X2, 0.8f, BUTTON_X2 + 0.15f, 0.85f);
		
		addComponent(new DynamicTextComponent("Max charges:", LABEL), LABEL_X2 - 0.15f, 0.74f, LABEL_X2, 0.79f);
		addComponent(maxChargesField, BUTTON_X2, 0.74f, BUTTON_X2 + 0.05f, 0.79f);
		
		class RechargeWrapper<T extends GuiComponent> extends WrapperComponent<T> {

			public RechargeWrapper(T component) {
				super(component);
			}
			
			@Override
			public boolean isActive() {
				Option.Int maxCharges = maxChargesField.getInt();
				return maxCharges.hasValue() && maxCharges.getValue() > 1;
			}
		}
		
		addComponent(new RechargeWrapper<>(
				new DynamicTextComponent("Recharge time:", LABEL)), LABEL_X2 - 0.15f, 0.68f, LABEL_X2, 0.73f);
		addComponent(new RechargeWrapper<>(rechargeTimeField), BUTTON_X2, 0.68f, BUTTON_X2 + 0.05f, 0.73f);
		
		addComponent(new DynamicTextComponent("Amount per shot:", LABEL), LABEL_X2 - 0.17f, 0.62f, LABEL_X2, 0.67f);
		addComponent(amountField, BUTTON_X2, 0.62f, BUTTON_X2 + 0.05f, 0.67f);
		
		addComponent(new DynamicTextComponent("Cooldown:", LABEL), LABEL_X2 - 0.13f, 0.56f, LABEL_X2, 0.61f);
		addComponent(cooldownField, BUTTON_X2, 0.56f, BUTTON_X2 + 0.05f, 0.61f);
	}

	@Override
	protected String create(short internalItemDamage) {
		return withProperties((int cooldown, WandCharges charges, int amount) -> {
			return menu.getSet().addWand(new CustomWand(internalType, internalItemDamage, name.getText(), 
					getDisplayName(), lore, attributes, enchantments, textureSelect.getSelected(), 
					itemFlags, customModel, playerEffects, targetEffects, commands, projectile, cooldown, 
					charges, amount));
		});
	}

	@Override
	protected String apply(short internalItemDamage) {
		return withProperties((int cooldown, WandCharges charges, int amount) -> {
			return menu.getSet().changeWand(previous, internalType, internalItemDamage, name.getText(), 
					getDisplayName(), lore, attributes, enchantments, textureSelect.getSelected(), 
					itemFlags, customModel, playerEffects, targetEffects, commands, projectile, cooldown, 
					charges, amount);
		});
	}
	
	private String withProperties(PropsListener listener) {
		Option.Int cooldown = cooldownField.getInt();
		Option.Int maxCharges = maxChargesField.getInt();
		Option.Int rechargeTime = rechargeTimeField.getInt();
		Option.Int amount = amountField.getInt();
		
		String error = null;
		
		if (!cooldown.hasValue()) error = "The cooldown must be a positive integer";
		if (!maxCharges.hasValue()) error = "The maximum charges must be a positive integer";
		if (!amount.hasValue()) error = "The amount must be a positive integer";
		
		if (maxCharges.getValue() > 1 && !rechargeTime.hasValue())
			error = "If the max charges is larger than 1, the recharge time must be a positive integer";
		
		if (error == null) {
			WandCharges charges;
			if (maxCharges.getValue() > 1)
				charges = new WandCharges(maxCharges.getValue(), rechargeTime.getValue());
			else
				charges = null;
			return listener.process(cooldown.getValue(), charges, amount.getValue());
		} else {
			return error;
		}
	}
	
	private static interface PropsListener {
		
		String process(int cooldown, WandCharges charges, int amount);
	}

	@Override
	protected CustomItem previous() {
		return previous;
	}

	@Override
	protected AttributeModifier getExampleAttributeModifier() {
		return EXAMPLE;
	}

	@Override
	protected Category getCategory() {
		return Category.WAND;
	}
}
