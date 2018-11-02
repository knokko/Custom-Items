package nl.knokko.customitems.editor.set.item;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.util.bits.BitOutput;

public class CustomTool extends CustomItem {
	
	private int durability;
	
	private boolean allowEnchanting;
	private boolean allowAnvil;

	public CustomTool(CustomItemType itemType, short itemDamage, String name, String displayName, String[] lore,
			AttributeModifier[] attributes, int durability, boolean allowEnchanting, boolean allowAnvil, NamedImage texture) {
		super(itemType, itemDamage, name, displayName, lore, attributes, texture);
		this.durability = durability;
		this.allowEnchanting = allowEnchanting;
		this.allowAnvil = allowAnvil;
	}
	
	@Override
	public void export(BitOutput output) {
		output.addByte(ItemEncoding.ENCODING_TOOL_2);
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
		output.addInt(durability);
		output.addBoolean(allowEnchanting);
		output.addBoolean(allowAnvil);
	}
}