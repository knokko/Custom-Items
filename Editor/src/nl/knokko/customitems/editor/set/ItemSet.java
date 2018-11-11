/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2018 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
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
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.item.CustomTool;
import nl.knokko.customitems.editor.set.item.NamedImage;
import nl.knokko.customitems.editor.set.recipe.Recipe;
import nl.knokko.customitems.editor.set.recipe.ShapedRecipe;
import nl.knokko.customitems.editor.set.recipe.ShapelessRecipe;
import nl.knokko.customitems.editor.set.recipe.ingredient.CustomItemIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.editor.set.recipe.result.CustomItemResult;
import nl.knokko.customitems.editor.set.recipe.result.Result;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.AttributeModifier.Attribute;
import nl.knokko.customitems.item.AttributeModifier.Operation;
import nl.knokko.customitems.item.AttributeModifier.Slot;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;
import nl.knokko.util.bits.BitOutputStream;
import nl.knokko.util.bits.ByteArrayBitOutput;

import static nl.knokko.customitems.encoding.SetEncoding.*;

public class ItemSet {
	
	private Recipe loadRecipe(BitInput input) {
		byte encoding = input.readByte();
		if (encoding == RecipeEncoding.SHAPED_RECIPE)
			return new ShapedRecipe(input, this);
		if (encoding == RecipeEncoding.SHAPELESS_RECIPE)
			return new ShapelessRecipe(input, this);
		throw new IllegalArgumentException("Unknown recipe encoding: " + encoding);
	}
	
	private CustomItem loadItem(BitInput input) {
		byte encoding = input.readByte();
        if (encoding == ItemEncoding.ENCODING_SIMPLE_1)
            return loadSimpleItem1(input);
        else if (encoding == ItemEncoding.ENCODING_SIMPLE_2)
        	return loadSimpleItem2(input);
        else if (encoding == ItemEncoding.ENCODING_TOOL_2)
        	return loadTool2(input);
        else if (encoding == ItemEncoding.ENCODING_TOOL_3)
        	return loadTool3(input);
        throw new IllegalArgumentException("Unknown encoding: " + encoding);
	}
	
	private CustomItem loadSimpleItem1(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
        short damage = input.readShort();
        String name = input.readJavaString();
        String displayName = input.readJavaString();
        //System.out.println("itemType is " + itemType.name());
        //System.out.println("loadSimple item with damage " + damage + " and name " + name + " and displayName " + displayName);
        String[] lore = new String[input.readByte() & 0xFF];
        //System.out.println("lore length is " + lore.length);
        for(int index = 0; index < lore.length; index++){
            lore[index] = input.readJavaString();
        }
        AttributeModifier[] attributes = new AttributeModifier[0];
        String imageName = input.readJavaString();
        NamedImage texture = null;
        for(NamedImage current : textures) {
        	if(current.getName().equals(imageName)) {
        		texture = current;
        		break;
        	}
        }
        if(texture == null) throw new IllegalArgumentException("Can't find texture " + imageName);
        return new CustomItem(itemType, damage, name, displayName, lore, attributes, texture);
	}
	
	private CustomItem loadSimpleItem2(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
        short damage = input.readShort();
        String name = input.readJavaString();
        String displayName = input.readJavaString();
        String[] lore = new String[input.readByte() & 0xFF];
        for(int index = 0; index < lore.length; index++){
            lore[index] = input.readJavaString();
        }
        AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
        for (int index = 0; index < attributes.length; index++)
        	attributes[index] = loadAttribute2(input);
        String imageName = input.readJavaString();
        NamedImage texture = null;
        for(NamedImage current : textures) {
        	if(current.getName().equals(imageName)) {
        		texture = current;
        		break;
        	}
        }
        if(texture == null) throw new IllegalArgumentException("Can't find texture " + imageName);
        return new CustomItem(itemType, damage, name, displayName, lore, attributes, texture);
	}
	
	private CustomItem loadTool2(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
        short damage = input.readShort();
        String name = input.readJavaString();
        String displayName = input.readJavaString();
        String[] lore = new String[input.readByte() & 0xFF];
        for(int index = 0; index < lore.length; index++){
            lore[index] = input.readJavaString();
        }
        AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
        for (int index = 0; index < attributes.length; index++)
        	attributes[index] = loadAttribute2(input);
        int durability = input.readInt();
        boolean allowEnchanting = input.readBoolean();
        boolean allowAnvil = input.readBoolean();
        String imageName = input.readJavaString();
        NamedImage texture = null;
        for(NamedImage current : textures) {
        	if(current.getName().equals(imageName)) {
        		texture = current;
        		break;
        	}
        }
        if(texture == null) throw new IllegalArgumentException("Can't find texture " + imageName);
        return new CustomTool(itemType, damage, name, displayName, lore, attributes, durability, 
        		allowEnchanting, allowAnvil, new NoIngredient(), texture);
	}
	
