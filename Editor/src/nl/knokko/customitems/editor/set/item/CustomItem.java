package nl.knokko.customitems.editor.set.item;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.util.bits.BitOutput;

public class CustomItem extends nl.knokko.customitems.item.CustomItem {
	
	protected NamedImage texture;

	public CustomItem(CustomItemType itemType, short itemDamage, String name, String displayName, String[] lore, 
			AttributeModifier[] attributes, NamedImage texture) {
		super(itemType, itemDamage, name, displayName, lore, attributes);
		this.texture = texture;
	}
	
	public NamedImage getTexture() {
		return texture;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDisplayName(String name) {
		displayName = name;
	}
	
	public void setItemType(CustomItemType type) {
		itemType = type;
	}
	
	public void setItemDamage(short damage) {
		itemDamage = damage;
	}
	
	public void setLore(String[] lore) {
		this.lore = lore;
	}
	
	public void setAttributes(AttributeModifier[] attributes) {
		this.attributes = attributes;
	}
	
	public void setTexture(NamedImage texture) {
		this.texture = texture;
	}
	
	public void save(BitOutput output) {
		export(output);
		output.addJavaString(texture.getName());
	}
	
	public void export(BitOutput output) {
		/*
		 * This is for ENCODING_SIMPLE_1
		output.addByte(ItemEncoding.ENCODING_SIMPLE_1);
		output.addJavaString(itemType.name());
		output.addShort(itemDamage);
		output.addJavaString(name);
		output.addJavaString(displayName);
		output.addByte((byte) lore.length);
		for(String line : lore)
			output.addJavaString(line);
		*/
		
		output.addByte(ItemEncoding.ENCODING_SIMPLE_2);
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
	}
}