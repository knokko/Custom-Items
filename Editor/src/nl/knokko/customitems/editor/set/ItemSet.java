/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2019 knokko
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
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
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

import nl.knokko.customitems.damage.DamageResistances;
import nl.knokko.customitems.drops.BlockDrop;
import nl.knokko.customitems.drops.BlockType;
import nl.knokko.customitems.drops.CIEntityType;
import nl.knokko.customitems.drops.Drop;
import nl.knokko.customitems.drops.EntityDrop;
import nl.knokko.customitems.editor.Editor;
import nl.knokko.customitems.editor.set.item.CustomArmor;
import nl.knokko.customitems.editor.set.item.CustomBow;
import nl.knokko.customitems.editor.set.item.CustomHoe;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.item.CustomShears;
import nl.knokko.customitems.editor.set.item.CustomTool;
import nl.knokko.customitems.editor.set.item.NamedImage;
import nl.knokko.customitems.editor.set.item.SimpleCustomItem;
import nl.knokko.customitems.editor.set.item.texture.BowTextures;
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
import nl.knokko.customitems.item.CustomToolDurability;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.EnchantmentType;
import nl.knokko.customitems.item.ItemFlag;
import nl.knokko.customitems.item.ItemSetBase;
import nl.knokko.gui.keycode.KeyCode;
import nl.knokko.gui.window.input.WindowInput;
import nl.knokko.customitems.item.AttributeModifier.Attribute;
import nl.knokko.customitems.item.AttributeModifier.Operation;
import nl.knokko.customitems.item.AttributeModifier.Slot;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;
import nl.knokko.util.bits.ByteArrayBitOutput;

import static nl.knokko.customitems.encoding.SetEncoding.*;

public class ItemSet implements ItemSetBase {

	private Recipe loadRecipe(BitInput input) {
		byte encoding = input.readByte();
		if (encoding == RecipeEncoding.SHAPED_RECIPE)
			return new ShapedRecipe(input, this);
		if (encoding == RecipeEncoding.SHAPELESS_RECIPE)
			return new ShapelessRecipe(input, this);
		throw new IllegalArgumentException("Unknown recipe encoding: " + encoding);
	}

	private CustomItem loadItem(BitInput input, boolean checkCustomModel) {
		byte encoding = input.readByte();
		if (encoding == ItemEncoding.ENCODING_SIMPLE_1)
			return loadSimpleItem1(input, checkCustomModel);
		else if (encoding == ItemEncoding.ENCODING_SIMPLE_2)
			return loadSimpleItem2(input, checkCustomModel);
		else if (encoding == ItemEncoding.ENCODING_SIMPLE_3)
			return loadSimpleItem3(input, checkCustomModel);
		else if (encoding == ItemEncoding.ENCODING_SIMPLE_4)
			return loadSimpleItem4(input, checkCustomModel);
		else if (encoding == ItemEncoding.ENCODING_SIMPLE_5)
			return loadSimpleItem5(input, checkCustomModel);
		else if (encoding == ItemEncoding.ENCODING_TOOL_2)
			return loadTool2(input, checkCustomModel);
		else if (encoding == ItemEncoding.ENCODING_TOOL_3)
			return loadTool3(input, checkCustomModel);
		else if (encoding == ItemEncoding.ENCODING_TOOL_4)
			return loadTool4(input, checkCustomModel);
		else if (encoding == ItemEncoding.ENCODING_TOOL_5)
			return loadTool5(input, checkCustomModel);
		else if (encoding == ItemEncoding.ENCODING_HOE_5)
			return loadHoe5(input, checkCustomModel);
		else if (encoding == ItemEncoding.ENCODING_SHEAR_5)
			return loadShear5(input, checkCustomModel);
		else if (encoding == ItemEncoding.ENCODING_BOW_3)
			return loadBow3(input, checkCustomModel);
		else if (encoding == ItemEncoding.ENCODING_BOW_4)
			return loadBow4(input, checkCustomModel);
		else if (encoding == ItemEncoding.ENCODING_BOW_5)
			return loadBow5(input, checkCustomModel);
		else if (encoding == ItemEncoding.ENCODING_ARMOR_4)
			return loadArmor4(input, checkCustomModel);
		else if (encoding == ItemEncoding.ENCODING_ARMOR_5)
			return loadArmor5(input, checkCustomModel);
		else if (encoding == ItemEncoding.ENCODING_ARMOR_6)
			return loadArmor6(input, checkCustomModel);
		throw new IllegalArgumentException("Unknown encoding: " + encoding);
	}
	
	private byte[] loadCustomModel(BitInput input, boolean check) {
		if (check && input.readBoolean()) {
			return input.readByteArray();
		} else {
			return null;
		}
	}