	private CustomItem loadTool3(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
        short damage = input.readShort();
        String name = input.readJavaString();
        String displayName = input.readJavaString();
        String[] lore = new String[input.readByte() & 0xFF];
        for(int index = 0; index < lore.length; index++){
            lore[index] = input.readJavaString();
        }
        AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
        for (int index = 0; index < attributes.length; index++)
        	attributes[index] = loadAttribute2(input);
        int durability = input.readInt();
        boolean allowEnchanting = input.readBoolean();
        boolean allowAnvil = input.readBoolean();
        Ingredient repairItem = Recipe.loadIngredient(input, this);
        String imageName = input.readJavaString();
        NamedImage texture = null;
        for(NamedImage current : textures) {
        	if(current.getName().equals(imageName)) {
        		texture = current;
        		break;
        	}
        }
        if(texture == null) throw new IllegalArgumentException("Can't find texture " + imageName);
        return new CustomTool(itemType, damage, name, displayName, lore, attributes, durability, 
        		allowEnchanting, allowAnvil, repairItem, texture);
	}
	
	private AttributeModifier loadAttribute2(BitInput input) {
		return new AttributeModifier(Attribute.valueOf(input.readJavaString()), Slot.valueOf(input.readJavaString()), 
				Operation.values()[(int) input.readNumber((byte) 2, false)], input.readDouble());
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
		//System.out.println("amount of textures is " + textureAmount);
		textures = new ArrayList<NamedImage>(textureAmount);
		for (int counter = 0; counter < textureAmount; counter++)
			textures.add(new NamedImage(input));
		//System.out.println("textures are " + textures);
		// Items
		int itemAmount = input.readInt();
		//System.out.println("amount of items is " + itemAmount);
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
					String name = entry.getKey().name().toLowerCase();
					if (entry.getKey() == CustomItemType.CARROT_STICK)
						name = "carrot_on_a_stick";
					String textureName = name;
					if (name.startsWith("gold"))
						name = name.replace("gold", "golden");
					ZipEntry zipEntry = new ZipEntry("assets/minecraft/models/item/" + name + ".json");
					zipOutput.putNextEntry(zipEntry);
					PrintWriter jsonWriter = new PrintWriter(zipOutput);
					
					// Begin of the json file
					jsonWriter.println("{");
					jsonWriter.println("    " + Q + "parent" + Q + ": " + Q + "item/handheld" + Q + ",");
					jsonWriter.println("    " + Q + "textures" + Q + ": {");
					jsonWriter.println("        " + Q + "layer0" + Q + ": " + Q + "items/" + textureName + Q);
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
		output.addInt(recipes.size());
		for (Recipe recipe : recipes)
			recipe.save(output);
	}
	
