package nl.knokko.customitems.editor.set.item;

import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.util.bits.BitOutput;

public class CustomShears extends CustomTool {
	
	protected int shearDurabilityLoss;

	public CustomShears(CustomItemType itemType, short itemDamage, String name, String displayName, String[] lore,
			AttributeModifier[] attributes, Enchantment[] defaultEnchantments, long durability, boolean allowEnchanting,
			boolean allowAnvil, Ingredient repairItem, NamedImage texture, boolean[] itemFlags,
			int entityHitDurabilityLoss, int blockBreakDurabilityLoss, int shearDurabilityLoss, 
			byte[] customModel) {
		super(itemType, itemDamage, name, displayName, lore, attributes, defaultEnchantments, durability, allowEnchanting,
				allowAnvil, repairItem, texture, itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, customModel);
		this.shearDurabilityLoss = shearDurabilityLoss;
	}
	
	@Override
	public void export(BitOutput output) {
		output.addByte(ItemEncoding.ENCODING_SHEAR_5);
		output.addJavaString(itemType.name());
		output.addShort(itemDamage);
		output.addJavaString(name);
		output.addJavaString(displayName);
		output.addByte((byte) lore.length);
		for(String line : lore)
			output.addJavaString(line);
		output.addByte((byte) attributes.length);
		for (AttributeModifier attribute : attributes) {
			output.addJavaString(attribute.getAttribute().name());
			output.addJavaString(attribute.getSlot().name());
			output.addNumber(attribute.getOperation().ordinal(), (byte) 2, false);
			output.addDouble(attribute.getValue());
		}
		output.addByte((byte) defaultEnchantments.length);
		for (Enchantment enchantment : defaultEnchantments) {
			output.addString(enchantment.getType().name());
			output.addInt(enchantment.getLevel());
		}
		output.addLong(durability);
		output.addBoolean(allowEnchanting);
		output.addBoolean(allowAnvil);
		repairItem.save(output);
		output.addBooleans(itemFlags);
		output.addInts(entityHitDurabilityLoss, blockBreakDurabilityLoss, shearDurabilityLoss);
	}
	
	public int getShearDurabilityLoss() {
		return shearDurabilityLoss;
	}
	
	public void setShearDurabilityLoss(int newDurabilityLoss) {
		shearDurabilityLoss = newDurabilityLoss;
	}
}