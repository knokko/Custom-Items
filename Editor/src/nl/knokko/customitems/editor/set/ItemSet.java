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
import nl.knokko.customitems.damage.DamageSource;
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
import nl.knokko.customitems.editor.set.item.CustomShield;
import nl.knokko.customitems.editor.set.item.CustomTool;
import nl.knokko.customitems.editor.set.item.CustomTrident;
import nl.knokko.customitems.editor.set.item.NamedImage;
import nl.knokko.customitems.editor.set.item.SimpleCustomItem;
import nl.knokko.customitems.editor.set.item.texture.BowTextures;
import nl.knokko.customitems.editor.set.projectile.CustomProjectileCover;
import nl.knokko.customitems.editor.set.projectile.ProjectileCover;
import nl.knokko.customitems.editor.set.projectile.SphereProjectileCover;
import nl.knokko.customitems.editor.set.recipe.Recipe;
import nl.knokko.customitems.editor.set.recipe.ShapedRecipe;
import nl.knokko.customitems.editor.set.recipe.ShapelessRecipe;
import nl.knokko.customitems.editor.set.recipe.ingredient.CustomItemIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.DataVanillaIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.editor.set.recipe.result.CustomItemResult;
import nl.knokko.customitems.editor.set.recipe.result.DataVanillaResult;
import nl.knokko.customitems.editor.set.recipe.result.Result;
import nl.knokko.customitems.editor.set.recipe.result.SimpleVanillaResult;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.CustomItemType.Category;
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