	private CustomItem loadSimpleItem1(BitInput input, boolean checkCustomModel) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		// System.out.println("itemType is " + itemType.name());
		// System.out.println("loadSimple item with damage " + damage + " and name " +
		// name + " and displayName " + displayName);
		String[] lore = new String[input.readByte() & 0xFF];
		// System.out.println("lore length is " + lore.length);
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[0];
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new SimpleCustomItem(itemType, damage, name, displayName, lore, attributes, 
				new Enchantment[0], 64, texture, ItemFlag.getDefaultValues(), customModel);
	}

	private CustomItem loadSimpleItem2(BitInput input, boolean checkCustomModel) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new SimpleCustomItem(itemType, damage, name, displayName, lore, attributes, 
				new Enchantment[0], 64, texture, ItemFlag.getDefaultValues(), customModel);
	}
	
	private CustomItem loadSimpleItem3(BitInput input, boolean checkCustomModel) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new SimpleCustomItem(itemType, damage, name, displayName, lore, attributes, 
				new Enchantment[0], 64, texture, ItemFlag.getDefaultValues(), customModel);
	}
	
	private CustomItem loadSimpleItem4(BitInput input, boolean checkCustomModel) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		byte maxStacksize = input.readByte();
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new SimpleCustomItem(itemType, damage, name, displayName, lore, attributes, 
				new Enchantment[0], maxStacksize, texture, ItemFlag.getDefaultValues(), customModel);
	}
	
	private CustomItem loadSimpleItem5(BitInput input, boolean checkCustomModel) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		byte maxStacksize = input.readByte();
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new SimpleCustomItem(itemType, damage, name, displayName, lore, attributes, new Enchantment[0],
				maxStacksize, texture, itemFlags, customModel);
	}

	private CustomItem loadTool2(BitInput input, boolean checkCustomModel) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
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
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomTool(itemType, damage, name, displayName, lore, attributes, new Enchantment[0], durability, allowEnchanting,
				allowAnvil, new NoIngredient(), texture, ItemFlag.getDefaultValues(),
				CustomToolDurability.defaultEntityHitDurabilityLoss(itemType), 
				CustomToolDurability.defaultBlockBreakDurabilityLoss(itemType), customModel);
	}

	private CustomItem loadTool3(BitInput input, boolean checkCustomModel) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
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
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomTool(itemType, damage, name, displayName, lore, attributes, new Enchantment[0], durability, allowEnchanting,
				allowAnvil, repairItem, texture, ItemFlag.getDefaultValues(), 
				CustomToolDurability.defaultEntityHitDurabilityLoss(itemType), 
				CustomToolDurability.defaultBlockBreakDurabilityLoss(itemType), customModel);
	}
	
	private CustomItem loadTool4(BitInput input, boolean checkCustomModel) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Recipe.loadIngredient(input, this);
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomTool(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, durability, allowEnchanting,
				allowAnvil, repairItem, texture, ItemFlag.getDefaultValues(),
				CustomToolDurability.defaultEntityHitDurabilityLoss(itemType), 
				CustomToolDurability.defaultBlockBreakDurabilityLoss(itemType), customModel);
	}
	
	private CustomItem loadTool5(BitInput input, boolean checkCustomModel) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Recipe.loadIngredient(input, this);
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomTool(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, 
				durability, allowEnchanting, allowAnvil, repairItem, texture, itemFlags, 
				entityHitDurabilityLoss, blockBreakDurabilityLoss, customModel);
	}
	
	private CustomItem loadHoe5(BitInput input, boolean checkCustomModel) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Recipe.loadIngredient(input, this);
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int tillDurabilityLoss = input.readInt();
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomHoe(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, 
				durability, allowEnchanting, allowAnvil, repairItem, texture, itemFlags, 
				entityHitDurabilityLoss, blockBreakDurabilityLoss, tillDurabilityLoss, customModel);
	}
	
	private CustomItem loadShear5(BitInput input, boolean checkCustomModel) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Recipe.loadIngredient(input, this);
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int shearDurabilityLoss = input.readInt();
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomShears(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, 
				durability, allowEnchanting, allowAnvil, repairItem, texture, itemFlags, 
				entityHitDurabilityLoss, blockBreakDurabilityLoss, shearDurabilityLoss, customModel);
	}

	private CustomBow loadBow3(BitInput input, boolean checkCustomModel) {
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		int durability = input.readInt();
		double damageMultiplier = input.readDouble();
		double speedMultiplier = input.readDouble();
		int knockbackStrength = input.readInt();
		boolean gravity = input.readBoolean();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Recipe.loadIngredient(input, this);
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomBow(damage, name, displayName, lore, attributes, new Enchantment[0], durability, 
				damageMultiplier, speedMultiplier, knockbackStrength, gravity, allowEnchanting, allowAnvil, 
				repairItem, (BowTextures) texture, ItemFlag.getDefaultValues(), 0, 0, 1, customModel);
	}
	
	private CustomBow loadBow4(BitInput input, boolean checkCustomModel) {
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		double damageMultiplier = input.readDouble();
		double speedMultiplier = input.readDouble();
		int knockbackStrength = input.readInt();
		boolean gravity = input.readBoolean();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Recipe.loadIngredient(input, this);
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomBow(damage, name, displayName, lore, attributes, defaultEnchantments, durability, 
				damageMultiplier, speedMultiplier, knockbackStrength, gravity, allowEnchanting, allowAnvil, 
				repairItem, (BowTextures) texture, ItemFlag.getDefaultValues(), 0, 0, 1, customModel);
	}
	
	private CustomBow loadBow5(BitInput input, boolean checkCustomModel) {
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		double damageMultiplier = input.readDouble();
		double speedMultiplier = input.readDouble();
		int knockbackStrength = input.readInt();
		boolean gravity = input.readBoolean();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Recipe.loadIngredient(input, this);
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int shootDurabilityLoss = input.readInt();
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomBow(damage, name, displayName, lore, attributes, defaultEnchantments, durability, 
				damageMultiplier, speedMultiplier, knockbackStrength, gravity, allowEnchanting, allowAnvil, 
				repairItem, (BowTextures) texture, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, shootDurabilityLoss, customModel);
	}
	
	private CustomItem loadArmor4(BitInput input, boolean checkCustomModel) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Recipe.loadIngredient(input, this);
		int red;
		int green;
		int blue;
		if (itemType == CustomItemType.LEATHER_BOOTS || itemType == CustomItemType.LEATHER_LEGGINGS
				|| itemType == CustomItemType.LEATHER_CHESTPLATE || itemType == CustomItemType.LEATHER_HELMET) {
			red = input.readByte() & 0xFF;
			green = input.readByte() & 0xFF;
			blue = input.readByte() & 0xFF;
		} else {
			red = 160;
			green = 101;
			blue = 64;
		}
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomArmor(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, durability, allowEnchanting,
				allowAnvil, repairItem, texture, red, green, blue, ItemFlag.getDefaultValues(), 0, 0,
				new DamageResistances(), customModel);
	}
	
	private CustomItem loadArmor5(BitInput input, boolean checkCustomModel) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Recipe.loadIngredient(input, this);
		int red;
		int green;
		int blue;
		if (itemType == CustomItemType.LEATHER_BOOTS || itemType == CustomItemType.LEATHER_LEGGINGS
				|| itemType == CustomItemType.LEATHER_CHESTPLATE || itemType == CustomItemType.LEATHER_HELMET) {
			red = input.readByte() & 0xFF;
			green = input.readByte() & 0xFF;
			blue = input.readByte() & 0xFF;
		} else {
			red = 160;
			green = 101;
			blue = 64;
		}
		
		// Don't use ItemFlag.values().length because it only had 6 flags during the version it was saved
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomArmor(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, durability, allowEnchanting,
				allowAnvil, repairItem, texture, red, green, blue, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, new DamageResistances(), customModel);
	}
	
	private CustomItem loadArmor6(BitInput input, boolean checkCustomModel) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Recipe.loadIngredient(input, this);
		int red;
		int green;
		int blue;
		if (itemType == CustomItemType.LEATHER_BOOTS || itemType == CustomItemType.LEATHER_LEGGINGS
				|| itemType == CustomItemType.LEATHER_CHESTPLATE || itemType == CustomItemType.LEATHER_HELMET) {
			red = input.readByte() & 0xFF;
			green = input.readByte() & 0xFF;
			blue = input.readByte() & 0xFF;
		} else {
			red = 160;
			green = 101;
			blue = 64;
		}
		
		// Don't use ItemFlag.values().length because it only had 6 flags during the version it was saved
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		
		DamageResistances resistances = new DamageResistances(input);
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomArmor(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, durability, allowEnchanting,
				allowAnvil, repairItem, texture, red, green, blue, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, resistances, customModel);
	}

	private AttributeModifier loadAttribute2(BitInput input) {
		return new AttributeModifier(Attribute.valueOf(input.readJavaString()), Slot.valueOf(input.readJavaString()),
				Operation.values()[(int) input.readNumber((byte) 2, false)], input.readDouble());
	}

	private boolean bypassChecks() {
		WindowInput input = Editor.getWindow().getInput();
		return input.isKeyDown(KeyCode.KEY_CONTROL) && input.isKeyDown(KeyCode.KEY_SHIFT);
	}

	private final String fileName;

	private Collection<NamedImage> textures;
	private Collection<CustomItem> items;
	private Collection<Recipe> recipes;
	private Collection<BlockDrop> blockDrops;
	private Collection<EntityDrop> mobDrops;

	public ItemSet(String fileName) {
		this.fileName = fileName;
		textures = new ArrayList<>();
		items = new ArrayList<>();
		recipes = new ArrayList<>();
		blockDrops = new ArrayList<>();
		mobDrops = new ArrayList<>();
	}

	public ItemSet(String fileName, BitInput input) {
		this.fileName = fileName;
		byte encoding = input.readByte();
		if (encoding == ENCODING_1)
			load1(input);
		else if (encoding == ENCODING_2)
			load2(input);
		else if (encoding == ENCODING_3)
			load3(input);
		else if (encoding == ENCODING_4)
			load4(input);
		else
			throw new IllegalArgumentException("Unknown encoding: " + encoding);
	}

	private String checkName(String name) {
		if (name.isEmpty())
			return "You can't leave the name empty.";
		for (int index = 0; index < name.length(); index++) {
			char c = name.charAt(index);
			if (c >= 'A' && c <= 'Z')
				return "Uppercase characters are not allowed in names.";
			if ((c < 'a' || c > 'z') && c != '_')
				return "The _ character is the only special character that is allowed in names.";
		}
		return null;
	}

	private void load1(BitInput input) {
		// Textures
		int textureAmount = input.readInt();
		// System.out.println("amount of textures is " + textureAmount);
		textures = new ArrayList<NamedImage>(textureAmount);
		for (int counter = 0; counter < textureAmount; counter++)
			textures.add(new NamedImage(input));
		// System.out.println("textures are " + textures);
		// Items
		int itemAmount = input.readInt();
		// System.out.println("amount of items is " + itemAmount);
		items = new ArrayList<CustomItem>(itemAmount);
		for (int counter = 0; counter < itemAmount; counter++)
			items.add(loadItem(input, false));

		// Recipes
		int recipeAmount = input.readInt();
		recipes = new ArrayList<Recipe>(recipeAmount);
		for (int counter = 0; counter < recipeAmount; counter++)
			recipes.add(loadRecipe(input));
		
		// Drops (there are no drops in this encoding)
		blockDrops = new ArrayList<>();
		mobDrops = new ArrayList<>();
	}

	private void load2(BitInput input) {
		// Textures
		int textureAmount = input.readInt();
		// System.out.println("amount of textures is " + textureAmount);
		textures = new ArrayList<NamedImage>(textureAmount);
		for (int counter = 0; counter < textureAmount; counter++) {
			byte textureType = input.readByte();
			if (textureType == NamedImage.ENCODING_BOW)
				textures.add(new BowTextures(input));
			else if (textureType == NamedImage.ENCODING_SIMPLE)
				textures.add(new NamedImage(input));
			else
				throw new IllegalArgumentException("Unknown texture encoding: " + textureType);
		}
		// System.out.println("textures are " + textures);
		// Items
		int itemAmount = input.readInt();
		// System.out.println("amount of items is " + itemAmount);
		items = new ArrayList<CustomItem>(itemAmount);
		for (int counter = 0; counter < itemAmount; counter++)
			items.add(loadItem(input, false));

		// Recipes
		int recipeAmount = input.readInt();
		recipes = new ArrayList<Recipe>(recipeAmount);
		for (int counter = 0; counter < recipeAmount; counter++)
			recipes.add(loadRecipe(input));
		
		// Drops (there are no drops in this encoding)
		blockDrops = new ArrayList<>();
		mobDrops = new ArrayList<>();
	}
	
	private void load3(BitInput input) {
		// Textures
		int textureAmount = input.readInt();
		// System.out.println("amount of textures is " + textureAmount);
		textures = new ArrayList<NamedImage>(textureAmount);
		for (int counter = 0; counter < textureAmount; counter++) {
			byte textureType = input.readByte();
			if (textureType == NamedImage.ENCODING_BOW)
				textures.add(new BowTextures(input));
			else if (textureType == NamedImage.ENCODING_SIMPLE)
				textures.add(new NamedImage(input));
			else
				throw new IllegalArgumentException("Unknown texture encoding: " + textureType);
		}
		// System.out.println("textures are " + textures);
		// Items
		int itemAmount = input.readInt();
		// System.out.println("amount of items is " + itemAmount);
		items = new ArrayList<CustomItem>(itemAmount);
		for (int counter = 0; counter < itemAmount; counter++)
			items.add(loadItem(input, false));

		// Recipes
		int recipeAmount = input.readInt();
		recipes = new ArrayList<Recipe>(recipeAmount);
		for (int counter = 0; counter < recipeAmount; counter++)
			recipes.add(loadRecipe(input));
		
		// Drops
		int numBlockDrops = input.readInt();
		blockDrops = new ArrayList<>(numBlockDrops);
		for (int counter = 0; counter < numBlockDrops; counter++)
			blockDrops.add(BlockDrop.load(input, this));
		
		int numMobDrops = input.readInt();
		mobDrops = new ArrayList<>(numMobDrops);
		for (int counter = 0; counter < numMobDrops; counter++)
			mobDrops.add(EntityDrop.load(input, this));
	}
	
	private void load4(BitInput input) {
		// Textures
		int textureAmount = input.readInt();
		// System.out.println("amount of textures is " + textureAmount);
		textures = new ArrayList<NamedImage>(textureAmount);
		for (int counter = 0; counter < textureAmount; counter++) {
			byte textureType = input.readByte();
			if (textureType == NamedImage.ENCODING_BOW)
				textures.add(new BowTextures(input));
			else if (textureType == NamedImage.ENCODING_SIMPLE)
				textures.add(new NamedImage(input));
			else
				throw new IllegalArgumentException("Unknown texture encoding: " + textureType);
		}
		// System.out.println("textures are " + textures);
		// Items
		int itemAmount = input.readInt();
		// System.out.println("amount of items is " + itemAmount);
		items = new ArrayList<CustomItem>(itemAmount);
		for (int counter = 0; counter < itemAmount; counter++)
			items.add(loadItem(input, true));

		// Recipes
		int recipeAmount = input.readInt();
		recipes = new ArrayList<Recipe>(recipeAmount);
		for (int counter = 0; counter < recipeAmount; counter++)
			recipes.add(loadRecipe(input));
		
		// Drops
		int numBlockDrops = input.readInt();
		blockDrops = new ArrayList<>(numBlockDrops);
		for (int counter = 0; counter < numBlockDrops; counter++)
			blockDrops.add(BlockDrop.load(input, this));
		
		int numMobDrops = input.readInt();
		mobDrops = new ArrayList<>(numMobDrops);
		for (int counter = 0; counter < numMobDrops; counter++)
			mobDrops.add(EntityDrop.load(input, this));
	}

	/**
	 * A String containing only the quote character. I use this constant because
	 * it's annoying to get that character inside a String
	 */
	private static final String Q = "" + '"';
	
	public String[] getDefaultModel(CustomItem item) {
		if (item instanceof CustomBow) {
			return new String[] {
			"{",
			"    \"parent\": \"item/bow\",",
			"    \"textures\": {",
			"        \"layer0\": \"customitems/" + item.getTexture().getName() + "_standby\"",
			"    }",
			"}"
			};
		} else {
			CustomItemType i = item.getItemType();
			boolean leather = i == CustomItemType.LEATHER_BOOTS || i == CustomItemType.LEATHER_LEGGINGS
					|| i == CustomItemType.LEATHER_CHESTPLATE || i == CustomItemType.LEATHER_HELMET;
			String[] start = {
			"{",
			"    \"parent\": \"item/handheld\",",
			"    \"textures\": {",
			"        \"layer0\": \"customitems/" + item.getTexture().getName() + Q + (leather ? "," : "")
			};
			
			String[] mid;
			if (leather) {
				mid = new String[] {"        \"layer1\": \"customitems/" + item.getTexture().getName() + Q};
			} else {
				mid = new String[0];
			}
			
			String[] end = {
			"    }",
			"}"
			};
			
			return chain(start, mid, end);
		}
	}
	
	private String[] chain(String[]...arrays) {
		int length = 0;
		for (String[] array : arrays) {
			length += array.length;
		}
		String[] result = new String[length];
		int index = 0;
		for (String[] array : arrays) {
			System.arraycopy(array, 0, result, index, array.length);
			index += array.length;
		}
		return result;
	}
	
	/**
	 * Experimental feature to export the resourcepack for minecraft 1.14 instead of minecraft 1.12
	 * @return
	 */
	public String export1_14() {
		try {
			File file = new File(Editor.getFolder() + "/" + fileName + ".cis");// cis stands for Custom Item Set
			OutputStream fileOutput = Files.newOutputStream(file.toPath());
			ByteArrayBitOutput output = new ByteArrayBitOutput();
			export3(output);
			output.terminate();
			fileOutput.write(output.getBytes());
			fileOutput.flush();
			fileOutput.close();
			ZipOutputStream zipOutput = new ZipOutputStream(
					new FileOutputStream(new File(Editor.getFolder() + "/" + fileName + ".zip")));

			// Custom textures
			for (NamedImage texture : textures) {
				String textureName = texture.getName();
				if (texture instanceof BowTextures) {
					textureName += "_standby";
					BowTextures bt = (BowTextures) texture;
					List<BowTextures.Entry> pullTextures = bt.getPullTextures();
					int index = 0;
					for (BowTextures.Entry pullTexture : pullTextures) {
						ZipEntry entry = new ZipEntry("assets/minecraft/textures/customitems/" + bt.getName()
								+ "_pulling_" + index++ + ".png");
						zipOutput.putNextEntry(entry);
						ImageIO.write(pullTexture.getTexture(), "PNG", new MemoryCacheImageOutputStream(zipOutput));
						zipOutput.closeEntry();
					}
				}
				ZipEntry entry = new ZipEntry("assets/minecraft/textures/customitems/" + textureName + ".png");
				zipOutput.putNextEntry(entry);
				ImageIO.write(texture.getImage(), "PNG", new MemoryCacheImageOutputStream(zipOutput));
				zipOutput.closeEntry();
			}

			// Custom item models
			for (CustomItem item : items) {
				ZipEntry entry = new ZipEntry("assets/minecraft/models/customitems/" + item.getName() + ".json");
				zipOutput.putNextEntry(entry);
				PrintWriter jsonWriter = new PrintWriter(zipOutput);
				/*
				if (item instanceof CustomBow) {
					jsonWriter.println("{");
					jsonWriter.println("    " + Q + "parent" + Q + ": " + Q + "item/bow" + Q + ",");
					jsonWriter.println("    " + Q + "textures" + Q + ": {");
					jsonWriter.println("        " + Q + "layer0" + Q + ": " + Q + "customitems/"
							+ item.getTexture().getName() + "_standby" + Q);
					jsonWriter.println("    }");
					jsonWriter.println("}");
				} else {
					jsonWriter.println("{");
					jsonWriter.println("    " + Q + "parent" + Q + ": " + Q + "item/handheld" + Q + ",");
					jsonWriter.println("    " + Q + "textures" + Q + ": {");
					CustomItemType i = item.getItemType();
					boolean leather = i == CustomItemType.LEATHER_BOOTS || i == CustomItemType.LEATHER_LEGGINGS
							|| i == CustomItemType.LEATHER_CHESTPLATE || i == CustomItemType.LEATHER_HELMET;
					jsonWriter.println("        " + Q + "layer0" + Q + ": " + Q + "customitems/"
							+ item.getTexture().getName() + Q + (leather ? "," : ""));
					if (leather) {
						jsonWriter.println("        " + Q + "layer1" + Q + ": " + Q + "customitems/"
								+ item.getTexture().getName() + Q);
					}
					jsonWriter.println("    }");
					jsonWriter.println("}");
				}
				*/
				byte[] customModel = item.getCustomModel();
				if (customModel != null) {
					zipOutput.write(customModel);
					zipOutput.flush();
				} else {
					String[] modelContent = getDefaultModel(item);
					for (String line : modelContent) {
						jsonWriter.println(line);
					}
					jsonWriter.flush();
				}
				zipOutput.closeEntry();
				if (item instanceof CustomBow) {
					CustomBow bow = (CustomBow) item;
					List<BowTextures.Entry> pullTextures = bow.getTexture().getPullTextures();
					String textureName = item.getTexture().getName() + "_pulling_";
					for (int index = 0; index < pullTextures.size(); index++) {
						entry = new ZipEntry("assets/minecraft/models/customitems/" + item.getName() + "_pulling_"
								+ index + ".json");
						zipOutput.putNextEntry(entry);
						jsonWriter = new PrintWriter(zipOutput);
						jsonWriter.println("{");
						jsonWriter.println("    " + Q + "parent" + Q + ": " + Q + "item/bow" + Q + ",");
						jsonWriter.println("    " + Q + "textures" + Q + ": {");
						jsonWriter.println(
								"        " + Q + "layer0" + Q + ": " + Q + "customitems/" + textureName + index + Q);
						jsonWriter.println("    }");
						jsonWriter.println("}");
						jsonWriter.flush();
						zipOutput.closeEntry();
					}
				}
			}

			// Map all custom items by their item type
			Map<CustomItemType, List<CustomItem>> itemMap = new EnumMap<CustomItemType, List<CustomItem>>(
					CustomItemType.class);
			for (CustomItem item : items) {
				List<CustomItem> list = itemMap.get(item.getItemType());
				if (list == null) {
					list = new ArrayList<CustomItem>();
					itemMap.put(item.getItemType(), list);
				}
				list.add(item);
			}

			// Now create the item model files for those models
			Set<Entry<CustomItemType, List<CustomItem>>> entrySet = itemMap.entrySet();
			for (Entry<CustomItemType, List<CustomItem>> entry : entrySet) {
				List<CustomItem> list = entry.getValue();
				if (list != null) {
					// The items with low damage should come first
					list.sort((CustomItem a, CustomItem b) -> {
						if (a.getItemDamage() > b.getItemDamage())
							return 1;
						if (a.getItemDamage() < b.getItemDamage())
							return -1;
						if (a == b)
							return 0;
						throw new IllegalArgumentException("a is " + a + " and b is " + b);
					});
					String modelName = entry.getKey().getModelName14();
					String textureName = entry.getKey().getTextureName14();
					
					ZipEntry zipEntry = new ZipEntry("assets/minecraft/models/item/" + modelName + ".json");
					zipOutput.putNextEntry(zipEntry);
					PrintWriter jsonWriter = new PrintWriter(zipOutput);

					if (entry.getKey() == CustomItemType.BOW) {
						// Begin of the json file
						jsonWriter.println("{");
						jsonWriter.println("    " + Q + "parent" + Q + ": " + Q + "item/generated" + Q + ",");
						jsonWriter.println("    " + Q + "textures" + Q + ": {");
						jsonWriter.println("        " + Q + "layer0" + Q + ": " + Q + "item/bow" + Q);
						jsonWriter.println("    },");
						// Display
						jsonWriter.println("    " + Q + "display" + Q + ": {");
						jsonWriter.println("        " + Q + "thirdperson_righthand" + Q + ": {");
						jsonWriter.println("            " + Q + "rotation" + Q + ": [ -80, 260, -40 ],");
						jsonWriter.println("            " + Q + "translation" + Q + ": [ -1, -2, 2.5 ],");
						jsonWriter.println("            " + Q + "scale" + Q + ": [ 0.9, 0.9, 0.9 ]");
						jsonWriter.println("        },");
						jsonWriter.println("        " + Q + "thirdperson_lefthand" + Q + ": {");
						jsonWriter.println("            " + Q + "rotation" + Q + ": [ -80, -280, 40 ],");
						jsonWriter.println("            " + Q + "translation" + Q + ": [ -1, -2, 2.5 ],");
						jsonWriter.println("            " + Q + "scale" + Q + ": [ 0.9, 0.9, 0.9 ]");
						jsonWriter.println("        },");
						jsonWriter.println("        " + Q + "firstperson_righthand" + Q + ": {");
						jsonWriter.println("            " + Q + "rotation" + Q + ": [ 0, -90, 25 ],");
						jsonWriter.println("            " + Q + "translation" + Q + ": [ 1.13, 3.2, 1.13 ],");
						jsonWriter.println("            " + Q + "scale" + Q + ": [ 0.68, 0.68, 0.68 ]");
						jsonWriter.println("        },");
						jsonWriter.println("        " + Q + "firstperson_lefthand" + Q + ": {");
						jsonWriter.println("            " + Q + "rotation" + Q + ": [ 0, 90, -25 ],");
						jsonWriter.println("            " + Q + "translation" + Q + ": [ 1.13, 3.2, 1.13 ],");
						jsonWriter.println("            " + Q + "scale" + Q + ": [ 0.68, 0.68, 0.68 ]");
						jsonWriter.println("        }");
						jsonWriter.println("    },");
						// The interesting part...
						jsonWriter.println("    " + Q + "overrides" + Q + ": [");

						jsonWriter.println("        { " + Q + "predicate" + Q + ": { " + Q + "pulling" + Q + ": 1 }, "
								+ Q + "model" + Q + ": " + Q + "item/bow_pulling_0" + Q + "},");
						jsonWriter.println("        { " + Q + "predicate" + Q + ": { " + Q + "pulling" + Q + ": 1, " + Q
								+ "pull" + Q + ": 0.65 }, " + Q + "model" + Q + ": " + Q + "item/bow_pulling_1" + Q
								+ "},");
						jsonWriter.println("        { " + Q + "predicate" + Q + ": { " + Q + "pulling" + Q + ": 1, " + Q
								+ "pull" + Q + ": 0.9 }, " + Q + "model" + Q + ": " + Q + "item/bow_pulling_2" + Q
								+ "},");

						for (CustomItem item : list) {
							jsonWriter.println("        { " + Q + "predicate" + Q + ": {" + Q + "damaged" + Q + ": 0, "
									+ Q + "damage" + Q + ": "
									+ (double) item.getItemDamage() / item.getItemType().getMaxDurability() + "}, " + Q
									+ "model" + Q + ": " + Q + "customitems/" + item.getName() + Q + "},");
							List<BowTextures.Entry> pullTextures = ((CustomBow) item).getTexture().getPullTextures();
							int counter = 0;
							for (BowTextures.Entry pullTexture : pullTextures) {
								jsonWriter.println("        { " + Q + "predicate" + Q + ": {" + Q + "damaged" + Q
										+ ": 0, " + Q + "damage" + Q + ": "
										+ (double) item.getItemDamage() / item.getItemType().getMaxDurability() + ", "
										+ Q + "pulling" + Q + ": 1, " + Q + "pull" + Q + ": " + pullTexture.getPull()
										+ "}, " + Q + "model" + Q + ": " + Q + "customitems/" + item.getName()
										+ "_pulling_" + counter++ + Q + "},");
							}
						}
						// End of the json file
						jsonWriter.println("        { " + Q + "predicate" + Q + ": {" + Q + "damaged" + Q + ": 1, " + Q
								+ "damage" + Q + ": 0}, " + Q + "model" + Q + ": " + Q + "item/" + modelName + Q + "}");
						jsonWriter.println("    ]");
						jsonWriter.println("}");
					} else {
						// Begin of the json file
						jsonWriter.println("{");
						jsonWriter.println("    " + Q + "parent" + Q + ": " + Q + "item/handheld" + Q + ",");
						jsonWriter.println("    " + Q + "textures" + Q + ": {");
						jsonWriter.print("        " + Q + "layer0" + Q + ": " + Q + "item/" + textureName + Q);
						boolean isLeatherArmor = entry.getKey() == CustomItemType.LEATHER_BOOTS ||
								entry.getKey() == CustomItemType.LEATHER_LEGGINGS || entry.getKey() == CustomItemType.LEATHER_CHESTPLATE
								|| entry.getKey() == CustomItemType.LEATHER_HELMET;
						if (isLeatherArmor) {
							jsonWriter.print(",");
						}
						jsonWriter.println();
						if (isLeatherArmor) {
							jsonWriter.print("        " + Q + "layer1" + Q + ": " + Q + "item/" + textureName + "_overlay" + Q);
						}
						jsonWriter.println("    },");
						jsonWriter.println("    " + Q + "overrides" + Q + ": [");

						// Now the interesting part
						for (CustomItem item : list) {
							jsonWriter.println("        { " + Q + "predicate" + Q + ": {" + Q + "damaged" + Q + ": 0, "
									+ Q + "damage" + Q + ": "
									+ (double) item.getItemDamage() / item.getItemType().getMaxDurability() + "}, " + Q
									+ "model" + Q + ": " + Q + "customitems/" + item.getName() + Q + "},");
						}

						// End of the json file
						jsonWriter.println("        { " + Q + "predicate" + Q + ": {" + Q + "damaged" + Q + ": 1, " + Q
								+ "damage" + Q + ": 0}, " + Q + "model" + Q + ": " + Q + "item/" + modelName + Q + "}");
						jsonWriter.println("    ]");
						jsonWriter.println("}");
					}
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
			jsonWriter.println("        " + Q + "pack_format" + Q + ": 4,");
			jsonWriter.println("        " + Q + "description" + Q + ": " + Q + "CustomItemSet" + Q);
			jsonWriter.println("    }");
			jsonWriter.println("}");
			jsonWriter.flush();
			zipOutput.closeEntry();

			zipOutput.close();
			return null;
		} catch (IOException ioex) {
			ioex.printStackTrace();
			return ioex.getMessage();
		}
	}

	public String export() {
		try {
			File file = new File(Editor.getFolder() + "/" + fileName + ".cis");// cis stands for Custom Item Set
			OutputStream fileOutput = Files.newOutputStream(file.toPath());
			ByteArrayBitOutput output = new ByteArrayBitOutput();
			export3(output);
			output.terminate();
			fileOutput.write(output.getBytes());
			fileOutput.flush();
			fileOutput.close();
			ZipOutputStream zipOutput = new ZipOutputStream(
					new FileOutputStream(new File(Editor.getFolder() + "/" + fileName + ".zip")));

			// Custom textures
			for (NamedImage texture : textures) {
				String textureName = texture.getName();
				if (texture instanceof BowTextures) {
					textureName += "_standby";
					BowTextures bt = (BowTextures) texture;
					List<BowTextures.Entry> pullTextures = bt.getPullTextures();
					int index = 0;
					for (BowTextures.Entry pullTexture : pullTextures) {
						ZipEntry entry = new ZipEntry("assets/minecraft/textures/customitems/" + bt.getName()
								+ "_pulling_" + index++ + ".png");
						zipOutput.putNextEntry(entry);
						ImageIO.write(pullTexture.getTexture(), "PNG", new MemoryCacheImageOutputStream(zipOutput));
						zipOutput.closeEntry();
					}
				}
				ZipEntry entry = new ZipEntry("assets/minecraft/textures/customitems/" + textureName + ".png");
				zipOutput.putNextEntry(entry);
				ImageIO.write(texture.getImage(), "PNG", new MemoryCacheImageOutputStream(zipOutput));
				zipOutput.closeEntry();
			}

			// Custom item models
			for (CustomItem item : items) {
				ZipEntry entry = new ZipEntry("assets/minecraft/models/customitems/" + item.getName() + ".json");
				zipOutput.putNextEntry(entry);
				PrintWriter jsonWriter = new PrintWriter(zipOutput);
				/*
				if (item instanceof CustomBow) {
					jsonWriter.println("{");
					jsonWriter.println("    " + Q + "parent" + Q + ": " + Q + "item/bow" + Q + ",");
					jsonWriter.println("    " + Q + "textures" + Q + ": {");
					jsonWriter.println("        " + Q + "layer0" + Q + ": " + Q + "customitems/"
							+ item.getTexture().getName() + "_standby" + Q);
					jsonWriter.println("    }");
					jsonWriter.println("}");
				} else {
					jsonWriter.println("{");
					jsonWriter.println("    " + Q + "parent" + Q + ": " + Q + "item/handheld" + Q + ",");
					jsonWriter.println("    " + Q + "textures" + Q + ": {");
					CustomItemType i = item.getItemType();
					boolean leather = i == CustomItemType.LEATHER_BOOTS || i == CustomItemType.LEATHER_LEGGINGS
							|| i == CustomItemType.LEATHER_CHESTPLATE || i == CustomItemType.LEATHER_HELMET;
					jsonWriter.println("        " + Q + "layer0" + Q + ": " + Q + "customitems/"
							+ item.getTexture().getName() + Q + (leather ? "," : ""));
					if (leather) {
						jsonWriter.println("        " + Q + "layer1" + Q + ": " + Q + "customitems/"
								+ item.getTexture().getName() + Q);
					}
					jsonWriter.println("    }");
					jsonWriter.println("}");
				}
				*/
				byte[] customModel = item.getCustomModel();
				if (customModel != null) {
					zipOutput.write(customModel);
					zipOutput.flush();
				} else {
					String[] modelContent = getDefaultModel(item);
					for (String line : modelContent) {
						jsonWriter.println(line);
					}
					jsonWriter.flush();
				}
				zipOutput.closeEntry();
				if (item instanceof CustomBow) {
					CustomBow bow = (CustomBow) item;
					List<BowTextures.Entry> pullTextures = bow.getTexture().getPullTextures();
					String textureName = item.getTexture().getName() + "_pulling_";
					for (int index = 0; index < pullTextures.size(); index++) {
						entry = new ZipEntry("assets/minecraft/models/customitems/" + item.getName() + "_pulling_"
								+ index + ".json");
						zipOutput.putNextEntry(entry);
						jsonWriter = new PrintWriter(zipOutput);
						jsonWriter.println("{");
						jsonWriter.println("    " + Q + "parent" + Q + ": " + Q + "item/bow" + Q + ",");
						jsonWriter.println("    " + Q + "textures" + Q + ": {");
						jsonWriter.println(
								"        " + Q + "layer0" + Q + ": " + Q + "customitems/" + textureName + index + Q);
						jsonWriter.println("    }");
						jsonWriter.println("}");
						jsonWriter.flush();
						zipOutput.closeEntry();
					}
				}
			}

			// Map all custom items by their item type
			Map<CustomItemType, List<CustomItem>> itemMap = new EnumMap<CustomItemType, List<CustomItem>>(
					CustomItemType.class);
			for (CustomItem item : items) {
				List<CustomItem> list = itemMap.get(item.getItemType());
				if (list == null) {
					list = new ArrayList<CustomItem>();
					itemMap.put(item.getItemType(), list);
				}
				list.add(item);
			}

			// Now create the item model files for those models
			Set<Entry<CustomItemType, List<CustomItem>>> entrySet = itemMap.entrySet();
			for (Entry<CustomItemType, List<CustomItem>> entry : entrySet) {
				List<CustomItem> list = entry.getValue();
				if (list != null) {
					// The items with low damage should come first
					list.sort((CustomItem a, CustomItem b) -> {
						if (a.getItemDamage() > b.getItemDamage())
							return 1;
						if (a.getItemDamage() < b.getItemDamage())
							return -1;
						if (a == b)
							return 0;
						throw new IllegalArgumentException("a is " + a + " and b is " + b);
					});
					String modelName = entry.getKey().getModelName12();
					String textureName = entry.getKey().getTextureName12();
					ZipEntry zipEntry = new ZipEntry("assets/minecraft/models/item/" + modelName + ".json");
					zipOutput.putNextEntry(zipEntry);
					PrintWriter jsonWriter = new PrintWriter(zipOutput);

					if (entry.getKey() == CustomItemType.BOW) {
						// Begin of the json file
						jsonWriter.println("{");
						jsonWriter.println("    " + Q + "parent" + Q + ": " + Q + "item/generated" + Q + ",");
						jsonWriter.println("    " + Q + "textures" + Q + ": {");
						jsonWriter.println("        " + Q + "layer0" + Q + ": " + Q + "items/bow_standby" + Q);
						jsonWriter.println("    },");
						// Display
						jsonWriter.println("    " + Q + "display" + Q + ": {");
						jsonWriter.println("        " + Q + "thirdperson_righthand" + Q + ": {");
						jsonWriter.println("            " + Q + "rotation" + Q + ": [ -80, 260, -40 ],");
						jsonWriter.println("            " + Q + "translation" + Q + ": [ -1, -2, 2.5 ],");
						jsonWriter.println("            " + Q + "scale" + Q + ": [ 0.9, 0.9, 0.9 ]");
						jsonWriter.println("        },");
						jsonWriter.println("        " + Q + "thirdperson_lefthand" + Q + ": {");
						jsonWriter.println("            " + Q + "rotation" + Q + ": [ -80, -280, -40 ],");
						jsonWriter.println("            " + Q + "translation" + Q + ": [ -1, -2, 2.5 ],");
						jsonWriter.println("            " + Q + "scale" + Q + ": [ 0.9, 0.9, 0.9 ]");
						jsonWriter.println("        },");
						jsonWriter.println("        " + Q + "firstperson_righthand" + Q + ": {");
						jsonWriter.println("            " + Q + "rotation" + Q + ": [ 0, -90, 25 ],");
						jsonWriter.println("            " + Q + "translation" + Q + ": [ 1.13, 3.2, 1.13 ],");
						jsonWriter.println("            " + Q + "scale" + Q + ": [ 0.68, 0.68, 0.68 ]");
						jsonWriter.println("        },");
						jsonWriter.println("        " + Q + "firstperson_lefthand" + Q + ": {");
						jsonWriter.println("            " + Q + "rotation" + Q + ": [ 0, 90, -25 ],");
						jsonWriter.println("            " + Q + "translation" + Q + ": [ 1.13, 3.2, 1.13 ],");
						jsonWriter.println("            " + Q + "scale" + Q + ": [ 0.68, 0.68, 0.68 ]");
						jsonWriter.println("        }");
						jsonWriter.println("    },");
						// The interesting part...
						jsonWriter.println("    " + Q + "overrides" + Q + ": [");

						jsonWriter.println("        { " + Q + "predicate" + Q + ": { " + Q + "pulling" + Q + ": 1 }, "
								+ Q + "model" + Q + ": " + Q + "item/bow_pulling_0" + Q + "},");
						jsonWriter.println("        { " + Q + "predicate" + Q + ": { " + Q + "pulling" + Q + ": 1, " + Q
								+ "pull" + Q + ": 0.65 }, " + Q + "model" + Q + ": " + Q + "item/bow_pulling_1" + Q
								+ "},");
						jsonWriter.println("        { " + Q + "predicate" + Q + ": { " + Q + "pulling" + Q + ": 1, " + Q
								+ "pull" + Q + ": 0.9 }, " + Q + "model" + Q + ": " + Q + "item/bow_pulling_2" + Q
								+ "},");

						for (CustomItem item : list) {
							jsonWriter.println("        { " + Q + "predicate" + Q + ": {" + Q + "damaged" + Q + ": 0, "
									+ Q + "damage" + Q + ": "
									+ (double) item.getItemDamage() / item.getItemType().getMaxDurability() + "}, " + Q
									+ "model" + Q + ": " + Q + "customitems/" + item.getName() + Q + "},");
							List<BowTextures.Entry> pullTextures = ((CustomBow) item).getTexture().getPullTextures();
							int counter = 0;
							for (BowTextures.Entry pullTexture : pullTextures) {
								jsonWriter.println("        { " + Q + "predicate" + Q + ": {" + Q + "damaged" + Q
										+ ": 0, " + Q + "damage" + Q + ": "
										+ (double) item.getItemDamage() / item.getItemType().getMaxDurability() + ", "
										+ Q + "pulling" + Q + ": 1, " + Q + "pull" + Q + ": " + pullTexture.getPull()
										+ "}, " + Q + "model" + Q + ": " + Q + "customitems/" + item.getName()
										+ "_pulling_" + counter++ + Q + "},");
							}
						}
						// End of the json file
						jsonWriter.println("        { " + Q + "predicate" + Q + ": {" + Q + "damaged" + Q + ": 1, " + Q
								+ "damage" + Q + ": 0}, " + Q + "model" + Q + ": " + Q + "item/" + modelName + Q + "}");
						jsonWriter.println("    ]");
						jsonWriter.println("}");
					} else {
						// Begin of the json file
						jsonWriter.println("{");
						jsonWriter.println("    " + Q + "parent" + Q + ": " + Q + "item/handheld" + Q + ",");
						jsonWriter.println("    " + Q + "textures" + Q + ": {");
						jsonWriter.print("        " + Q + "layer0" + Q + ": " + Q + "items/" + textureName + Q);
						boolean isLeatherArmor = entry.getKey() == CustomItemType.LEATHER_BOOTS ||
								entry.getKey() == CustomItemType.LEATHER_LEGGINGS || entry.getKey() == CustomItemType.LEATHER_CHESTPLATE
								|| entry.getKey() == CustomItemType.LEATHER_HELMET;
						if (isLeatherArmor) {
							jsonWriter.print(",");
						}
						jsonWriter.println();
						if (isLeatherArmor) {
							jsonWriter.print("        " + Q + "layer1" + Q + ": " + Q + "items/" + textureName + "_overlay" + Q);
						}
						jsonWriter.println("    },");
						jsonWriter.println("    " + Q + "overrides" + Q + ": [");

						// Now the interesting part
						for (CustomItem item : list) {
							jsonWriter.println("        { " + Q + "predicate" + Q + ": {" + Q + "damaged" + Q + ": 0, "
									+ Q + "damage" + Q + ": "
									+ (double) item.getItemDamage() / item.getItemType().getMaxDurability() + "}, " + Q
									+ "model" + Q + ": " + Q + "customitems/" + item.getName() + Q + "},");
						}

						// End of the json file
						jsonWriter.println("        { " + Q + "predicate" + Q + ": {" + Q + "damaged" + Q + ": 1, " + Q
								+ "damage" + Q + ": 0}, " + Q + "model" + Q + ": " + Q + "item/" + modelName + Q + "}");
						jsonWriter.println("    ]");
						jsonWriter.println("}");
					}
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
		} catch (IOException ioex) {
			ioex.printStackTrace();
			return ioex.getMessage();
		}
	}

	@SuppressWarnings("unused")
	private void export1(BitOutput output) {
		output.addByte(ENCODING_1);

		// Items
		output.addInt(items.size());
		for (CustomItem item : items)
			item.export(output);

		// Recipes
		output.addInt(recipes.size());
		for (Recipe recipe : recipes)
			recipe.save(output);
		
		// There are no drops in this encoding
	}
	
	// I wonder why there is no export2
	
	private void export3(BitOutput output) {
		output.addByte(ENCODING_3);

		// Items
		output.addInt(items.size());
		for (CustomItem item : items)
			item.export(output);

		// Recipes
		output.addInt(recipes.size());
		for (Recipe recipe : recipes)
			recipe.save(output);
		
		// Drops
		output.addInt(blockDrops.size());
		for (BlockDrop drop : blockDrops)
			drop.save(output);
		
		output.addInt(mobDrops.size());
		for (EntityDrop drop : mobDrops)
			drop.save(output);
	}

	public String save() {
		try {
			Editor.getFolder().mkdir();
			Editor.getBackupFolder().mkdir();
			File file = new File(Editor.getFolder() + "/" + fileName + ".cisb");// cisb stands for Custom Item Set
																				// Builder
			ByteArrayBitOutput output = new ByteArrayBitOutput();
			save4(output);
			output.terminate();
			byte[] bytes = output.getBytes();
			OutputStream mainOutput = Files.newOutputStream(file.toPath());
			mainOutput.write(bytes);
			mainOutput.flush();
			mainOutput.close();
			OutputStream backupOutput = Files.newOutputStream(
					new File(Editor.getBackupFolder() + "/" + fileName + " " + System.currentTimeMillis() + ".cisb").toPath());
			backupOutput.write(bytes);
			mainOutput.flush();
			backupOutput.close();
			return null;
		} catch (IOException ioex) {
			ioex.printStackTrace();
			return ioex.getMessage();
		}
	}

	@SuppressWarnings("unused")
	private void save1(BitOutput output) {
		output.addByte(ENCODING_1);
		output.addInt(textures.size());
		for (NamedImage texture : textures)
			texture.save(output);
		output.addInt(items.size());

		// Save the normal items before the tools so that tools can use normal items as
		// repair item
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
		for (CustomItem item : sorted)
			item.save1(output);

		output.addInt(recipes.size());
		for (Recipe recipe : recipes)
			recipe.save(output);
	}

	@SuppressWarnings("unused")
	private void save2(BitOutput output) {
		output.addByte(ENCODING_2);
		output.addInt(textures.size());
		for (NamedImage texture : textures) {
			if (texture instanceof BowTextures)
				output.addByte(NamedImage.ENCODING_BOW);
			else
				output.addByte(NamedImage.ENCODING_SIMPLE);
			texture.save(output);
		}
		output.addInt(items.size());

		// Save the normal items before the tools so that tools can use normal items as
		// repair item
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
		for (CustomItem item : sorted)
			item.save1(output);

		output.addInt(recipes.size());
		for (Recipe recipe : recipes)
			recipe.save(output);
	}
	
	@SuppressWarnings("unused")
	private void save3(BitOutput output) {
		output.addByte(ENCODING_3);
		output.addInt(textures.size());
		for (NamedImage texture : textures) {
			if (texture instanceof BowTextures)
				output.addByte(NamedImage.ENCODING_BOW);
			else
				output.addByte(NamedImage.ENCODING_SIMPLE);
			texture.save(output);
		}
		output.addInt(items.size());

		// Save the normal items before the tools so that tools can use normal items as
		// repair item
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
		for (CustomItem item : sorted)
			item.save1(output);

		output.addInt(recipes.size());
		for (Recipe recipe : recipes)
			recipe.save(output);
		
		output.addInt(blockDrops.size());
		for (BlockDrop drop : blockDrops)
			drop.save(output);
		
		output.addInt(mobDrops.size());
		for (EntityDrop drop : mobDrops)
			drop.save(output);
	}
	
	// Use CustomItem.save2 instead of CustomItem.save1
	private void save4(BitOutput output) {
		output.addByte(ENCODING_4);
		output.addInt(textures.size());
		for (NamedImage texture : textures) {
			if (texture instanceof BowTextures)
				output.addByte(NamedImage.ENCODING_BOW);
			else
				output.addByte(NamedImage.ENCODING_SIMPLE);
			texture.save(output);
		}
		output.addInt(items.size());

		// Save the normal items before the tools so that tools can use normal items as
		// repair item
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
		for (CustomItem item : sorted)
			item.save2(output);

		output.addInt(recipes.size());
		for (Recipe recipe : recipes)
			recipe.save(output);
		
		output.addInt(blockDrops.size());
		for (BlockDrop drop : blockDrops)
			drop.save(output);
		
		output.addInt(mobDrops.size());
		for (EntityDrop drop : mobDrops)
			drop.save(output);
	}

	/**
	 * Attempts to add the specified texture to this item set. If the texture can be
	 * added, it will be added. If the texture can't be added, the reason is
	 * returned.
	 * 
	 * @param texture The texture that should be added to this item set
	 * @return The reason the texture could not be added, or null if the texture was
	 *         added successfully
	 */
	public String addTexture(NamedImage texture, boolean checkClass) {
		if (!bypassChecks()) {
			if (texture == null)
				return "Can't add null textures";
			if (checkClass && texture.getClass() != NamedImage.class)
				return "Use the right method for that class";
			String nameError = checkName(texture.getName());
			if (nameError != null)
				return nameError;
			for (NamedImage current : textures)
				if (current.getName().equals(texture.getName()))
					return "There is already a texture with that name";
		}
		textures.add(texture);
		return null;
	}

	/**
	 * Attempts to change the specified texture in this item set. If the texture can
	 * be changed, it will be changed. if the texture can't be changed, the reason
	 * is returned.
	 * 
	 * @param texture  The texture to change
	 * @param newName  The new name of the texture (possibly the old name)
	 * @param newImage The new image of the texture (possibly the old image)
	 * @return The reason the texture could not be changed, or null if the texture
	 *         changed successfully
	 */
	public String changeTexture(NamedImage texture, String newName, BufferedImage newImage, boolean checkClass) {
		if (!bypassChecks()) {
			if (texture == null)
				return "Can't change null textures";
			String nameError = checkName(newName);
			if (nameError != null)
				return nameError;
			if (newImage == null)
				return "You need to select an image";
			boolean has = false;
			for (NamedImage current : textures) {
				if (current == texture)
					has = true;
				else if (current.getName().equals(texture.getName()))
					return "Another texture has that name already";
			}
			if (!has)
				return "The previous texture is not in the list!";
		}
		texture.setName(newName);
		texture.setImage(newImage);
		return null;
	}

	/**
	 * Attempts to remove the specified texture from this item set. If the texture
	 * could not be removed, the reason is returned. If the texture could be
	 * removed, it will be removed.
	 * 
	 * @param texture The texture that should be removed from this set
	 * @return The reason the texture could not be removed, or null if the texture
	 *         was removed successfully.
	 */
	public String removeTexture(NamedImage texture) {
		if (!bypassChecks()) {
			boolean has = false;
			for (NamedImage current : textures) {
				if (current == texture) {
					has = true;
					break;
				}
			}
			if (!has)
				return "That texture is not in this item set.";
			for (CustomItem item : items)
				if (item.getTexture() == texture)
					return "That texture is used by " + item.getName();
		}
		textures.remove(texture);
		return null;
	}

	/**
	 * Attempts to add the specified bow texture to this item set. If the texture
	 * can be added, it will be added. If the texture can't be added, the reason is
	 * returned.
	 * 
	 * @param texture    The texture that should be added to this item set
	 * @param checkClass True if the class must be BowTextures.class, false if it
	 *                   can be a subclass as well
	 * @return The reason the texture could not be added, or null if it was added
	 *         succesfully
	 */
	public String addBowTexture(BowTextures texture, boolean checkClass) {
		if (!bypassChecks()) {
			if (texture == null)
				return "Can't add null textures";
			if (checkClass && texture.getClass() != BowTextures.class)
				return "Use the right method for this class";
			String nameError = checkName(texture.getName());
			if (nameError != null)
				return nameError;
			for (NamedImage current : textures)
				if (current.getName().equals(texture.getName()))
					return "There is already a texture with that name";
			List<BowTextures.Entry> pullEntries = texture.getPullTextures();
			for (BowTextures.Entry pullEntry : pullEntries) {
				if (pullEntry.getTexture() == null) {
					return "Pull " + pullEntry.getPull() + " doesn't have a texture";
				}
				if (pullEntry.getPull() < 0 || pullEntry.getPull() > 1) {
					return "All pulls must be between 0 and 1";
				}
			}
		}
		return addTexture(texture, false);
	}

	public String changeBowTexture(BowTextures current, String newName, BufferedImage newTexture,
			List<BowTextures.Entry> newPullTextures, boolean checkClass) {
		if (!bypassChecks()) {
			if (current == null)
				return "Can't change null textures";
			if (checkClass && current.getClass() != BowTextures.class)
				return "Use the right method for that class";
			for (BowTextures.Entry pullTexture : newPullTextures) {
				if (pullTexture.getTexture() == null) {
					return "There is no texture for pull " + pullTexture.getPull();
				}
				if (pullTexture.getPull() < 0 || pullTexture.getPull() > 1) {
					return "All pulls must be between 0 and 1";
				}
			}
		}
		String error = changeTexture(current, newName, newTexture, false);
		if (error == null) {
			current.setEntries(newPullTextures);
		}
		return error;
	}
	
	/**
	 * Attempts to add the specified armor piece to this item set. If the piece can be added,
	 * it will be added. If the armor piece can't be added, the reason is returned.
	 * 
	 * @param item The armor piece that should be added to this item set
	 * @return The reason the piece could not be added or null if it was added
	 *         successfully
	 */
	public String addArmor(CustomArmor item, boolean checkClass) {
		if (!bypassChecks()) {
			if (item == null)
				return "Can't add null items";
			if (checkClass && item.getClass() != CustomArmor.class)
				return "Use the appropriate method for that class";
			if (item.getRed() < 0 || item.getRed() > 255)
				return "Red (" + item.getRed() + ") is out of range";
			if (item.getGreen() < 0 || item.getGreen() > 255)
				return "Green (" + item.getGreen() + ") is out of range";
			if (item.getBlue() < 0 || item.getBlue() > 255)
				return "Blue (" + item.getBlue() + ") is out of range";
		}
		return addTool(item, false);
	}
	
	public String changeArmor(CustomArmor armor, CustomItemType newType, short newDamage, String newName, 
			String newDisplayName, String[] newLore, AttributeModifier[] newAttributes, 
			Enchantment[] newEnchantments, boolean allowEnchanting, boolean allowAnvil, 
			Ingredient repairItem, long newDurability, NamedImage newTexture, int newRed, int newGreen, 
			int newBlue, boolean[] itemFlags, int entityHitDurabilityLoss, int blockBreakDurabilityLoss, 
			DamageResistances resistances, boolean checkClass) {
		if (!bypassChecks()) {
			if (armor == null)
				return "Can't change bows that do not exist";
			if (checkClass && armor.getClass() != CustomArmor.class)
				return "Use the appropriate method for the class";
			if (armor.getRed() < 0 || armor.getRed() > 255)
				return "Red (" + armor.getRed() + ") is out of range";
			if (armor.getGreen() < 0 || armor.getGreen() > 255)
				return "Green (" + armor.getGreen() + ") is out of range";
			if (armor.getBlue() < 0 || armor.getBlue() > 255)
				return "Blue (" + armor.getBlue() + ") is out of range";
			if (resistances == null)
				return "The damage resistances can't be null";
		}
		String error = changeTool(armor, newType, newDamage, newName, newDisplayName, newLore, newAttributes, newEnchantments,
				allowEnchanting, allowAnvil, repairItem, newDurability, newTexture, itemFlags,
				entityHitDurabilityLoss, blockBreakDurabilityLoss, false);
		if (error == null) {
			armor.setRed(newRed);
			armor.setGreen(newGreen);
			armor.setBlue(newBlue);
			armor.setDamageResistances(resistances);
			return null;
		} else {
			return error;
		}
	}

	/**
	 * Attempts to add the specified bow to this item set. If the bow can be added,
	 * it will be added. If the bow can't be added, the reason is returned.
	 * 
	 * @param item The bow that should be added to this item set
	 * @return The reason the bow could not be added or null if it was added
	 *         successfully
	 */
	public String addBow(CustomBow item, boolean checkClass) {
		if (!bypassChecks()) {
			if (item == null)
				return "Can't add null items";
			if (checkClass && item.getClass() != CustomBow.class)
				return "Use the appropriate method for that class";
			if (item.getTexture() == null)
				return "Every item needs a texture";
			List<BowTextures.Entry> pullTextures = item.getTexture().getPullTextures();
			for (BowTextures.Entry pullTexture : pullTextures) {
				if (pullTexture == null)
					return "One of the pull textures is undefined";
				if (pullTexture.getTexture() == null)
					return "The texture for pull " + pullTexture.getPull() + " is undefined.";
			}
		}
		return addTool(item, false);
	}

	public String changeBow(CustomBow bow, short newDamage, String newName, String newDisplayName, String[] newLore,
			AttributeModifier[] newAttributes, Enchantment[] newEnchantments, double newDamageMultiplier, double newSpeedMultiplier,
			int newKnockbackStrength, boolean useGravity, boolean allowEnchanting, boolean allowAnvil,
			Ingredient repairItem, long newDurability, BowTextures newTextures, boolean[] itemFlags,
			int entityHitDurabilityLoss, int blockBreakDurabilityLoss, int shootDurabilityLoss,
			boolean checkClass) {
		if (!bypassChecks()) {
			if (bow == null)
				return "Can't change bows that do not exist";
			if (checkClass && bow.getClass() != CustomBow.class)
				return "Use the appropriate method for the class";
			List<BowTextures.Entry> pullTextures = newTextures.getPullTextures();
			for (BowTextures.Entry pullTexture : pullTextures) {
				if (pullTexture == null)
					return "One of the pull textures is undefined";
				if (pullTexture.getTexture() == null)
					return "The texture for pull " + pullTexture.getPull() + " is undefined.";
			}
			if (shootDurabilityLoss < 0)
				return "The shoot durability loss can't be negative";
		}
		String error = changeTool(bow, CustomItemType.BOW, newDamage, newName, newDisplayName, newLore, newAttributes, newEnchantments,
				allowEnchanting, allowAnvil, repairItem, newDurability, newTextures, itemFlags,
				entityHitDurabilityLoss, blockBreakDurabilityLoss, false);
		if (error == null) {
			bow.setDamageMultiplier(newDamageMultiplier);
			bow.setSpeedMultiplier(newSpeedMultiplier);
			bow.setKnockbackStrength(newKnockbackStrength);
			bow.setGravity(useGravity);
			bow.setShootDurabilityLoss(shootDurabilityLoss);
			return null;
		} else {
			return error;
		}
	}

	/**
	 * Attempts to add the specified tool to this item set. If the tool can be
	 * added, it will be added. If the tool can't be added, the reason is returned.
	 * 
	 * @param item The tool that should be added to this item set
	 * @return The reason the tool could not be added, or null if the tool was added
	 *         succesfully
	 */
	public String addTool(CustomTool item, boolean checkClass) {
		if (!bypassChecks()) {
			if (item == null)
				return "Can't add null items";
			if (checkClass && item.getClass() != CustomTool.class)
				return "Use the appropriate method for that class";
			if (item.getRepairItem() instanceof CustomItemIngredient
					&& !(((CustomItemIngredient) item.getRepairItem()).getItem().getClass() == SimpleCustomItem.class))
				return "Only vanilla items and simple custom items are allowed as repair item.";
			if (item.allowAnvilActions() && item.getDisplayName().contains("§"))
				return "Items with color codes in their display name can not allow anvil actions";
			if (item.allowEnchanting() && item.getDefaultEnchantments().length > 0)
				return "You can't allow enchanting on items that have default enchantments";
		}
		return addItem(item, false);
	}
	
	public String addShears(CustomShears shears, boolean checkClass) {
		if (!bypassChecks()) {
			if (shears == null)
				return "Can't add null items";
			if (checkClass && shears.getClass() != CustomShears.class)
				return "Use the appropriate method for that class";
			if (shears.getShearDurabilityLoss() < 0)
				return "The shear durability loss must be a positive integer";
		}
		return addTool(shears, false);
	}
	
	public String addHoe(CustomHoe hoe, boolean checkClass) {
		if (!bypassChecks()) {
			if (hoe == null)
				return "Can't add null items";
			if (checkClass && hoe.getClass() != CustomHoe.class)
				return "Use the appropriate method for that class";
			if (hoe.getTillDurabilityLoss() < 0)
				return "The till durability loss must be a positive integer";
		}
		return addTool(hoe, false);
	}

	/**
	 * Attempts to change the specified tool in this item set. If the tool can be
	 * changed, it will be changed. If the tool can't be changed, the reason is
	 * returned.
	 * 
	 * @param item            The tool that should be changed
	 * @param newType         The new item type of the tool
	 * @param newDamage       The new internal damage of the tool
	 * @param newName         The new name of the tool
	 * @param newDisplayName  The new display name of the tool
	 * @param newLore         The new lore of the tool
	 * @param newAttributes   The new attribute modifiers of the tool
	 * @param allowEnchanting The new value of allowEnchanting of the tool
	 * @param allowAnvil      The new value of allowAnvil of the tool
	 * @param newDurability   The new maximum uses of the tool
	 * @param newImage        The new image of the tool
	 * @return The reason the tool could not be changed, or null if the tool was
	 *         changed successfully
	 */
	public String changeTool(CustomTool item, CustomItemType newType, short newDamage, String newName,
			String newDisplayName, String[] newLore, AttributeModifier[] newAttributes, Enchantment[] newEnchantments, boolean allowEnchanting,
			boolean allowAnvil, Ingredient repairItem, long newDurability, NamedImage newImage, 
			boolean[] itemFlags, int entityHitDurabilityLoss, int blockBreakDurabilityLoss,
			boolean checkClass) {
		if (!bypassChecks()) {
			if (checkClass && item.getClass() != CustomTool.class)
				return "Use the appropriate method to change this class";
			if (allowAnvil && newDisplayName.contains("§"))
				return "Items with color codes in their display name can not allow anvil actions";
			if (repairItem instanceof CustomItemIngredient
					&& !(((CustomItemIngredient) repairItem).getItem().getClass() == SimpleCustomItem.class))
				return "Only vanilla items and simple custom items are allowed as repair item.";
			if (allowEnchanting && newEnchantments.length > 0)
				return "You can't allow enchanting on items that have default enchantments";
			if (entityHitDurabilityLoss < 0)
				return "The entity hit durability loss can't be negative";
			if (blockBreakDurabilityLoss < 0)
				return "The block break durability loss can't be negative";
		}
		String error = changeItem(item, newType, newDamage, newName, newDisplayName, newLore, newAttributes, 
				newEnchantments, newImage, itemFlags,
				false);
		if (error == null) {
			item.setAllowEnchanting(allowEnchanting);
			item.setAllowAnvilActions(allowAnvil);
			item.setRepairItem(repairItem);
			item.setDurability(newDurability);
			item.setEntityHitDurabilityLoss(entityHitDurabilityLoss);
			item.setBlockBreakDurabilityLoss(blockBreakDurabilityLoss);
			return null;
		} else {
			return error;
		}
	}
	
	public String changeShears(CustomShears shears, CustomItemType newType, short newDamage, String newName,
			String newDisplayName, String[] newLore, AttributeModifier[] newAttributes, Enchantment[] newEnchantments, boolean allowEnchanting,
			boolean allowAnvil, Ingredient repairItem, long newDurability, NamedImage newImage, 
			boolean[] itemFlags, int entityHitDurabilityLoss, int blockBreakDurabilityLoss, int shearDurabilityLoss,
			boolean checkClass) {
		if (!bypassChecks()) {
			if (shearDurabilityLoss < 0) {
				return "The shear durability loss must be positive";
			}
			if (checkClass && shears.getClass() != CustomShears.class) {
				return "Use the appropriate method to change this class";
			}
		}
		String error = changeTool(shears, newType, newDamage, newName, newDisplayName, newLore, newAttributes,
				newEnchantments, allowEnchanting, allowAnvil, repairItem, newDurability, newImage, itemFlags,
				entityHitDurabilityLoss, blockBreakDurabilityLoss, false);
		if (error == null) {
			shears.setShearDurabilityLoss(shearDurabilityLoss);
			return null;
		} else {
			return error;
		}
	}
	
	public String changeHoe(CustomHoe hoe, CustomItemType newType, short newDamage, String newName,
			String newDisplayName, String[] newLore, AttributeModifier[] newAttributes, Enchantment[] newEnchantments, boolean allowEnchanting,
			boolean allowAnvil, Ingredient repairItem, long newDurability, NamedImage newImage, 
			boolean[] itemFlags, int entityHitDurabilityLoss, int blockBreakDurabilityLoss, int tillDurabilityLoss,
			boolean checkClass) {
		if (!bypassChecks()) {
			if (tillDurabilityLoss < 0) {
				return "The till durability loss must be positive";
			}
			if (checkClass && hoe.getClass() != CustomHoe.class) {
				return "Use the appropriate method to change this class";
			}
		}
		String error = changeTool(hoe, newType, newDamage, newName, newDisplayName, newLore, newAttributes,
				newEnchantments, allowEnchanting, allowAnvil, repairItem, newDurability, newImage, itemFlags,
				entityHitDurabilityLoss, blockBreakDurabilityLoss, false);
		if (error == null) {
			hoe.setTillDurabilityLoss(tillDurabilityLoss);
			return null;
		} else {
			return error;
		}
	}

	private String addItem(CustomItem item, boolean doClassCheck) {
		if (!bypassChecks()) {
			if (item == null)
				return "Can't add null items";
			String nameError = checkName(item.getName());
			if (nameError != null)
				return nameError;
			if (doClassCheck && item.getClass() != CustomItem.class)
				return "Use the appropriate method for that class";
			if (item.getTexture() == null)
				return "Every item needs a texture";
			if (item.getAttributes() == null)
				return "Attributes are null";
			if (item.getAttributes().length > Byte.MAX_VALUE)
				return "Too many attribute modifiers";
			for (AttributeModifier att : item.getAttributes()) {
				if (att.getAttribute() == null) {
					return "An attribute modifier has no attribute";
				}
				if (att.getOperation() == null) {
					return "An attribute modifier has no operation";
				}
				if (att.getSlot() == null) {
					return "An attribute modifier has no slot";
				}
			}
			if (item.getDefaultEnchantments() == null)
				return "Default enchantments are null";
			if (item.getDefaultEnchantments().length > Byte.MAX_VALUE)
				return "Too many default enchantments";
			for (Enchantment enchantment : item.getDefaultEnchantments()) {
				if (enchantment.getType() == null) {
					return "An enchantment has no type";
				}
			}
			for (CustomItem current : items) {
				if (current.getName().equals(item.getName()))
					return "There is already a custom item with that name";
				if (current.getItemType() == item.getItemType() && current.getItemDamage() == item.getItemDamage())
					return "There is already a custom item with the same item type and damage";
			}
		}
		items.add(item);
		return null;
	}

	/**
	 * Attempts to add the specified simple item to this item set. If the item can be
	 * added, it will be added. If the item can't be added, the reason is returned.
	 * 
	 * @param item The simple item that should be added to this set
	 * @return The reason the item could not be added, or null if the item was added
	 *         successfully
	 */
	public String addSimpleItem(SimpleCustomItem item) {
		if (!bypassChecks()) {
			if (item == null)
				return "item is null";
			if (item.getMaxStacksize() < 1 || item.getMaxStacksize() > 64)
				return "The maximum stacksize (" + item.getMaxStacksize() + ") is out of range";
		}
		return addItem(item, false);
	}
	
	public String changeSimpleItem(SimpleCustomItem item, CustomItemType newType, short newDamage, String newName,
			String newDisplayName, String[] newLore, AttributeModifier[] newAttributes, 
			Enchantment[] newEnchantments, NamedImage newImage, int newStacksize, boolean[] newItemFlags,
			boolean checkClass) {
		if (!bypassChecks()) {
			if (checkClass && item.getClass() != SimpleCustomItem.class)
				return "Use the right method for the class";
			if (newStacksize < 1 || newStacksize > 64)
				return "The maximum stacksize (" + newStacksize + ") is out of range";
		}
		String error = changeItem(item, newType, newDamage, newName, newDisplayName, newLore, newAttributes,
				newEnchantments, newImage, newItemFlags, false);
		if (error == null) {
			item.setMaxStacksize(newStacksize);
		}
		return error;
	}

	/**
	 * Attempts to change the specified item in this item set. If the item can be
	 * changed, it will be changed. If the item can't be changed, the reason is
	 * returned.
	 * 
	 * @param item           The item that should be changed
	 * @param newType        The new type of the item
	 * @param newDamage      The new damage of the item
	 * @param newName        The new name of the item
	 * @param newDisplayName The new display name of the item
	 * @param newLore        The new lore of the item
	 * @param newAttributes  The new attribute modifiers of the item
	 * @param newImage       The new image of the item
	 * @return null if the item was changed successfully or the reason the item
	 *         could not be changed
	 */
	public String changeItem(CustomItem item, CustomItemType newType, short newDamage, String newName,
			String newDisplayName, String[] newLore, AttributeModifier[] newAttributes, 
			Enchantment[] newEnchantments, NamedImage newImage, boolean[] itemFlags,
			boolean checkClass) {
		if (!bypassChecks()) {
			if (item == null)
				return "Can't change null items";
			if (checkClass && item.getClass() != CustomItem.class)
				return "Use the appropriate method to change that class";
			String nameError = checkName(newName);
			if (nameError != null)
				return nameError;
			boolean has = false;
			if (newImage == null)
				return "Every item needs a texture";
			if (newAttributes == null)
				return "Attributes are null";
			if (itemFlags == null)
				return "The item flags are null";
			if (itemFlags.length != ItemFlag.values().length)
				return "The length of the item flags is incorrect";
			if (newAttributes.length > Byte.MAX_VALUE)
				return "Too many attribute modifiers";
			for (AttributeModifier att : newAttributes) {
				if (att.getAttribute() == null) {
					return "An attribute modifier has no attribute";
				}
				if (att.getOperation() == null) {
					return "An attribute modifier has no operation";
				}
				if (att.getSlot() == null) {
					return "An attribute modifier has no slot";
				}
			}
			if (newEnchantments == null)
				return "Default enchantments are null";
			if (newEnchantments.length > Byte.MAX_VALUE)
				return "Too many default enchantments";
			for (Enchantment enchantment : newEnchantments) {
				if (enchantment.getType() == null) {
					return "An enchantment has no type";
				}
			}
			for (CustomItem current : items) {
				if (current == item) {
					has = true;
					break;
				} else {
					if (current.getItemType() == newType && current.getItemDamage() == newDamage) {
						return "The item " + current.getName() + " has the same internal type and damage.";
					}
				}
			}
			if (!has)
				return "There is no previous item!";
			has = false;
			for (NamedImage texture : textures)
				if (texture == newImage)
					has = true;
			if (!has)
				return "The specified texture is not in the texture list!";
		}
		item.setItemType(newType);
		item.setItemDamage(newDamage);
		item.setName(newName);
		item.setDisplayName(newDisplayName);
		item.setLore(newLore);
		item.setAttributes(newAttributes);
		item.setDefaultEnchantments(newEnchantments);
		item.setTexture(newImage);
		item.setItemFlags(itemFlags);
		return null;
	}

	/**
	 * Attempts to remove the specified item from this set. If the item could not be
	 * removed, the reason is returned. If the item can be removed, it will be
	 * removed and null will be returned.
	 * 
	 * @param item The item that should be removed from this ItemSet
	 * @return The reason the item could not be removed, or null if the item was
	 *         removed successfully.
	 */
	public String removeItem(CustomItem item) {
		if (!bypassChecks()) {
			for (Recipe recipe : recipes) {
				if (recipe.getResult() instanceof CustomItemResult
						&& ((CustomItemResult) recipe.getResult()).getItem() == item)
					return "At least one of your recipes has this item as result.";
				if (recipe.requires(item))
					return "At least one of your recipes has this item as an ingredient.";
			}
		}
		if (!items.remove(item)) {
			return "This item is not in the item set";
		}
		return null;
	}

	/**
	 * Attempts to add a shaped recipe with the specified id, ingredients and result
	 * to this ItemSet. If the recipe can be added, it will be added. If the recipe
	 * can't be added, the reason is returned.
	 * 
	 * @param id          The id of the recipe to add
	 * @param ingredients The ingredients of the recipe to add
	 * @param result      The result of the recipe to add
	 * @return The reason why the recipe can't be added, or null if the recipe was
	 *         added successfully
	 */
	public String addShapedRecipe(Ingredient[] ingredients, Result result) {
		if (!bypassChecks()) {
			for (Recipe recipe : recipes)
				if (recipe.hasConflictingShapedIngredients(ingredients))
					return "The ingredients of another recipe conflict with these ingredients.";
		}
		recipes.add(new ShapedRecipe(ingredients, result));
		return null;
	}

	/**
	 * Attempts to change the ingredients and result of the specified ShapedRecipe.
	 * If the recipe can be changed, it will be changed. If the recipe can't be
	 * changed, the reason is returned.
	 * 
	 * @param previous    The recipe to change
	 * @param ingredients The new ingredients for the recipe
	 * @param result      The new result for the recipe
	 * @return The reason the recipe can't be changed, or null if the recipe changed
	 *         succesfully.
	 */
	public String changeShapedRecipe(ShapedRecipe previous, Ingredient[] ingredients, Result result) {
		if (!bypassChecks()) {
			boolean has = false;
			for (Recipe recipe : recipes) {
				if (recipe == previous) {
					has = true;
					break;
				} else if (recipe.hasConflictingShapedIngredients(ingredients)) {
					return "Another shaped recipe (" + recipe.getResult() + ") has conflicting ingredients";
				}
			}
			if (!has)
				return "That recipe is not in this item set";
		}
		previous.setIngredients(ingredients);
		previous.setResult(result);
		return null;
	}

	/**
	 * Attempts to add a shapeless recipe with the specified ingredients and result
	 * to this set. If the recipe can be added, it will be added. If the recipe
	 * can't be added, the reason is returned.
	 * 
	 * @param ingredients The ingredients of the shapeless recipe
	 * @param result      The result of the shapeless recipe
	 * @return The reason the recipe could not be added, or null if the recipe was
	 *         added successfully
	 */
	public String addShapelessRecipe(Ingredient[] ingredients, Result result) {
		if (!bypassChecks()) {
			for (Recipe recipe : recipes)
				if (recipe.hasConflictingShapelessIngredients(ingredients))
					return "Another shapeless recipe (" + recipe.getResult() + ") has conflicting ingredients";
		}
		recipes.add(new ShapelessRecipe(result, ingredients));
		return null;
	}

	/**
	 * Attempts to change the ingredients and result of the specified shapeless
	 * recipe. If the recipe can be changed, it will be changed. If the recipe can't
	 * be changed, the reason is returned.
	 * 
	 * @param previous       The shapeless recipe to change
	 * @param newIngredients The new ingredients of the recipe
	 * @param newResult      The new result of the recipe
	 * @return The reason the recipe could not be changed, or null if the recipe was
	 *         changed successfully.
	 */
	public String changeShapelessRecipe(ShapelessRecipe previous, Ingredient[] newIngredients, Result newResult) {
		if (!bypassChecks()) {
			boolean has = false;
			for (Recipe recipe : recipes) {
				if (recipe == previous) {
					has = true;
					break;
				} else if (recipe.hasConflictingShapelessIngredients(newIngredients)) {
					return "Another shapeless recipe (" + recipe.getResult() + ") has conflicting ingredients";
				}
			}
			if (!has)
				return "That recipe is not in this item set";
		}
		previous.setIngredients(newIngredients);
		previous.setResult(newResult);
		return null;
	}

	public void removeRecipe(Recipe recipe) {
		if (!recipes.remove(recipe) && !bypassChecks()) {
			throw new IllegalArgumentException("The given recipe was not in the recipe list!");
		}
	}
	
	private String validateDrop(Drop d) {
		if (d == null)
			return "The drop is null";
		if (d.getItemToDrop() == null)
			return "The item to drop is null";
		if (d.getMinDropAmount() < 1)
			return "The minimum drop amount must be at least 1";
		if (d.getMinDropAmount() > 64)
			return "The minimum drop amount must be at most 64";
		if (d.getMaxDropAmount() < 1)
			return "The maximum drop amount must be at least 1";
		if (d.getMaxDropAmount() > 64)
			return "The maximum drop amount must be at most 64";
		if (d.getMaxDropAmount() < d.getMinDropAmount())
			return "The maximum drop amount can't be smaller than the minimum drop amount";
		if (d.getDropChance() < 1)
			return "The drop chance must be at least 1";
		if (d.getDropChance() > 100)
			return "The drop chance must be at most 100";
		return null;
	}
	
	public String addBlockDrop(BlockDrop drop) {
		if (!bypassChecks()) {
			if (drop == null)
				return "The blockDrop is null";
			if (drop.getBlock() == null)
				return "The block is null";
			String dropError = validateDrop(drop.getDrop());
			if (dropError != null)
				return dropError;
		}
		blockDrops.add(drop);
		return null;
	}
	
	public String changeBlockDrop(BlockDrop old, BlockType newBlock, Drop newDrop) {
		if (!bypassChecks()) {
			if (old == null)
				return "The old blockDrop is null";
			boolean has = false;
			for (BlockDrop existing : blockDrops) {
				if (existing == old) {
					has = true;
					break;
				}
			}
			if (!has) {
				return "The old blockDrop was not in this item set";
			}
			if (newBlock == null)
				return "The new block is null";
			String dropError = validateDrop(newDrop);
			if (dropError != null)
				return dropError;
		}
		old.setBlock(newBlock);
		old.setDrop(newDrop);
		return null;
	}
	
	public void removeBlockDrop(BlockDrop drop) {
		if (!blockDrops.remove(drop) && !bypassChecks()) {
			throw new IllegalArgumentException("The drop " + drop + " was not in the block drop list!");
		}
	}
	
	public String addMobDrop(EntityDrop drop) {
		if (!bypassChecks()) {
			if (drop == null)
				return "The mob drop is null";
			if (drop.getEntityType() == null)
				return "The entity type is null";
			String dropError = validateDrop(drop.getDrop());
			if (dropError != null)
				return dropError;
			if (mobDrops.contains(drop))
				return "That mob drop is already in the mob drop list";
		}
		mobDrops.add(drop);
		return null;
	}
	
	public String changeMobDrop(EntityDrop old, CIEntityType newType, String newRequiredName, Drop newDrop) {
		if (!bypassChecks()) {
			if (newType == null)
				return "The selected entity type is null";
			String dropError = validateDrop(newDrop);
			if (dropError != null)
				return dropError;
			if (!mobDrops.contains(old))
				return "The mob drop you are changing is not in the mob drop list";
		}
		old.setEntityType(newType);
		old.setRequiredName(newRequiredName);
		old.setDrop(newDrop);
		return null;
	}
	
	public void removeMobDrop(EntityDrop drop) {
		if (!mobDrops.remove(drop) && !bypassChecks()) {
			throw new IllegalArgumentException("The drop " + drop + " was not in the mob drop list!");
		}
	}

	/**
	 * Do not modify this collection directly!
	 * 
	 * @return The CustomItem collection of this ItemSet
	 */
	public Collection<CustomItem> getItems() {
		return items;
	}

	/**
	 * Do not modify this collection directly!
	 * 
	 * @return The NamedImage collection of this ItemSet
	 */
	public Collection<NamedImage> getTextures() {
		return textures;
	}

	/**
	 * Do not modify this collection directly!
	 * 
	 * @return The Recipe collection of this ItemSet
	 */
	public Collection<Recipe> getRecipes() {
		return recipes;
	}
	
	/**
	 * Do not modify this collection directly!
	 * @return The mob drop collection of this ItemSet
	 */
	public Collection<EntityDrop> getMobDrops(){
		return mobDrops;
	}
	
	/**
	 * Do not modify this collection directly!
	 * @return The block drop collection of this ItemSet
	 */
	public Collection<BlockDrop> getBlockDrops(){
		return blockDrops;
	}

	public short nextAvailableDamage(CustomItemType type) {
		boolean[] usedDamage = new boolean[type.getMaxDurability() - 1];
		for (CustomItem item : items)
			if (item.getItemType() == type)
				usedDamage[item.getItemDamage() - 1] = true;
		for (short damage = 1; damage < type.getMaxDurability(); damage++)
			if (!usedDamage[damage - 1])
				return damage;
		return -1;
	}

	@Override
	public CustomItem getCustomItemByName(String name) {
		for (CustomItem item : items)
			if (item.getName().equals(name))
				return item;
		return null;
	}
}