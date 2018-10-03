package nl.knokko.customitems.editor.set;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import nl.knokko.customitems.editor.Editor;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.item.ItemType;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;
import nl.knokko.util.bits.BitOutputStream;

public class ItemSet {
	
	private static final byte ENCODING_1 = 0;
	
	private CustomItem loadItem(BitInput input) {
		byte encoding = input.readByte();
        if(encoding == ItemEncoding.ENCODING_SIMPLE_1){
            return loadSimpleItem1(input);
        }
        throw new IllegalArgumentException("Unknown encoding: " + encoding);
	}
	
	private CustomItem loadSimpleItem1(BitInput input) {
		ItemType itemType = ItemType.valueOf(input.readJavaString());
        short damage = input.readShort();
        String name = input.readJavaString();
        String displayName = input.readJavaString();
        String[] lore = new String[input.readByte() & 0xFF];
        for(int index = 0; index < lore.length; index++){
            lore[index] = input.readJavaString();
        }
        String imageName = input.readJavaString();
        NamedImage texture = null;
        for(NamedImage current : textures) {
        	if(current.getName().equals(imageName)) {
        		texture = current;
        		break;
        	}
        }
        if(texture == null) throw new IllegalArgumentException("Can't find texture " + imageName);
        return new CustomItem(itemType, damage, name, displayName, lore, texture);
	}
	
	private final String fileName;
	
	private Collection<NamedImage> textures;
	private Collection<CustomItem> items;
	
	public ItemSet(String fileName) {
		this.fileName = fileName;
		textures = new ArrayList<NamedImage>();
		items = new ArrayList<CustomItem>();
	}
	
	public ItemSet(String fileName, BitInput input) {
		this.fileName = fileName;
		byte encoding = input.readByte();
		if(encoding == ENCODING_1)
			load1(input);
		else
			throw new IllegalArgumentException("Unknown encoding: " + encoding);
	}
	
	private void load1(BitInput input) {
		int textureAmount = input.readInt();
		textures = new ArrayList<NamedImage>(textureAmount);
		for(int counter = 0; counter < textureAmount; counter++)
			textures.add(new NamedImage(input));
		int itemAmount = input.readInt();
		items = new ArrayList<CustomItem>(itemAmount);
		for(int counter = 0; counter < itemAmount; counter++)
			items.add(loadItem(input));
	}
	
	public String save() {
		try {
			File file = new File(Editor.getFolder() + "/" + fileName + ".cis");
			BitOutput output = new BitOutputStream(new FileOutputStream(file));
			save1(output);
			output.terminate();
			return null;
		} catch(IOException ioex) {
			ioex.printStackTrace();
			return ioex.getMessage();
		}
	}
	
	private void save1(BitOutput output) {
		output.addByte(ENCODING_1);
		output.addInt(textures.size());
		for(NamedImage texture : textures)
			texture.save(output);
		output.addInt(items.size());
		for(CustomItem item : items)
			item.save(output);
	}
	
	/**
	 * Attempts to add the specified item to this item set.
	 * If the item can be added, it will be added.
	 * If the item can't be added, the reason is returned.
	 * @param item The item that should be added to this set
	 * @return The reason the item could not be added, or null if the item was added succesfully
	 */
	public String addItem(CustomItem item) {
		for(CustomItem current : items) {
			if(current.getName().equals(item.getName()))
				return "There is already a custom item with that name";
			if(current.getItemType() == item.getItemType() && current.getItemDamage() == item.getItemDamage())
				return "There is already a custom item with the same item type and damage";
		}
		items.add(item);
		return null;
	}
	
	/**
	 * Attempts to change the specified item in this item set.
	 * If the item can be changed, it will be changed.
	 * If the item can't be changed, the reason is returned.
	 * @param item The item that should be changed
	 * @param newType The new type of the item
	 * @param newDamage The new damage of the item
	 * @param newName The new name of the item
	 * @param newDisplayName The new display name of the item
	 * @param newLore The new lore of the item
	 * @param newImage The new image of the item
	 * @return null if the item was changed successfully or the reason the item could not be changed
	 */
	public String changeItem(CustomItem item, ItemType newType, short newDamage, String newName, String newDisplayName, String[] newLore, NamedImage newImage) {
		boolean has = false;
		for(CustomItem current : items) {
			if(current == item) {
				has = true;
				break;
			}
			else {
				if(current.getItemType() == newType && current.getItemDamage() == newDamage) {
					return "The item " + current.getName() + " has the same internal type and damage.";
				}
			}
		}
		if(!has) return "There is no previous item!";
		has = false;
		for(NamedImage texture : textures)
			if(texture == newImage)
				has = true;
		if(!has) return "The specified texture is not in the texture list!";
		item.setItemType(newType);
		item.setItemDamage(newDamage);
		item.setName(newName);
		item.setDisplayName(newDisplayName);
		item.setLore(newLore);
		item.setTexture(newImage);
		return null;
	}
	
	public void removeItem(CustomItem item) {
		items.remove(item);
	}
	
	/**
	 * Do not modify this collection directly!
	 * @return The CustomItem collection of this ItemSet
	 */
	public Collection<CustomItem> getItems(){
		return items;
	}
	
	/**
	 * Do not modify this collection directly!
	 * @return The NamedImage collection of this ItemSet
	 */
	public Collection<NamedImage> getTextures(){
		return textures;
	}
	
	public short nextAvailableDamage(ItemType type) {
		boolean[] usedDamage = new boolean[type.getMaxDurability() - 1];
		for(CustomItem item : items)
			if(item.getItemType() == type)
				usedDamage[item.getItemDamage() - 1] = true;
		for(short damage = 1; damage < type.getMaxDurability(); damage++)
			if(!usedDamage[damage - 1])
				return damage;
		return -1;
	}
}