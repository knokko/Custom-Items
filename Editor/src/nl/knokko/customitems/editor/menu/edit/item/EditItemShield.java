package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.CustomShield;
import nl.knokko.customitems.item.CustomItemType.Category;
import nl.knokko.gui.component.text.FloatEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public class EditItemShield extends EditItemTool {
	
	private final CustomShield previous;
	
	private final FloatEditField thresholdField;
	
	private byte[] customBlockingModel;

	public EditItemShield(EditMenu menu, CustomShield previous) {
		super(menu, previous, Category.SHIELD);
		this.previous = previous;
		if (previous == null) {
			thresholdField = new FloatEditField(4.0, 0.0, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		} else {
			thresholdField = new FloatEditField(previous.getThresholdDamage(), 0.0, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		}
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextComponent("Required damage to lose durability:", EditProps.LABEL), 0.5f, 0.325f, 0.84f, 0.4f);
		addComponent(thresholdField, 0.85f, 0.325f, 0.9f, 0.425f);
		
		addComponent(new DynamicTextComponent("Blocking model: ", EditProps.LABEL), LABEL_X, 0.2f, LABEL_X + 0.2f, 0.25f);
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow()
					.setMainComponent(new EditCustomModel(ItemSet.getDefaultModelBlockingShield(textureSelect.currentTexture != null ? textureSelect.currentTexture.getName() : "TEXTURE_NAME"), this, (byte[] array) -> {
								customBlockingModel = array;
							}));
		}), BUTTON_X, 0.2f, BUTTON_X + 0.1f, 0.25f);
	}
	
	@Override
	protected String create(short damage, long maxUses, int entityHitDurabilityLoss, int blockBreakDurabilityLoss) {
		Option.Double thresholdDamage = thresholdField.getDouble();
		if (!thresholdDamage.hasValue())
			return "The required damage must be a positive number";
		return menu.getSet().addShield(
				new CustomShield(internalType.currentType, damage, name.getText(), getDisplayName(),
						lore, attributes, enchantments, maxUses, allowEnchanting.isChecked(),
						allowAnvil.isChecked(), repairItem.getIngredient(), textureSelect.currentTexture, itemFlags,
						entityHitDurabilityLoss, blockBreakDurabilityLoss, thresholdDamage.getValue(), customModel, 
						customBlockingModel, playerEffects, targetEffects, commands), true);
	}
	
	@Override
	protected String apply(short damage, long maxUses, int entityHitDurabilityLoss, int blockBreakDurabilityLoss) {
		Option.Double thresholdDamage = thresholdField.getDouble();
		if (!thresholdDamage.hasValue())
			return "The required damage must be a positive number";
		return menu.getSet().changeShield(previous, internalType.currentType, damage, name.getText(),
				getDisplayName(), lore, attributes, enchantments, allowEnchanting.isChecked(),
				allowAnvil.isChecked(), repairItem.getIngredient(), maxUses, textureSelect.currentTexture,
				itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, thresholdDamage.getValue(),
				customModel, customBlockingModel, playerEffects, targetEffects, commands, true);
	}
}