	public String save() {
		try {
			Editor.getFolder().mkdir();
			Editor.getBackupFolder().mkdir();
			File file = new File(Editor.getFolder() + "/" + fileName + ".cisb");// cisb stands for Custom Item Set Builder
			ByteArrayBitOutput output = new ByteArrayBitOutput();
			save1(output);
			output.terminate();
			byte[] bytes = output.getBytes();
			FileOutputStream mainOutput = new FileOutputStream(file);
			mainOutput.write(bytes);
			mainOutput.close();
			FileOutputStream backupOutput = new FileOutputStream(new File(Editor.getBackupFolder() + "/" + fileName + " " + System.currentTimeMillis() + ".cisb"));
			backupOutput.write(bytes);
			backupOutput.close();
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
		
		// Save the normal items before the tools so that tools can use normal items as repair item
		List<CustomItem> sorted = new ArrayList<CustomItem>(items.size());
		for (CustomItem item : items) {
			if (!(item instanceof CustomTool)) {
				sorted.add(item);
			}
		}
		for (CustomItem item : items) {
			if (item instanceof CustomTool) {
				sorted.add(item);
			}
		}
		for(CustomItem item : sorted)
			item.save(output);
		
		output.addInt(recipes.size());
		for (Recipe recipe : recipes)
			recipe.save(output);
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
	 * Attempts to add the specified tool to this item set.
	 * If the tool can be added, it will be added.
	 * If the tool can't be added, the reason is returned.
	 * @param item The tool that should be added to this item set
	 * @return The reason the tool could not be added, or null if the tool was added succesfully
	 */
	public String addTool(CustomTool item) {
		if (item == null) return "Can't add null items";
		if (item.getClass() != CustomTool.class) return "Use the appropriate method for that class";
		for(CustomItem current : items) {
			if(current.getName().equals(item.getName()))
				return "There is already a custom item with that name";
			if(current.getItemType() == item.getItemType() && current.getItemDamage() == item.getItemDamage())
				return "There is already a custom item with the same item type and damage";
		}
		if (item.getRepairItem() instanceof CustomItemIngredient && !(((CustomItemIngredient) item.getRepairItem()).getItem().getClass() == CustomItem.class))
			return "Only vanilla items and simple custom items are allowed as repair item.";
		if (item.allowAnvilActions() && item.getDisplayName().contains("§"))
			return "Items with color codes in their display name can not allow anvil actions";
		return addItem(item, false);
	}
	
	/**
	 * Attempts to change the specified tool in this item set.
	 * If the tool can be changed, it will be changed.
	 * If the tool can't be changed, the reason is returned.
	 * @param item The tool that should be changed
	 * @param newType The new item type of the tool
	 * @param newDamage The new internal damage of the tool
	 * @param newName The new name of the tool
	 * @param newDisplayName The new display name of the tool
	 * @param newLore The new lore of the tool
	 * @param newAttributes The new attribute modifiers of the tool
	 * @param allowEnchanting The new value of allowEnchanting of the tool
	 * @param allowAnvil The new value of allowAnvil of the tool
	 * @param newDurability The new maximum uses of the tool
	 * @param newImage The new image of the tool
	 * @return The reason the tool could not be changed, or null if the tool was changed successfully
	 */
	public String changeTool(CustomTool item, CustomItemType newType, short newDamage, String newName, 
			String newDisplayName, String[] newLore, AttributeModifier[] newAttributes, boolean allowEnchanting,
			boolean allowAnvil, Ingredient repairItem, int newDurability, NamedImage newImage) {
		boolean has = false;
		if (newImage == null) return "Every item needs a texture";
		if (allowAnvil && newDisplayName.contains("§"))
			return "Items with color codes in their display name can not allow anvil actions";
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
		if (repairItem instanceof CustomItemIngredient && !(((CustomItemIngredient) repairItem).getItem().getClass() == CustomItem.class))
			return "Only vanilla items and simple custom items are allowed as repair item.";
		item.setItemType(newType);
		item.setItemDamage(newDamage);
		item.setName(newName);
		item.setDisplayName(newDisplayName);
		item.setLore(newLore);
		item.setAttributes(newAttributes);
		item.setAllowEnchanting(allowEnchanting);
		item.setAllowAnvilActions(allowAnvil);
		item.setRepairItem(repairItem);
		item.setDurability(newDurability);
		item.setTexture(newImage);
		return null;
	}
	
	private String addItem(CustomItem item, boolean doClassCheck) {
		if (item == null) return "Can't add null items";
		if (doClassCheck && item.getClass() != CustomItem.class) return "Use the appropriate method for that class";
		if (item.getTexture() == null) return "Every item needs a texture";
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
	 * Attempts to add the specified item to this item set.
	 * If the item can be added, it will be added.
	 * If the item can't be added, the reason is returned.
	 * @param item The item that should be added to this set
	 * @return The reason the item could not be added, or null if the item was added successfully
	 */
	public String addItem(CustomItem item) {
		return addItem(item, true);
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
	 * @param newAttributes The new attribute modifiers of the item
	 * @param newImage The new image of the item
	 * @return null if the item was changed successfully or the reason the item could not be changed
	 */
	public String changeItem(CustomItem item, CustomItemType newType, short newDamage, String newName, 
			String newDisplayName, String[] newLore, AttributeModifier[] newAttributes, NamedImage newImage) {
		boolean has = false;
		if (newImage == null) return "Every item needs a texture";
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
		item.setAttributes(newAttributes);
		item.setTexture(newImage);
		return null;
	}
	
	/**
	 * Attempts to remove the specified item from this set.
	 * If the item could not be removed, the reason is returned.
	 * If the item can be removed, it will be removed and null will be returned.
	 * @param item The item that should be removed from this ItemSet
	 * @return The reason the item could not be removed, or null if the item was removed successfully.
	 */
	public String removeItem(CustomItem item) {
		for (Recipe recipe : recipes) {
			if (recipe.getResult() instanceof CustomItemResult && ((CustomItemResult)recipe.getResult()).getItem() == item)
				return "At least one of your recipes has this item as result.";
			if (recipe.requires(item))
				return "At least one of your recipes has this item as an ingredient.";
		}
		items.remove(item);
		return null;
	}
	
	/**
	 * Attempts to add a shaped recipe with the specified id, ingredients and result to this ItemSet.
	 * If the recipe can be added, it will be added.
	 * If the recipe can't be added, the reason is returned.
	 * @param id The id of the recipe to add
	 * @param ingredients The ingredients of the recipe to add
	 * @param result The result of the recipe to add
	 * @return The reason why the recipe can't be added, or null if the recipe was added successfully
	 */
	public String addShapedRecipe(Ingredient[] ingredients, Result result) {
		for (Recipe recipe : recipes) 
			if (recipe.hasConflictingShapedIngredients(ingredients))
				return "The ingredients of another recipe conflict with these ingredients.";
		recipes.add(new ShapedRecipe(ingredients, result));
		return null;
	}
	
	/**
	 * Attempts to change the ingredients and result of the specified ShapedRecipe.
	 * If the recipe can be changed, it will be changed.
	 * If the recipe can't be changed, the reason is returned.
	 * @param previous The recipe to change
	 * @param ingredients The new ingredients for the recipe
	 * @param result The new result for the recipe
	 * @return The reason the recipe can't be changed, or null if the recipe changed succesfully.
	 */
	public String changeShapedRecipe(ShapedRecipe previous, Ingredient[] ingredients, Result result) {
		boolean has = false;
		for (Recipe recipe : recipes) {
			if (recipe == previous) {
				has = true;
				break;
			} else if (recipe.hasConflictingShapedIngredients(ingredients)) {
				return "Another shaped recipe (" + recipe.getResult() + ") has conflicting ingredients";
			}
		}
		if (!has) return "That recipe is not in this item set";
		previous.setIngredients(ingredients);
		previous.setResult(result);
		return null;
	}
	
	/**
	 * Attempts to add a shapeless recipe with the specified ingredients and result to this set.
	 * If the recipe can be added, it will be added.
	 * If the recipe can't be added, the reason is returned.
	 * @param ingredients The ingredients of the shapeless recipe
	 * @param result The result of the shapeless recipe
	 * @return The reason the recipe could not be added, or null if the recipe was added successfully
	 */
	public String addShapelessRecipe(Ingredient[] ingredients, Result result) {
		for (Recipe recipe : recipes)
			if (recipe.hasConflictingShapelessIngredients(ingredients))
				return "Another shapeless recipe (" + recipe.getResult() + ") has conflicting ingredients";
		recipes.add(new ShapelessRecipe(result, ingredients));
		return null;
	}
	
	/**
	 * Attempts to change the ingredients and result of the specified shapeless recipe.
	 * If the recipe can be changed, it will be changed.
	 * If the recipe can't be changed, the reason is returned.
	 * @param previous The shapeless recipe to change
	 * @param newIngredients The new ingredients of the recipe
	 * @param newResult The new result of the recipe
	 * @return The reason the recipe could not be changed, or null if the recipe was changed successfully.
	 */
	public String changeShapelessRecipe(ShapelessRecipe previous, Ingredient[] newIngredients, Result newResult) {
		boolean has = false;
		for (Recipe recipe : recipes) {
			if (recipe == previous) {
				has = true;
				break;
			} else if (recipe.hasConflictingShapelessIngredients(newIngredients)) {
				return "Another shapeless recipe (" + recipe.getResult() + ") has conflicting ingredients";
			}
		}
		if (!has) return "That recipe is not in this item set";
		previous.setIngredients(newIngredients);
		previous.setResult(newResult);
		return null;
	}
	
	public void removeRecipe(Recipe recipe) {
		recipes.remove(recipe);
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
	
	/**
	 * Do not modify this collection directly!
	 * @return The Recipe collection of this ItemSet
	 */
	public Collection<Recipe> getRecipes(){
		return recipes;
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