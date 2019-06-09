package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.item.CustomHoe;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.AttributeModifier.Attribute;
import nl.knokko.customitems.item.AttributeModifier.Operation;
import nl.knokko.customitems.item.AttributeModifier.Slot;
import nl.knokko.customitems.item.CustomItemType.Category;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public class EditItemHoe extends EditItemTool {
	
	private static final AttributeModifier EXAMPLE_ATTRIBUTE_MODIFIER = new AttributeModifier(Attribute.MOVEMENT_SPEED, Slot.OFFHAND, Operation.MULTIPLY, 1.5);

	private final CustomHoe previous;
	
	private final IntEditField tillDurabilityLoss;

	public EditItemHoe(EditMenu menu, CustomHoe previous) {
		super(menu, previous, Category.HOE);
		this.previous = previous;
		if (previous == null) {
			tillDurabilityLoss = new IntEditField(1, 0, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		} else {
			tillDurabilityLoss = new IntEditField(previous.getTillDurabilityLoss(), 0, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		}
	}
	
	@Override
	protected AttributeModifier getExampleAttributeModifier() {
		return EXAMPLE_ATTRIBUTE_MODIFIER;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextComponent("Durability loss on tilling:", EditProps.LABEL), 0.6f, 0.35f, 0.84f, 0.425f);
		addComponent(tillDurabilityLoss, 0.85f, 0.35f, 0.9f, 0.425f);
	}
	
	@Override
	protected CustomHoe previous() {
		return previous;
	}
	
	@Override
	protected String create(short damage, long maxUses, int entityHitDurabilityLoss, int blockBreakDurabilityLoss) {
		Option.Int durLoss = tillDurabilityLoss.getInt();
		if (!durLoss.hasValue())
			return "The till durability loss must be a positive integer";
		return menu.getSet().addHoe(
				new CustomHoe(internalType.currentType, damage, name.getText(), getDisplayName(),
						lore, attributes, enchantments, maxUses, allowEnchanting.isChecked(),
						allowAnvil.isChecked(), repairItem.getIngredient(), textureSelect.currentTexture, itemFlags,
						entityHitDurabilityLoss, blockBreakDurabilityLoss, durLoss.getValue()),
						true);
	}
	
	@Override
	protected String apply(short damage, long maxUses, int entityHitDurabilityLoss, int blockBreakDurabilityLoss) {
		Option.Int durLoss = tillDurabilityLoss.getInt();
		if (!durLoss.hasValue())
			return "The shear durability loss must be a positive integer";
		return menu.getSet().changeHoe(previous, internalType.currentType, damage, name.getText(),
				getDisplayName(), lore, attributes, enchantments, allowEnchanting.isChecked(),
				allowAnvil.isChecked(), repairItem.getIngredient(), maxUses, textureSelect.currentTexture,
				itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, durLoss.getValue(),
				true);
	}
}