import static nl.knokko.customitems.MCVersions.*;
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
		else if (encoding == ItemEncoding.ENCODING_ARMOR_7)
			return loadArmor7(input, checkCustomModel);
		else if (encoding == ItemEncoding.ENCODING_SHIELD_6)
			return loadShield6(input, checkCustomModel);
		else if (encoding == ItemEncoding.ENCODING_TRIDENT_7)
			return loadTrident7(input, checkCustomModel);
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
		NamedImage texture = getTextureByName(imageName);
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
		NamedImage texture = getTextureByName(imageName);
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
		NamedImage texture = getTextureByName(imageName);
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
		NamedImage texture = getTextureByName(imageName);
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
		NamedImage texture = getTextureByName(imageName);
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
		NamedImage texture = getTextureByName(imageName);
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
		NamedImage texture = getTextureByName(imageName);
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
		NamedImage texture = getTextureByName(imageName);
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
		NamedImage texture = getTextureByName(imageName);
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
		NamedImage texture = getTextureByName(imageName);
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
		NamedImage texture = getTextureByName(imageName);
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
		NamedImage texture = getTextureByName(imageName);
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
		NamedImage texture = getTextureByName(imageName);
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
		NamedImage texture = getTextureByName(imageName);
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
		if (itemType.isLeatherArmor()) {
			red = input.readByte() & 0xFF;
			green = input.readByte() & 0xFF;
			blue = input.readByte() & 0xFF;
		} else {
			red = 160;
			green = 101;
			blue = 64;
		}
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
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
		if (itemType.isLeatherArmor()) {
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
		NamedImage texture = getTextureByName(imageName);
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
		if (itemType.isLeatherArmor()) {
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
		
		DamageResistances resistances = DamageResistances.load12(input);
		
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomArmor(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, durability, allowEnchanting,
				allowAnvil, repairItem, texture, red, green, blue, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, resistances, customModel);
	}
	
	private CustomItem loadArmor7(BitInput input, boolean checkCustomModel) {
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
		if (itemType.isLeatherArmor()) {
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
		
		DamageResistances resistances = DamageResistances.load14(input);
		
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomArmor(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, durability, allowEnchanting,
				allowAnvil, repairItem, texture, red, green, blue, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, resistances, customModel);
	}
	
	private CustomItem loadShield6(BitInput input, boolean checkCustomModel) {
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
		double thresholdDamage = input.readDouble();
		
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		byte[] customBlockingModel = loadCustomModel(input, checkCustomModel);
		return new CustomShield(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, 
				durability, allowEnchanting, allowAnvil, repairItem, texture, itemFlags, 
				entityHitDurabilityLoss, blockBreakDurabilityLoss, thresholdDamage, customModel, customBlockingModel);
	}
	
	private CustomItem loadTrident7(BitInput input, boolean checkCustomModel) {
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
		int throwDurabilityLoss = input.readInt();
		double throwDamageMultiplier = input.readDouble();
		double speedMultiplier = input.readDouble();
		
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		byte[] customInHandModel = loadCustomModel(input, checkCustomModel);
		byte[] customThrowingModel = loadCustomModel(input, checkCustomModel);
		return new CustomTrident(damage, name, displayName, lore, attributes, defaultEnchantments, 
				durability, allowEnchanting, allowAnvil, throwDamageMultiplier, speedMultiplier, repairItem, 
				texture, itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, throwDurabilityLoss, 
				customModel, customInHandModel, customThrowingModel);
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
	private Collection<ProjectileCover> projectileCovers;

	public ItemSet(String fileName) {
		this.fileName = fileName;
		textures = new ArrayList<>();
		items = new ArrayList<>();
		recipes = new ArrayList<>();
		blockDrops = new ArrayList<>();
		mobDrops = new ArrayList<>();
		projectileCovers = new ArrayList<>();
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
		else if (encoding == ENCODING_5)
			load5(input);
		else
			throw new IllegalArgumentException("Unknown encoding: " + encoding);
	}

	private String checkName(String name) {
		if (name == null)
			return "The name can't be null";
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
		
		// Projectile covers (there are no projectile covers in this encoding)
		projectileCovers = new ArrayList<>();
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
		
		// Projectile covers (there are no projectile covers in this encoding)
		projectileCovers = new ArrayList<>();
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
		
		// Projectile covers (there are no projectile covers in this encoding)
		projectileCovers = new ArrayList<>();
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
		
		// Projectile covers (there are no projectile covers in this encoding)
		projectileCovers = new ArrayList<>();
	}
	
	private void load5(BitInput input) {
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
		
		// Projectile covers
		int numProjectileCovers = input.readInt();
		projectileCovers = new ArrayList<>(numProjectileCovers);
		for (int counter = 0; counter < numProjectileCovers; counter++)
			projectileCovers.add(ProjectileCover.fromBits(input, this));
	}

	/**
	 * A String containing only the quote character. I use this constant because
	 * it's annoying to get that character inside a String
	 */
	private static final String Q = "" + '"';
	
	public static String[] getDefaultModel(CustomItem item) {
		return getDefaultModel(item.getItemType(), item.getTexture().getName(), item.getItemType().isLeatherArmor());
	}
	
	public static String[] getDefaultModel(CustomItemType type, String textureName, boolean isLeather) {
		if (type == CustomItemType.BOW) {
			return getDefaultModelBow(textureName);
		} else if (type == CustomItemType.SHIELD) {
			return getDefaultModelShield(textureName);
		} else if (type == CustomItemType.TRIDENT) {
			return getDefaultModelTrident(textureName);
		} else {
			String[] start = {
			"{",
			"    \"parent\": \"item/handheld\",",
			"    \"textures\": {",
			"        \"layer0\": \"customitems/" + textureName + Q + (isLeather ? "," : "")
			};
			
			String[] mid;
			if (isLeather) {
				mid = new String[] {"        \"layer1\": \"customitems/" + textureName + Q};
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
	
	public static String[] getDefaultModelBow(String textureName) {
		return new String[] {
			"{",
			"    \"parent\": \"item/bow\",",
			"    \"textures\": {",
			"        \"layer0\": \"customitems/" + textureName + "_standby\"",
			"    }",
			"}"
		};
	}
	
	public static String[] getDefaultModelShield(String textureName) {
		return new String[] {
				"{",
				"    \"parent\": \"item/handheld\",",
				"    \"textures\": {",
				"        \"layer0\": \"customitems/" + textureName + "\"",
				"    },",
				"    \"display\": {",
				"        \"thirdperson_righthand\": {",
				"            \"rotation\": [0, -90, 0],",
				"            \"translation\": [3, -1.5, 6],",
				"            \"scale\": [1.25, 1.25, 1.25]",
				"        },",
				"        \"thirdperson_lefthand\": {",
				"            \"rotation\": [0, -90, 0],",
				"            \"translation\": [3, -2, 4],",
				"            \"scale\": [1.25, 1.25, 1.25]",
				"        },",
				"        \"firstperson_righthand\": {",
				"            \"rotation\": [-5, 0, -5],",
				"            \"translation\": [-2, -5, 0],",
				"            \"scale\": [1.35, 1.35, 1.35]",
				"        },",
				"        \"firstperson_lefthand\": {",
				"            \"rotation\": [5, 0, -5],",
				"            \"translation\": [-1.5, -5, 0],",
				"            \"scale\": [1.35, 1.35, 1.35]",
				"        }",
				"    }",
				"}"
		};
	}
	
	public static String[] getDefaultModelBlockingShield(String textureName) {
		return new String[] {
				"{",
				"    \"parent\": \"item/handheld\",",
				"    \"textures\": {",
				"        \"layer0\": \"customitems/" + textureName + "\"",
				"    },",
				"    \"display\": {",
				"        \"thirdperson_righthand\": {",
				"            \"rotation\": [35, -45, -5],",
				"            \"translation\": [5, 0, 1],",
				"            \"scale\": [1.15, 1.15, 1.15]",
				"        },",
				"        \"thirdperson_lefthand\": {",
				"            \"rotation\": [35, -35, -5],",
				"            \"translation\": [3, -3, -1],",
				"            \"scale\": [1.25, 1.25, 1.25]",
				"        },",
				"        \"firstperson_righthand\": {",
				"            \"rotation\": [0, -5, 5],",
				"            \"translation\": [-6, -0.5, 0],",
				"            \"scale\": [1.2, 1.2, 1.2]",
				"        },",
				"        \"firstperson_lefthand\": {",
				"            \"rotation\": [0, -5, 5],",
				"            \"translation\": [-6, -2.5, 0],",
				"            \"scale\": [1.2, 1.2, 1.2]",
				"        }",
				"    }",
				"}"
		};
	}
	
	public static String[] getDefaultModelTrident(String textureName) {
		return new String[] {
				"{",
				"    \"parent\": \"item/generated\",",
				"    \"textures\": {",
				"        \"layer0\": \"customitems/" + textureName + "\"",
				"    },",
				"    \"display\": {",
				"        \"gui\": {",
				"            \"rotation\": [0, 0, -45],",
				"            \"translation\": [0, 0, 0],",
				"            \"scale\": [1, 1, 1]",
				"        },",
				"        \"ground\": {",
				"            \"rotation\": [0, 0, -45],",
				"            \"translation\": [0, 0, 0],",
				"            \"scale\": [0.5, 0.5, 0.5]",
				"        }",
				"    }",
				"}"
		};
	}
	
	public static String[] getDefaultModelTridentInHand(String textureName) {
		return new String[] {
				"{",
				"    \"parent\": \"item/handheld\",",
				"    \"textures\": {",
				"        \"layer0\": \"customitems/" + textureName + "\"",
				"    },",
				"    \"display\": {",
				"        \"thirdperson_righthand\": {",
				"            \"rotation\": [0, 65, 0],",
				"            \"translation\": [0, 0, 0],",
				"            \"scale\": [0.5, 1.8, 1.0]",
				"        },",
				"        \"thirdperson_lefthand\": {",
				"            \"rotation\": [0, 65, 0],",
				"            \"translation\": [0, 0, 0],",
				"            \"scale\": [0.5, 1.8, 1.0]",
				"        },",
				"        \"firstperson_righthand\": {",
				"            \"rotation\": [-30, 100, 0],",
				"            \"translation\": [4, 2, 0],",
				"            \"scale\": [0.5, 1.0, 1.0]",
				"        },",
				"        \"firstperson_lefthand\": {",
				"            \"rotation\": [-30, 100, 0],",
				"            \"translation\": [4, 2, 0],",
				"            \"scale\": [0.5, 1.0, 1.0]",
				"        }",
				"    }",
				"}"
		};
	}
	
	public static String[] getDefaultModelTridentThrowing(String textureName) {
		return new String[] {
				"{",
				"    \"parent\": \"item/handheld\",",
				"    \"textures\": {",
				"        \"layer0\": \"customitems/" + textureName + "\"",
				"    },",
				"    \"display\": {",
				"        \"thirdperson_righthand\": {",
				"            \"rotation\": [0, 90, 180],",
				"            \"translation\": [1, -3, 2],",
				"            \"scale\": [1, 2, 1]",
				"        },",
				"        \"thirdperson_lefthand\": {",
				"            \"rotation\": [0, 90, 180],",
				"            \"translation\": [1, -3, 2],",
				"            \"scale\": [1, 2, 1]",
				"        },",
				"        \"firstperson_righthand\": {",
				"            \"rotation\": [-20, -90, 0],",
				"            \"translation\": [5, 2, -1],",
				"            \"scale\": [1, 2, 1]",
				"        },",
				"        \"firstperson_lefthand\": {",
				"            \"rotation\": [-20, -90, 0],",
				"            \"translation\": [5, 2, -1],",
				"            \"scale\": [1, 2, 1]",
				"        }",
				"    }",
				"}"
		};
	}
	
	private static String[] getMinecraftModelTridentInHandBegin() {
		return new String[] {
				"{",
				"    \"parent\": \"builtin/entity\",",
				"    \"textures\": {",
				"        \"particle\": \"item/trident\"",
				"    },",
				"    \"display\": {",
				"        \"thirdperson_righthand\": {",
				"            \"rotation\": [0, 60, 0],",
				"            \"translation\": [11, 17, -2],",
				"            \"scale\": [1, 1, 1]",
				"        },",
				"        \"thirdperson_lefthand\": {",
				"            \"rotation\": [0, 60, 0],",
				"            \"translation\": [3, 17, 12],",
				"            \"scale\": [1, 1, 1]",
				"        },",
				"        \"firstperson_righthand\": {",
				"            \"rotation\": [0, -90, 25],",
				"            \"translation\": [-3, 17, 1],",
				"            \"scale\": [1, 1, 1]",
				"        },",
				"        \"firstperson_lefthand\": {",
				"            \"rotation\": [0, 90, -25],",
				"            \"translation\": [13, 17, 1],",
				"            \"scale\": [1, 1, 1]",
				"        },",
				"        \"gui\": {",
				"            \"rotation\": [15, -25, -5],",
				"            \"translation\": [2, 3, 0],",
				"            \"scale\": [0.65, 0.65, 0.65]",
				"        },",
				"        \"fixed\": {",
				"            \"rotation\": [0, 180, 0],",
				"            \"translation\": [-2, 4, -5],",
				"            \"scale\": [0.5, 0.5, 0.5]",
				"        },",
				"        \"ground\": {",
				"            \"rotation\": [0, 0, 0],",
				"            \"translation\": [4, 4, 2],",
				"            \"scale\": [0.25, 0.25, 0.25]",
				"        }",
				"    },",
				"    \"overrides\": [",
				"        {\"predicate\": {\"throwing\": 1}, \"model\": \"item/trident_throwing\"},",
		};
	}
	
	private static String[] getMinecraftModelTridentInHandEnd() {
		return new String[] {
				"        {\"predicate\": {\"damaged\": 1, \"damage\": 0}, \"model\": \"item/trident_in_hand\"},",
				"        {\"predicate\": {\"damaged\": 1, \"damage\": 0, \"throwing\": 1}, \"model\": \"item/trident_throwing\"}",
				"    ]",
				"}"
		};
	}
	
	public static String[] chain(String[]...arrays) {
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
	 * Export the item set for minecraft version 1.mcVersion with the new resourcepack format
	 * @param mcVersion The minecraft version to export for, after the 1.
	 * @return The error message if exporting failed, or null if the item set was exported successfully
	 */
	public String exportNew(int mcVersion) {
		String versionError = validateExportVersion(mcVersion);
		if (versionError != null) {
			return versionError;
		}
		try {
			
			// See exportOld for explanation
			File file = new File(Editor.getFolder() + "/" + fileName + ".cis");// cis stands for Custom Item Set
			OutputStream fileOutput = Files.newOutputStream(file.toPath());
			ByteArrayBitOutput output = new ByteArrayBitOutput();
			export3(output);
			output.terminate();
			
			byte[] bytes = output.getBytes();
			fileOutput.write(bytes);
			fileOutput.flush();
			fileOutput.close();
			
			byte[] textyBytes = createTextyBytes(bytes);
			fileOutput = Files.newOutputStream(new File(Editor.getFolder() + "/" + fileName + ".txt").toPath());
			fileOutput.write(textyBytes);
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
				} else if (item instanceof CustomShield) {
					CustomShield shield = (CustomShield) item;
					byte[] blockingModel = shield.getBlockingModel();
					entry = new ZipEntry("assets/minecraft/models/customitems/" + item.getName() + "_blocking.json");
					zipOutput.putNextEntry(entry);
					if (blockingModel != null) {
						zipOutput.write(blockingModel);
						zipOutput.flush();
					} else {
						String[] modelContent = getDefaultModelBlockingShield(item.getTexture().getName());
						jsonWriter = new PrintWriter(zipOutput);
						for (String line : modelContent) {
							jsonWriter.println(line);
						}
						jsonWriter.flush();
					}
				} else if (item instanceof CustomTrident) {
					CustomTrident trident = (CustomTrident) item;
					byte[] inHandModel = trident.customInHandModel;
					entry = new ZipEntry("assets/minecraft/models/customitems/" + item.getName() + "_in_hand.json");
					zipOutput.putNextEntry(entry);
					if (inHandModel != null) {
						zipOutput.write(inHandModel);
						zipOutput.flush();
					} else {
						String[] modelContent = getDefaultModelTridentInHand(item.getTexture().getName());
						jsonWriter = new PrintWriter(zipOutput);
						for (String line : modelContent) {
							jsonWriter.println(line);
						}
						jsonWriter.flush();
					}
					byte[] throwingModel = trident.customThrowingModel;
					entry = new ZipEntry("assets/minecraft/models/customitems/" + item.getName() + "_throwing.json");
					zipOutput.putNextEntry(entry);
					if (throwingModel != null) {
						zipOutput.write(throwingModel);
						zipOutput.flush();
					} else {
						String[] modelContent = getDefaultModelTridentThrowing(item.getTexture().getName());
						jsonWriter = new PrintWriter(zipOutput);
						for (String line : modelContent) {
							jsonWriter.println(line);
						}
						jsonWriter.flush();
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
					final PrintWriter jsonWriter = new PrintWriter(zipOutput);

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
						jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0}, \"model\": \"item/" + modelName + "\"},");
						jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0, \"pulling\": 1 }, \"model\": \"item/" + modelName + "_pulling_0\"},");
						jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0, \"pulling\": 1, \"pull\": 0.65 }, \"model\": \"item/" + modelName + "_pulling_1\"},");
						jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0, \"pulling\": 1, \"pull\": 0.9 }, \"model\": \"item/" + modelName + "_pulling_2\"}");
						jsonWriter.println("    ]");
						jsonWriter.println("}");
					} else if (entry.getKey() == CustomItemType.SHIELD) {
						
						// The beginning
						jsonWriter.println("{");
						jsonWriter.println("    \"parent\": \"builtin/entity\",");
						jsonWriter.println("    \"textures\": {");
						jsonWriter.println("        \"particle\": \"block/dark_oak_planks\"");
						jsonWriter.println("    },");
						jsonWriter.println("    \"display\": {");
						
						// All the display stuff, it's copied from minecrafts default shield model
						jsonWriter.println("        \"thirdperson_righthand\": {");
						jsonWriter.println("            \"rotation\": [0,90,0],");
						jsonWriter.println("            \"translation\": [10,6,-4],");
						jsonWriter.println("            \"scale\": [1,1,1]");
						jsonWriter.println("        },");
						jsonWriter.println("        \"thirdperson_lefthand\": {");
						jsonWriter.println("            \"rotation\": [0,90,0],");
						jsonWriter.println("            \"translation\": [10,6,12],");
						jsonWriter.println("            \"scale\": [1,1,1]");
						jsonWriter.println("        },");
						jsonWriter.println("        \"firstperson_righthand\": {");
						jsonWriter.println("            \"rotation\": [0,180,5],");
						jsonWriter.println("            \"translation\": [-10,2,-10],");
						jsonWriter.println("            \"scale\": [1.25,1.25,1.25]");
						jsonWriter.println("        },");
						jsonWriter.println("        \"firstperson_lefthand\": {");
						jsonWriter.println("            \"rotation\": [0,180,5],");
						jsonWriter.println("            \"translation\": [10,0,-10],");
						jsonWriter.println("            \"scale\": [1.25,1.25,1.25]");
						jsonWriter.println("        },");
						jsonWriter.println("        \"gui\": {");
						jsonWriter.println("            \"rotation\": [15,-25,-5],");
						jsonWriter.println("            \"translation\": [2,3,0],");
						jsonWriter.println("            \"scale\": [0.65,0.65,0.65]");
						jsonWriter.println("        },");
						jsonWriter.println("        \"fixed\": {");
						jsonWriter.println("            \"rotation\": [0,180,0],");
						jsonWriter.println("            \"translation\": [-2,4,-5],");
						jsonWriter.println("            \"scale\": [0.5,0.5,0.5]");
						jsonWriter.println("        },");
						jsonWriter.println("        \"ground\": {");
						jsonWriter.println("            \"rotation\": [0,0,0],");
						jsonWriter.println("            \"translation\": [4,4,2],");
						jsonWriter.println("            \"scale\": [0.25,0.25,0.25]");
						jsonWriter.println("        }");
						jsonWriter.println("    }, \"overrides\": [");
						
						// The next entry is part of preserving vanilla shield blocking model
						jsonWriter.println("        { \"predicate\": { \"blocking\": 1 }, \"model\": \"item/shield_blocking\" },");
						
						// Now the part for the custom shield predicates...
						for (CustomItem item : list) {
							jsonWriter.println("        { \"predicate\": { \"blocking\": 0, \"damaged\": 0, \"damage\": "
									+ (double) item.getItemDamage() / item.getItemType().getMaxDurability() + " }, \"model\": \"customitems/" + item.getName() + "\" },");
							jsonWriter.println("        { \"predicate\": { \"blocking\": 1, \"damaged\": 0, \"damage\": "
									+ (double) item.getItemDamage() / item.getItemType().getMaxDurability() + " }, \"model\": \"customitems/" + item.getName() + "_blocking\" },");
						}
						
						// The next ones are required to preserve the vanilla shield models
						jsonWriter.println("        { \"predicate\": { \"blocking\": 0, \"damaged\": 1, \"damage\": 0 }, \"model\": \"item/shield\" },");
						jsonWriter.println("        { \"predicate\": { \"blocking\": 1, \"damaged\": 1, \"damage\": 0 }, \"model\": \"item/shield_blocking\" }");
						
						// Now finish the json
						jsonWriter.println("    ]");
						jsonWriter.println("}");
					} else {
						// Begin of the json file
						jsonWriter.println("{");
						jsonWriter.println("    " + Q + "parent" + Q + ": " + Q + "item/handheld" + Q + ",");
						jsonWriter.println("    " + Q + "textures" + Q + ": {");
						jsonWriter.print("        " + Q + "layer0" + Q + ": " + Q + "item/" + textureName + Q);
						boolean isLeatherArmor = entry.getKey().isLeatherArmor();
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
					
					// Not part of the if-else chain above because the base item model of trident is not special
					if (entry.getKey() == CustomItemType.TRIDENT) {
						
						// The beginning:
						zipEntry = new ZipEntry("assets/minecraft/models/item/" + modelName + "_in_hand.json");
						zipOutput.putNextEntry(zipEntry);
						String[] begin = getMinecraftModelTridentInHandBegin();
						
						String[] end = getMinecraftModelTridentInHandEnd();
						
						for (String line : begin) {
							jsonWriter.println(line);
						}
						
						for (CustomItem item : list) {
							jsonWriter.println("        { \"predicate\": { \"throwing\": 0, \"damaged\": 0, \"damage\": "
									+ (double) item.getItemDamage() / item.getItemType().getMaxDurability() + " }, \"model\": \"customitems/" + item.getName() + "_in_hand\" },");
							jsonWriter.println("        { \"predicate\": { \"throwing\": 1, \"damaged\": 0, \"damage\": "
									+ (double) item.getItemDamage() / item.getItemType().getMaxDurability() + " }, \"model\": \"customitems/" + item.getName() + "_throwing\" },");
						}
						
						for (String line : end) {
							jsonWriter.println(line);
						}
						
						jsonWriter.flush();
					}
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
	
	private int ingredientVersion(Ingredient ingredient) {
		if (ingredient instanceof NoIngredient || ingredient instanceof CustomItemIngredient) {
			return VERSION1_12;
		} else if (ingredient instanceof SimpleVanillaIngredient) {
			return ((SimpleVanillaIngredient) ingredient).getType().version;
		} else {
			return ((DataVanillaIngredient) ingredient).getType().version;
		}
	}
	
	private int resultVersion(Result result) {
		if (result instanceof CustomItemResult) {
			return VERSION1_12;
		} else if (result instanceof SimpleVanillaResult) {
			return ((SimpleVanillaResult) result).getType().version;
		} else {
			return ((DataVanillaResult) result).getType().version;
		}
	}
	
	private String validateExportVersion(int version) {
		
		// Reject everything from a higher minecraft version
		for (CustomItem item : items) {
			for (Enchantment enchant : item.getDefaultEnchantments()) {
				if (enchant.getType().version > version) {
					return "The item " + item.getName() + " has enchantment " + enchant.getType().getName() + ", which was added after minecraft 1." + version;
				}
			}
			if (item instanceof CustomTool) {
				CustomTool tool = (CustomTool) item;
				if (ingredientVersion(tool.getRepairItem()) > version) {
					return "The repair item " + tool.getRepairItem() + " for " + tool.getName() + " was added after minecraft 1." + version;
				}
				
				if (item instanceof CustomArmor) {
					CustomArmor armor = (CustomArmor) item;
					DamageSource[] sources = DamageSource.values();
					for (DamageSource source : sources) {
						if (source.version > version && armor.getDamageResistances().getResistance(source) != 0) {
							return "Armor " + item.getName() + " has a damage resistance against " + source + ", which was added after minecraft 1." + version;
						}
					}
				}
				
				if (item.getItemType().version > version) {
					return "The item " + item.getName() + " is a " + item.getItemType() + ", which were added after minecraft 1." + version;
				}
			}
		}
		
		for (Recipe recipe : recipes) {
			if (recipe instanceof ShapedRecipe) {
				ShapedRecipe shaped = (ShapedRecipe) recipe;
				for (Ingredient ingredient : shaped.getIngredients()) {
					if (ingredientVersion(ingredient) > version) {
						return "The ingredient " + ingredient + " used for " + shaped.getResult() + " was added after minecraft 1." + version;
					}
				}
			} else {
				ShapelessRecipe shapeless = (ShapelessRecipe) recipe;
				for (Ingredient ingredient : shapeless.getIngredients()) {
					if (ingredientVersion(ingredient) > version) {
						return "The ingredient " + ingredient + " used for " + shapeless.getResult() + " was added after minecraft 1." + version;
					}
				}
			}
			
			if (resultVersion(recipe.getResult()) > version) {
				return "The crafting recipe result " + recipe.getResult() + " was added after minecraft 1." + version;
			}
		}
		
		for (BlockDrop drop : blockDrops) {
			if (drop.getBlock().version > version) {
				return "There is a block drop for " + drop.getBlock() + ", but this block was added after minecraft 1." + version;
			}
		}
		
		for (EntityDrop drop : mobDrops) {
			if (drop.getEntityType().version > version) {
				return "There is a mob drop for " + drop.getEntityType() + ", but this mob was added after minecraft 1." + version;
			}
		}
		
		return null;
	}
	
	private byte[] createTextyBytes(byte[] bytes) {
		byte[] textBytes = new byte[2 * bytes.length + 2 * bytes.length / 50];
		int textIndex = 0;
		int textCounter = 0;
		byte charCodeA = (byte) 'a';
		byte charCodeSR = (byte) '\r';
		byte charCodeSN = (byte) '\n';
		for (byte data : bytes) {
			int value = data & 0xFF;
			textBytes[textIndex++] = (byte) (charCodeA + value % 16);
			textBytes[textIndex++] = (byte) (charCodeA + value / 16);
					
			textCounter++;
			if (textCounter == 50) {
				textCounter = 0;
				textBytes[textIndex++] = charCodeSR;
				textBytes[textIndex++] = charCodeSN;
			}
		}
		return textBytes;
	}

	/**
	 * Export the item set for minecraft version 1.mcVersion with the old resourcepack format
	 * @param mcVersion The minecraft version to export for, after the 1.
	 * @return The error message if exporting failed, or null if the item set was exported successfully
	 */
	public String exportOld(int mcVersion) {
		
		String error = validateExportVersion(mcVersion);
		if (error != null) {
			return error;
		}
		try {
			ByteArrayBitOutput output = new ByteArrayBitOutput();
			export3(output);
			output.terminate();
			
			byte[] bytes = output.getBytes();
			
			// Write the .cis file, which stands for Custom Item Set
			File file = new File(Editor.getFolder() + "/" + fileName + ".cis");
			OutputStream fileOutput = Files.newOutputStream(file.toPath());
			fileOutput.write(bytes);
			fileOutput.flush();
			fileOutput.close();
			
			/*
			 * Write the .txt file, which can be used as alternative for the .cis file.
			 * It has a bigger file size and will be a bit slower to read, but it is useful
			 * for servers hosts like Aternos that do not allow users to upload (binary files).
			 * 
			 * It will only use alphabetic characters, which makes it possible to copy the data
			 * as text (although it still won't be readable by humans).
			 */
			byte[] textBytes = createTextyBytes(bytes);
			File textFile = new File(Editor.getFolder() + "/" + fileName + ".txt");
			fileOutput = Files.newOutputStream(textFile.toPath());
			fileOutput.write(textBytes);
			fileOutput.flush();
			fileOutput.close();
			
			// Write the .zip file, which is the resourcepack
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
				} else if (item instanceof CustomShield) {
					CustomShield shield = (CustomShield) item;
					byte[] blockingModel = shield.getBlockingModel();
					entry = new ZipEntry("assets/minecraft/models/customitems/" + item.getName() + "_blocking.json");
					zipOutput.putNextEntry(entry);
					if (blockingModel != null) {
						zipOutput.write(blockingModel);
						zipOutput.flush();
					} else {
						String[] modelContent = getDefaultModelBlockingShield(item.getName());
						jsonWriter = new PrintWriter(zipOutput);
						for (String line : modelContent) {
							jsonWriter.println(line);
						}
						jsonWriter.flush();
					}
				} // Don't bother custom trident models since tridents don't exist in old versions
			}
			
			// Projectile covers
			for (ProjectileCover cover : projectileCovers) {
				ZipEntry entry = new ZipEntry("assets/minecraft/models/customprojectiles/" + cover.name + ".json");
				zipOutput.putNextEntry(entry);
				cover.writeModel(zipOutput);
				zipOutput.flush();
			}

			// Map all custom items by their item type
			Map<CustomItemType, List<ItemDamageClaim>> itemMap = new EnumMap<CustomItemType, List<ItemDamageClaim>>(
					CustomItemType.class);
			
			// Add the custom items to the map
			for (CustomItem item : items) {
				List<ItemDamageClaim> list = itemMap.get(item.getItemType());
				if (list == null) {
					list = new ArrayList<>();
					itemMap.put(item.getItemType(), list);
				}
				list.add(item);
			}
			
			// Add the projectile covers to the map
			for (ProjectileCover cover : projectileCovers) {
				List<ItemDamageClaim> list = itemMap.get(cover.getItemType());
				if (list == null) {
					list = new ArrayList<>();
					itemMap.put(cover.getItemType(), list);
				}
				list.add(cover);
			}

			// Now create the item model files for those models
			Set<Entry<CustomItemType, List<ItemDamageClaim>>> entrySet = itemMap.entrySet();
			for (Entry<CustomItemType, List<ItemDamageClaim>> entry : entrySet) {
				List<ItemDamageClaim> list = entry.getValue();
				if (list != null) {
					// The items with low damage should come first
					list.sort((ItemDamageClaim a, ItemDamageClaim b) -> {
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

						for (ItemDamageClaim item : list) {
							jsonWriter.println("        { " + Q + "predicate" + Q + ": {" + Q + "damaged" + Q + ": 0, "
									+ Q + "damage" + Q + ": "
									+ (double) item.getItemDamage() / item.getItemType().getMaxDurability() + "}, " + Q
									+ "model" + Q + ": " + Q + item.getResourcePath() + Q + "},");
							List<BowTextures.Entry> pullTextures = ((CustomBow) item).getTexture().getPullTextures();
							int counter = 0;
							for (BowTextures.Entry pullTexture : pullTextures) {
								jsonWriter.println("        { " + Q + "predicate" + Q + ": {" + Q + "damaged" + Q
										+ ": 0, " + Q + "damage" + Q + ": "
										+ (double) item.getItemDamage() / item.getItemType().getMaxDurability() + ", "
										+ Q + "pulling" + Q + ": 1, " + Q + "pull" + Q + ": " + pullTexture.getPull()
										+ "}, " + Q + "model" + Q + ": " + Q + item.getResourcePath()
										+ "_pulling_" + counter++ + Q + "},");
							}
						}
						// End of the json file
						jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0}, \"model\": \"item/" + modelName + "\"},");
						jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0, \"pulling\": 1 }, \"model\": \"item/" + modelName + "_pulling_0\"},");
						jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0, \"pulling\": 1, \"pull\": 0.65 }, \"model\": \"item/" + modelName + "_pulling_1\"},");
						jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0, \"pulling\": 1, \"pull\": 0.9 }, \"model\": \"item/" + modelName + "_pulling_2\"}");
						jsonWriter.println("    ]");
						jsonWriter.println("}");
					} else if (entry.getKey() == CustomItemType.SHIELD) {
						
						// The beginning
						jsonWriter.println("{");
						jsonWriter.println("    \"parent\": \"builtin/entity\",");
						jsonWriter.println("    \"display\": {");
						
						// All the display stuff, it's copied from minecrafts default shield model
						jsonWriter.println("        \"thirdperson_righthand\": {");
						jsonWriter.println("            \"rotation\": [0,90,0],");
						jsonWriter.println("            \"translation\": [10.51,6,-4],");
						jsonWriter.println("            \"scale\": [1,1,1]");
						jsonWriter.println("        },");
						jsonWriter.println("        \"thirdperson_lefthand\": {");
						jsonWriter.println("            \"rotation\": [0,90,0],");
						jsonWriter.println("            \"translation\": [10.51,6,12],");
						jsonWriter.println("            \"scale\": [1,1,1]");
						jsonWriter.println("        },");
						jsonWriter.println("        \"firstperson_righthand\": {");
						jsonWriter.println("            \"rotation\": [0,180,5],");
						jsonWriter.println("            \"translation\": [-10,2,-10],");
						jsonWriter.println("            \"scale\": [1.25,1.25,1.25]");
						jsonWriter.println("        },");
						jsonWriter.println("        \"firstperson_lefthand\": {");
						jsonWriter.println("            \"rotation\": [0,180,5],");
						jsonWriter.println("            \"translation\": [10,0,-10],");
						jsonWriter.println("            \"scale\": [1.25,1.25,1.25]");
						jsonWriter.println("        },");
						jsonWriter.println("        \"gui\": {");
						jsonWriter.println("            \"rotation\": [15,-25,-5],");
						jsonWriter.println("            \"translation\": [2,3,0],");
						jsonWriter.println("            \"scale\": [0.65,0.65,0.65]");
						jsonWriter.println("        },");
						jsonWriter.println("        \"fixed\": {");
						jsonWriter.println("            \"rotation\": [0,180,0],");
						jsonWriter.println("            \"translation\": [-2,4,-5],");
						jsonWriter.println("            \"scale\": [0.5,0.5,0.5]");
						jsonWriter.println("        },");
						jsonWriter.println("        \"ground\": {");
						jsonWriter.println("            \"rotation\": [0,0,0],");
						jsonWriter.println("            \"translation\": [4,4,2],");
						jsonWriter.println("            \"scale\": [0.25,0.25,0.25]");
						jsonWriter.println("        }");
						jsonWriter.println("    }, \"overrides\": [");
						
						// The next entry is part of preserving vanilla shield blocking model
						jsonWriter.println("        { \"predicate\": { \"blocking\": 1 }, \"model\": \"item/shield_blocking\" },");
						
						// Now the part for the custom shield predicates...
						for (ItemDamageClaim item : list) {
							jsonWriter.println("        { \"predicate\": { \"blocking\": 0, \"damaged\": 0, \"damage\": "
									+ (double) item.getItemDamage() / item.getItemType().getMaxDurability() + " }, \"model\": \"" + item.getResourcePath() + "\" },");
							jsonWriter.println("        { \"predicate\": { \"blocking\": 1, \"damaged\": 0, \"damage\": "
									+ (double) item.getItemDamage() / item.getItemType().getMaxDurability() + " }, \"model\": \"" + item.getResourcePath() + "_blocking\" },");
						}
						
						// The next ones are required to preserve the vanilla shield models
						jsonWriter.println("        { \"predicate\": { \"blocking\": 0, \"damaged\": 1, \"damage\": 0 }, \"model\": \"item/shield\" },");
						jsonWriter.println("        { \"predicate\": { \"blocking\": 1, \"damaged\": 1, \"damage\": 0 }, \"model\": \"item/shield_blocking\" }");
						
						// Now finish the json
						jsonWriter.println("    ]");
						jsonWriter.println("}");
					} else {
						// Begin of the json file
						jsonWriter.println("{");
						jsonWriter.println("    " + Q + "parent" + Q + ": " + Q + "item/handheld" + Q + ",");
						jsonWriter.println("    " + Q + "textures" + Q + ": {");
						jsonWriter.print("        " + Q + "layer0" + Q + ": " + Q + "items/" + textureName + Q);
						boolean isLeatherArmor = entry.getKey().isLeatherArmor();
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
						for (ItemDamageClaim item : list) {
							jsonWriter.println("        { " + Q + "predicate" + Q + ": {" + Q + "damaged" + Q + ": 0, "
									+ Q + "damage" + Q + ": "
									+ (double) item.getItemDamage() / item.getItemType().getMaxDurability() + "}, " + Q
									+ "model" + Q + ": " + Q + item.getResourcePath() + Q + "},");
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
			save5(output);
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
	@SuppressWarnings("unused")
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
	
	// Add projectile covers
	private void save5(BitOutput output) {
		output.addByte(ENCODING_5);
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
		
		output.addInt(projectileCovers.size());
		for (ProjectileCover cover : projectileCovers)
			cover.toBits(output);
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
			if (hasTexture(texture.getName()))
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
			if (checkClass && texture.getClass() != NamedImage.class)
				return "Use the appropriate method for the class of that texture";
			String nameError = checkName(newName);
			if (nameError != null)
				return nameError;
			NamedImage sameName = getTextureByName(newName);
			if (sameName != null && sameName != texture)
				return "Another texture with that name already exists";
			if (newImage == null)
				return "You need to select an image";
			if (!textures.contains(texture))
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
			DamageResistances resistances, byte[] newCustomModel, boolean checkClass) {
		if (!bypassChecks()) {
			if (armor == null)
				return "Can't change armor pieces that do not exist";
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
				entityHitDurabilityLoss, blockBreakDurabilityLoss, newCustomModel, false);
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
			byte[] newCustomModel, boolean checkClass) {
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
				entityHitDurabilityLoss, blockBreakDurabilityLoss, newCustomModel, false);
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
	
	public String addShield(CustomShield shield, boolean checkClass) {
		if (!bypassChecks()) {
			if (shield == null)
				return "Can't add null items";
			if (checkClass && shield.getClass() != CustomShield.class)
				return "Use the appropriate method for that class!";
			double th = shield.getThresholdDamage();
			if (th < 0)
				return "The threshold damage can't be negative";
			if (th != th)
				return "The threshold damage can't be NaN";
		}
		return addTool(shield, false);
	}
	
	public String changeShield(CustomShield shield, CustomItemType newType, short newDamage, String newName,
			String newDisplayName, String[] newLore, AttributeModifier[] newAttributes, 
			Enchantment[] newEnchantments, boolean allowEnchanting, boolean allowAnvil, 
			Ingredient repairItem, long newDurability, NamedImage newImage, boolean[] itemFlags, 
			int entityHitDurabilityLoss, int blockBreakDurabilityLoss, double thresholdDamage,
			byte[] newCustomModel, byte[] newCustomBlockingModel, boolean checkClass) {
		if (!bypassChecks()) {
			if (shield == null)
				return "Can't change null items";
			if (checkClass && shield.getClass() != CustomShield.class)
				return "Use the appropriate method for that class!";
			double th = thresholdDamage;
			if (th < 0)
				return "The threshold damage can't be negative";
			if (th != th)
				return "The threshold damage can't be NaN";
		}
		String error = changeTool(shield, newType, newDamage, newName, newDisplayName, newLore, newAttributes,
				newEnchantments, allowEnchanting, allowAnvil, repairItem, newDurability, newImage, itemFlags,
				entityHitDurabilityLoss, blockBreakDurabilityLoss, newCustomModel, false);
		if (error != null) {
			return error;
		} else {
			shield.setThresholdDamage(thresholdDamage);
			shield.setBlockingModel(newCustomBlockingModel);
			return null;
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
			if (item.allowAnvilActions() && item.getDisplayName().contains("�"))
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
	
	public String addTrident(CustomTrident trident, boolean checkClass) {
		if (!bypassChecks()) {
			if (trident == null)
				return "Can't add null items";
			if (checkClass && trident.getClass() != CustomTrident.class)
				return "Use the appropriate method for that class";
			if (trident.throwDurabilityLoss < 0)
				return "The throw durability loss must be a positive integer";
			if (trident.throwDamageMultiplier < 0)
				return "The throw damage multiplier must be a positive number";
			if (trident.speedMultiplier < 0)
				return "The speed multiplier must be a positive number";
		}
		return addTool(trident, false);
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
			byte[] newCustomModel, boolean checkClass) {
		if (!bypassChecks()) {
			if (checkClass && item.getClass() != CustomTool.class)
				return "Use the appropriate method to change this class";
			if (allowAnvil && newDisplayName.contains("�"))
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
				newEnchantments, newImage, itemFlags, newCustomModel,
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
			byte[] newCustomModel, boolean checkClass) {
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
				entityHitDurabilityLoss, blockBreakDurabilityLoss, newCustomModel, false);
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
			byte[] newCustomModel, boolean checkClass) {
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
				entityHitDurabilityLoss, blockBreakDurabilityLoss, newCustomModel, false);
		if (error == null) {
			hoe.setTillDurabilityLoss(tillDurabilityLoss);
			return null;
		} else {
			return error;
		}
	}
	
	public String changeTrident(CustomTrident trident, CustomItemType newType, short newDamage, 
			String newName, String newDisplayName, String[] newLore, AttributeModifier[] newAttributes, 
			Enchantment[] newEnchantments, boolean allowEnchanting, boolean allowAnvil, 
			double throwDamageMultiplier, double throwSpeedMultiplier, Ingredient repairItem, 
			long newDurability, NamedImage newImage, boolean[] itemFlags, int entityHitDurabilityLoss, 
			int blockBreakDurabilityLoss, int throwDurabilityLoss, byte[] newCustomModel, 
			byte[] newCustomInHandModel, byte[] newCustomThrowingModel, boolean checkClass) {
		if (!bypassChecks()) {
			if (trident == null)
				return "Can't change null items";
			if (checkClass && trident.getClass() != CustomTrident.class)
				return "Use the appropriate method for that class";
			if (throwDurabilityLoss < 0)
				return "The throw durability loss must be a positive integer";
			if (throwDamageMultiplier < 0)
				return "The throw damage multiplier must be a positive number";
			if (throwSpeedMultiplier < 0)
				return "The speed multiplier must be a positive number";
		}
		
		String error = changeTool(trident, newType, newDamage, newName, newDisplayName, newLore, newAttributes,
				newEnchantments, allowEnchanting, allowAnvil, repairItem, newDurability, newImage, itemFlags,
				entityHitDurabilityLoss, blockBreakDurabilityLoss, newCustomModel, false);
		if (error == null) {
			trident.throwDurabilityLoss = throwDurabilityLoss;
			trident.throwDamageMultiplier = throwDamageMultiplier;
			trident.speedMultiplier = throwSpeedMultiplier;
			trident.customInHandModel = newCustomInHandModel;
			trident.customThrowingModel = newCustomThrowingModel;
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
			for (CustomItem current : items)
				if (current.getName().equals(item.getName()))
					return "There is already a custom item with that name";
			if (!isItemDamageTypeFree(item.getItemType(), item.getItemDamage(), item))
				return "There is already a custom item or projectile cover with the same internal item type and damage";
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
			byte[] newCustomModel, boolean checkClass) {
		if (!bypassChecks()) {
			if (checkClass && item.getClass() != SimpleCustomItem.class)
				return "Use the right method for the class";
			if (newStacksize < 1 || newStacksize > 64)
				return "The maximum stacksize (" + newStacksize + ") is out of range";
		}
		String error = changeItem(item, newType, newDamage, newName, newDisplayName, newLore, newAttributes,
				newEnchantments, newImage, newItemFlags, newCustomModel, false);
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
			byte[] newCustomModel, boolean checkClass) {
		if (!bypassChecks()) {
			if (item == null)
				return "Can't change null items";
			if (checkClass && item.getClass() != CustomItem.class)
				return "Use the appropriate method to change that class";
			String nameError = checkName(newName);
			if (nameError != null)
				return nameError;
			CustomItem sameName = getCustomItemByName(newName);
			if (sameName != null && sameName != item)
				return "There is already another item with that name";
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
			if (!items.contains(item))
				return "There is no previous item!";
			if (!isItemDamageTypeFree(newType, newDamage, item))
				return "There is another custom item or projectile cover with the same internal item type and damage";
			if (!textures.contains(newImage))
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
		item.setCustomModel(newCustomModel);
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
			for (CustomItem current : items) {
				if (current instanceof CustomTool) {
					CustomTool tool = (CustomTool) current;
					if (tool.getRepairItem() instanceof CustomItemIngredient) {
						CustomItemIngredient ingredient = (CustomItemIngredient) tool.getRepairItem();
						if (ingredient.getItem() == item) {
							return "The tool " + tool.getName() + " has this item as repair item.";
						}
					}
				}
			}
			for (EntityDrop drop : mobDrops) {
				if (drop.getDrop().getItemToDrop() == item) {
					return "There is a mob drop for " + drop.getEntityType() + " that drops this item.";
				}
			}
			for (BlockDrop drop : blockDrops) {
				if (drop.getDrop().getItemToDrop() == item) {
					return "There is a block drop for " + drop.getBlock() + " that drops this item.";
				}
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
			if (!blockDrops.contains(old)) {
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
	
	private String addProjectileCover(ProjectileCover cover) {
		if (!bypassChecks()) {
			if (cover == null)
				return "The projectile cover can't be null";
			String nameError = checkName(cover.name);
			if (nameError != null)
				return nameError;
			if (cover.itemType == null)
				return "The internal item type can't be null";
			if (!cover.itemType.canServe(Category.PROJECTILE_COVER))
				return "The selected internal item type can't be used as projectile cover";
			if (cover.itemDamage <= 0)
				return "The internal item damage must be positive";
			if (cover.itemDamage > cover.itemType.getMaxDurability())
				return "The internal item damage can't be greater than the maximum durability " + cover.itemType.getMaxDurability();
			if (!isItemDamageTypeFree(cover.itemType, cover.itemDamage, cover))
				return "There is already a custom item or projectile cover with the same internal item type and damage";
			if (hasProjectileCover(cover.name))
				return "There is already a projectile cover with that name";
			if (projectileCovers.contains(cover))
				return "That projectile cover is already in the list of projectile covers";
		}
		projectileCovers.add(cover);
		return null;
	}
	
	private String changeProjectileCover(ProjectileCover original, CustomItemType newType, short newDamage,
			String newName) {
		if (!bypassChecks()) {
			if (original == null)
				return "Can't change null projectile covers";
			if (!projectileCovers.contains(original))
				return "The projectile cover to change is not in the list of projectile covers";
			if (newType == null)
				return "The internal item type can't be null";
			if (!newType.canServe(Category.PROJECTILE_COVER))
				return "This internal item type can't be used as projectile cover";
			if (newDamage <= 0)
				return "The internal item damage must be a positive integer";
			if (newDamage > newType.getMaxDurability())
				return "The internal item damage can't be greater than the maximum durability " + newType.getMaxDurability();
			String nameError = checkName(newName);
			if (nameError != null)
				return nameError;
			ProjectileCover sameName = getProjectileCoverByName(newName);
			if (sameName != null && sameName != original)
				return "There is another projectile cover with that name";
		}
		
		original.itemType = newType;
		original.itemDamage = newDamage;
		original.name = newName;
		return null;
	}
	
	/**
	 * Attempts to change the properties of the given sphere projectile cover to the given values.
	 * If the given values are valid, the properties of the given projectile cover will be set to those
	 * values.
	 * If not, the projectile cover will not be changed and a human-readable reason why the values are
	 * invalid will be returned.
	 * @param original The old projectile cover
	 * @param newType The new internal item type
	 * @param newDamage The new internal item damage
	 * @param newName The new name
	 * @param newTexture The new texture
	 * @param newSlotsPerAxis The new value for the slotsPerAxis field
	 * @param newScale The new scale
	 * @return null if the cover was changed successfully, or the reason it wasn't
	 */
	public String changeSphereProjectileCover(SphereProjectileCover original, CustomItemType newType, 
			short newDamage, String newName, NamedImage newTexture, int newSlotsPerAxis, double newScale) {
		
		if (!bypassChecks()) {
			if (newTexture == null)
				return "You must select a texture";
			if (!textures.contains(newTexture))
				return "The selected texture is not in the list of textures";
			if (newSlotsPerAxis <= 0)
				return "The slots per axis must be a positive integer";
			if (newSlotsPerAxis > 50)
				return "The slots per axis can't be larger than 50";
			if (!(newScale > 0))
				return "The scale must be greater than zero";
		}
		
		String error = changeProjectileCover(original, newType, newDamage, newName);
		if (error == null) {
			original.texture = newTexture;
			original.slotsPerAxis = newSlotsPerAxis;
			original.scale = newScale;
		}
		return error;
	}
	
	/**
	 * Attempts to add the given projectile cover to this item set. 
	 * If the given cover can be added, it will be added and this method will return null.
	 * If the given cover can't be added, it won't be added and this method will return a String
	 * containing a human-readable message why it couldn't be added.
	 * @param cover The projectile cover to add to this item set
	 * @return null if added successfully, or the reason it was not added successfully
	 */
	public String addSphereProjectileCover(SphereProjectileCover cover) {
		if (!bypassChecks()) {
			if (cover == null)
				return "Can't add null covers";
			if (cover.texture == null)
				return "You must select a texture";
			if (!textures.contains(cover.texture))
				return "The selected texture is not in the textures list";
			
			// This is NOT equivalent to <= 0.0 because this also handles NaN
			if (!(cover.scale > 0.0))
				return "The scale must be greater than 0";
			
			if (cover.slotsPerAxis <= 0)
				return "The slots per axis must be a positive integer";
			if (cover.slotsPerAxis > 50)
				return "The slots per axis must not be larger than 50 (would get expensive)";
		}
		return addProjectileCover(cover);
	}
	
	/**
	 * Attempts to change the properties of the given custom projectile cover to the given values. If those
	 * values are valid, the properties of the projectile cover will be changed to those values and this
	 * method will return null.
	 * If not, the projectile cover won't be changed and a human-readable reason why they aren't valid
	 * will be returned.
	 * @param original The projectile cover to change
	 * @param newType The new internal item type
	 * @param newDamage The new internal item damage
	 * @param newName The new name
	 * @param newModel The new item model
	 * @return null if the projectile cover was changed successfully, or the reason it wasn't
	 */
	public String changeCustomProjectileCover(CustomProjectileCover original, CustomItemType newType, 
			short newDamage, String newName, byte[] newModel) {
		
		if (!bypassChecks()) {
			if (newModel == null)
				return "You must select a model";
		}
		
		String error = changeProjectileCover(original, newType, newDamage, newName);
		if (error == null) {
			original.model = newModel;
		}
		return error;
	}
	
	/**
	 * Attempts to add the given projectile cover to this item set. 
	 * If the given cover can be added, it will be added and this method will return null.
	 * If the given cover can't be added, it won't be added and this method will return a String
	 * containing a human-readable message why it couldn't be added.
	 * @param cover The projectile cover to add to this item set
	 * @return null if added successfully, or the reason it was not added successfully
	 */
	public String addCustomProjectileCover(CustomProjectileCover cover) {
		if (!bypassChecks()) {
			if (cover == null)
				return "Can't add null covers";
			if (cover.model == null)
				return "You must select a model";
		}
		return addProjectileCover(cover);
	}

	/**
	 * Do not modify this collection directly!
	 * 
	 * @return The CustomItem collection of this ItemSet
	 */
	public Collection<CustomItem> getBackingItems() {
		return items;
	}

	/**
	 * Do not modify this collection directly!
	 * 
	 * @return The NamedImage collection of this ItemSet
	 */
	public Collection<NamedImage> getBackingTextures() {
		return textures;
	}

	/**
	 * Do not modify this collection directly!
	 * 
	 * @return The Recipe collection of this ItemSet
	 */
	public Collection<Recipe> getBackingRecipes() {
		return recipes;
	}
	
	/**
	 * Do not modify this collection directly!
	 * @return The mob drop collection of this ItemSet
	 */
	public Collection<EntityDrop> getBackingMobDrops(){
		return mobDrops;
	}
	
	/**
	 * Do not modify this collection directly!
	 * @return The block drop collection of this ItemSet
	 */
	public Collection<BlockDrop> getBackingBlockDrops(){
		return blockDrops;
	}

	public short nextAvailableDamage(CustomItemType type, CustomItem exclude) {
		boolean[] usedDamage = new boolean[type.getMaxDurability() - 1];
		for (CustomItem item : items)
			if (item != exclude && item.getItemType() == type)
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
	
	public NamedImage getTextureByName(String name) {
		for (NamedImage texture : textures) {
			if (texture.getName().equals(name)) {
				return texture;
			}
		}
		return null;
	}
	
	public ProjectileCover getProjectileCoverByName(String name) {
		for (ProjectileCover cover : projectileCovers)
			if (cover.name.equals(name))
				return cover;
		return null;
	}
	
	public boolean hasCustomItem(String name) {
		return getCustomItemByName(name) != null;
	}
	
	public boolean hasTexture(String name) {
		return getTextureByName(name) != null;
	}
	
	public boolean hasProjectileCover(String name) {
		return getProjectileCoverByName(name) != null;
	}
	
	private boolean isItemDamageTypeFree(CustomItemType type, short damage, ItemDamageClaim exclude) {
		for (CustomItem item : items)
			if (item != exclude && item.getItemType() == type && item.getItemDamage() == damage)
				return false;
		for (ProjectileCover cover : projectileCovers)
			if (cover != exclude && cover.getItemType() == type && cover.getItemDamage() == damage)
				return false;
		return true;
	}
}