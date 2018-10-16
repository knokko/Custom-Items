package nl.knokko.customitems.editor.set;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import nl.knokko.customitems.editor.Editor;
import nl.knokko.customitems.editor.set.recipe.Recipe;
import nl.knokko.customitems.editor.set.recipe.ShapedRecipe;
import nl.knokko.customitems.editor.set.recipe.ShapelessRecipe;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;
import nl.knokko.util.bits.BitOutputStream;

import static nl.knokko.customitems.encoding.SetEncoding.*;

public class ItemSet {
	
	private Recipe loadRecipe(BitInput input) {
		byte encoding = input.readByte();
		if (encoding == RecipeEncoding.SHAPED_RECIPE)
			return new ShapedRecipe(input);
		if (encoding == RecipeEncoding.SHAPELESS_RECIPE)
			return new ShapelessRecipe(input);
		throw new IllegalArgumentException("Unknown recipe encoding: " + encoding);
	}
	
	private CustomItem loadItem(BitInput input) {
		byte encoding = input.readByte();
        if(encoding == ItemEncoding.ENCODING_SIMPLE_1){
            return loadSimpleItem1(input);
        }
        throw new IllegalArgumentException("Unknown encoding: " + encoding);
	}
	
	private CustomItem loadSimpleItem1(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
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
	private Collection<Recipe> recipes;
	
	public ItemSet(String fileName) {
		this.fileName = fileName;
		textures = new ArrayList<NamedImage>();
		items = new ArrayList<CustomItem>();
		recipes = new ArrayList<Recipe>();
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
		// Textures
		int textureAmount = input.readInt();
		textures = new ArrayList<NamedImage>(textureAmount);
		for (int counter = 0; counter < textureAmount; counter++)
			textures.add(new NamedImage(input));
		
		// Items
		int itemAmount = input.readInt();
		items = new ArrayList<CustomItem>(itemAmount);
		for (int counter = 0; counter < itemAmount; counter++)
			items.add(loadItem(input));
		
		// Recipes
		int recipeAmount = input.readInt();
		recipes = new ArrayList<Recipe>(recipeAmount);
		for (int counter = 0; counter < recipeAmount; counter++)
			recipes.add(loadRecipe(input));
	}
	
	/**
	 * A String containing only the quote character.
	 * I use this constant because it's annoying to get that character inside a String
	 */
	private static final String Q = "" + '"';
	
	public String export() {
		try {
			File file = new File(Editor.getFolder() + "/" + fileName + ".cis");// cis stands for Custom Item Set
			BitOutput output = new BitOutputStream(new FileOutputStream(file));
			export1(output);
			output.terminate();
			ZipOutputStream zipOutput = new ZipOutputStream(new FileOutputStream(new File(Editor.getFolder() + "/" + fileName + ".zip")));
			
			// Custom textures
			for(NamedImage texture : textures) {
				ZipEntry entry = new ZipEntry("assets/minecraft/textures/customitems/" + texture.getName() + ".png");
				zipOutput.putNextEntry(entry);
				ImageIO.write(texture.getImage(), "PNG", new MemoryCacheImageOutputStream(zipOutput));
				zipOutput.closeEntry();
			}
			
			// Custom item models
			for(CustomItem item : items) {
				ZipEntry entry = new ZipEntry("assets/minecraft/models/customitems/" + item.getName() + ".json");
				zipOutput.putNextEntry(entry);
				PrintWriter jsonWriter = new PrintWriter(zipOutput);
				jsonWriter.println("{");
				jsonWriter.println("    " + Q + "parent" + Q + ": " + Q + "item/handheld" + Q + ",");
				jsonWriter.println("    " + Q + "textures" + Q + ": {");
				jsonWriter.println("        " + Q + "layer0" + Q + ": " + Q + "customitems/" + item.getTexture().getName() + Q);
				jsonWriter.println("    }");
				jsonWriter.println("}");
				jsonWriter.flush();
				zipOutput.closeEntry();
			}
			
			// Map all custom items by their item type
			Map<CustomItemType, List<CustomItem>> itemMap = new EnumMap<CustomItemType, List<CustomItem>>(CustomItemType.class);
			for(CustomItem item : items) {
				List<CustomItem> list = itemMap.get(item.getItemType());
				if(list == null) {
					list = new ArrayList<CustomItem>();
					itemMap.put(item.getItemType(), list);
				}
				list.add(item);
			}
			
			// Now create the item model files for those models
			Set<Entry<CustomItemType, List<CustomItem>>> entrySet = itemMap.entrySet();
			for(Entry<CustomItemType, List<CustomItem>> entry : entrySet) {
				List<CustomItem> list = entry.getValue();
				if(list != null) {
					// The items with low damage should come first
					list.sort((CustomItem a, CustomItem b) -> {
						if(a.getItemDamage() > b.getItemDamage()) return 1;
						if(a.getItemDamage() < b.getItemDamage()) return -1;
						if(a == b) return 0;
						throw new IllegalArgumentException("a is " + a + " and b is " + b);
					});
					String name = entry.getKey().toString().toLowerCase();
					ZipEntry zipEntry = new ZipEntry("assets/minecraft/models/item/" + name + ".json");
					zipOutput.putNextEntry(zipEntry);
					PrintWriter jsonWriter = new PrintWriter(zipOutput);
					
					// Begin of the json file
					jsonWriter.println("{");
					jsonWriter.println("    " + Q + "parent" + Q + ": " + Q + "item/handheld" + Q + ",");
					jsonWriter.println("    " + Q + "textures" + Q + ": {");
					jsonWriter.println("        " + Q + "layer0" + Q + ": " + Q + "items/" + name + Q);
					jsonWriter.println("    },");
					jsonWriter.println("    " + Q + "overrides" + Q + ": [");
					
					// Now the interesting part
					for(CustomItem item : list) {
						jsonWriter.println("        { " + Q + "predicate" + Q + ": {" + Q + "damaged" + Q + 
								": 0, " + Q + "damage" + Q + ": " + 
								(double) item.getItemDamage() / item.getItemType().getMaxDurability() + "}, " + Q 
								+ "model" + Q + ": " + Q + "customitems/" + item.getName() + Q + "},");
					}
					
					// End of the json file
					jsonWriter.println("        { " + Q + "predicate" + Q + ": {" + Q + "damaged" + Q + ": 1, " + Q + "damage" + Q + 
							": 0}, " + Q + "model" + Q + ": " + Q + "item/" + name + Q + "}");
					jsonWriter.println("    ]");
					jsonWriter.println("}");
					jsonWriter.flush();
					zipOutput.closeEntry();
				}
			}
			
			// pack.mcmeta
			ZipEntry mcMeta = new ZipEntry("pack.mcmeta");
			zipOutput.putNextEntry(mcMeta);
			PrintWriter jsonWriter = new PrintWriter(zipOutput);
			jsonWriter.println("{");
			jsonWriter.println("    " + Q + "pack" + Q + ": {");
			jsonWriter.println("        " + Q + "pack_format" + Q + ": 3,");
			jsonWriter.println("        " + Q + "description" + Q + ": " + Q + "CustomItemSet" + Q);
			jsonWriter.println("    }");
			jsonWriter.println("}");
			jsonWriter.flush();
			zipOutput.closeEntry();
			
			zipOutput.close();
			return null;
		} catch(IOException ioex) {
			ioex.printStackTrace();
			return ioex.getMessage();
		}
	}
	
	private void export1(BitOutput output) {
		output.addByte(ENCODING_1);
		
		// Items
		output.addInt(items.size());
		for(CustomItem item : items)
			item.export(output);
		
		// Recipes
		output.addInt(0);
	}
	
	public String save() {
		try {
			File file = new File(Editor.getFolder() + "/" + fileName + ".cisb");// cisb stands for Custom Item Set Builder
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
		
		// Recipes
		output.addInt(0);
	}
	
	/**
	 * Attempts to add the specified texture to this item set.
	 * If the texture can be added, it will be added.
	 * If the texture can't be added, the reason is returned.
	 * @param texture The texture that should be added to this item set
	 * @return The reason the texture could not be added, or null if the texture was added successfully
	 */
	public String addTexture(NamedImage texture) {
		if(texture == null)
			return "Can't add null textures";
		for(NamedImage current : textures) 
			if(current.getName().equals(texture.getName()))
				return "There is already a texture with that name";
		textures.add(texture);
		return null;
	}
	
	/**
	 * Attempts to change the specified texture in this item set.
	 * If the texture can be changed, it will be changed.
	 * if the texture can't be changed, the reason is returned.
	 * @param texture The texture to change
	 * @param newName The new name of the texture (possibly the old name)
	 * @param newImage The new image of the texture (possibly the old image)
	 * @return The reason the texture could not be changed, or null if the texture changed successfully
	 */
	public String changeTexture(NamedImage texture, String newName, BufferedImage newImage) {
		boolean has = false;
		for(NamedImage current : textures) {
			if(current == texture)
				has = true;
			else if(current.getName().equals(texture.getName()))
				return "Another texture has that name already";
		}
		if(!has) return "The previous texture is not in the list!";
		texture.setName(newName);
		texture.setImage(newImage);
		return null;
	}
	
	/**
	 * Attempts to remove the specified texture from this item set.
	 * If the texture could not be removed, the reason is returned.
	 * If the texture could be removed, it will be removed.
	 * @param texture The texture that should be removed from this set
	 * @return The reason the texture could not be removed, or null if the texture was removed successfully.
	 */
	public String removeTexture(NamedImage texture) {
		boolean has = false;
		for(NamedImage current : textures) {
			if(current == texture) {
				has = true;
				break;
			}
		}
		if(!has) return "That texture is not in this item set.";
		for(CustomItem item : items)
			if(item.getTexture() == texture)
				return "That texture is used by " + item.getName();
		textures.remove(texture);
		return null;
	}
	
	/**
	 * Attempts to add the specified item to this item set.
	 * If the item can be added, it will be added.
	 * If the item can't be added, the reason is returned.
	 * @param item The item that should be added to this set
	 * @return The reason the item could not be added, or null if the item was added successfully
	 */
	public String addItem(CustomItem item) {
		if(item == null) return "Can't add null items";
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
	public String changeItem(CustomItem item, CustomItemType newType, short newDamage, String newName, String newDisplayName, String[] newLore, NamedImage newImage) {
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
	
	public short nextAvailableDamage(CustomItemType type) {
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