package nl.knokko.customitems.editor.set.item;

import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.util.bits.BitOutput;

public class CustomTrident extends CustomTool {
	
	public int throwDurabilityLoss;
	
	public double throwDamageMultiplier;
	public double speedMultiplier;
	
	public byte[] customInHandModel;
	public byte[] customThrowingModel;

	public CustomTrident(short itemDamage, String name, String displayName, String[] lore,
			AttributeModifier[] attributes, Enchantment[] defaultEnchantments, long durability, boolean allowEnchanting,
			boolean allowAnvil, double throwDamageMultiplier, double speedMultiplier, Ingredient repairItem, 
			NamedImage texture, boolean[] itemFlags, int entityHitDurabilityLoss, 
			int blockBreakDurabilityLoss, int throwDurabilityLoss, byte[] customModel,
			byte[] customInHandModel, byte[] customThrowingModel) {
		super(CustomItemType.TRIDENT, itemDamage, name, displayName, lore, attributes, defaultEnchantments, durability, allowEnchanting,
				allowAnvil, repairItem, texture, itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, customModel);
		this.throwDamageMultiplier = throwDamageMultiplier;
		this.speedMultiplier = speedMultiplier;
		this.throwDurabilityLoss = throwDurabilityLoss;
		this.customInHandModel = customInHandModel;
		this.customThrowingModel = customThrowingModel;
	}
	
	@Override
	public void export(BitOutput output) {
		output.addByte(ItemEncoding.ENCODING_TRIDENT_7);
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
		output.addInts(entityHitDurabilityLoss, blockBreakDurabilityLoss, throwDurabilityLoss);
		output.addDoubles(throwDamageMultiplier, speedMultiplier);
	}
}
