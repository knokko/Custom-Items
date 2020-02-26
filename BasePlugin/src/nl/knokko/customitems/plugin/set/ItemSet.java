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
package nl.knokko.customitems.plugin.set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.customitems.damage.DamageResistances;
import nl.knokko.customitems.drops.*;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.encoding.SetEncoding;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.AttributeModifier.Attribute;
import nl.knokko.customitems.item.AttributeModifier.Operation;
import nl.knokko.customitems.item.AttributeModifier.Slot;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.CustomToolDurability;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.EnchantmentType;
import nl.knokko.customitems.item.ItemFlag;
import nl.knokko.customitems.item.ItemSetBase;
import nl.knokko.customitems.item.WandCharges;
import nl.knokko.customitems.plugin.recipe.*;
import nl.knokko.customitems.plugin.recipe.ingredient.*;
import nl.knokko.customitems.plugin.set.item.*;
import nl.knokko.customitems.projectile.CIProjectile;
import nl.knokko.customitems.projectile.ProjectileCover;
import nl.knokko.util.bits.BitInput;
import nl.knokko.customitems.effect.EffectType;
import nl.knokko.customitems.effect.PotionEffect;

public class ItemSet implements ItemSetBase {

	private CustomRecipe[] recipes;

	private final Map<CIMaterial, Map<Short, ItemDamageClaim>> customItemMap;

	private CustomItem[] items;
	
	private ProjectileCover[] projectileCovers;
	private CIProjectile[] projectiles;
	
	private Drop[][] blockDropMap;
	private EntityDrop[][] mobDropMap;

	public ItemSet() {
		customItemMap = new EnumMap<>(CIMaterial.class);
		items = new CustomItem[0];
		recipes = new CustomRecipe[0];
		
		projectileCovers = new ProjectileCover[0];
		projectiles = new CIProjectile[0];
		
		blockDropMap = new Drop[BlockType.AMOUNT][0];
		mobDropMap = new EntityDrop[CIEntityType.AMOUNT][0];
	}

	public ItemSet(BitInput input) {
		customItemMap = new EnumMap<>(CIMaterial.class);
		byte encoding = input.readByte();
		
		// Note that ENCODING_2 and ENCODING_4 are editor-only
		if (encoding == SetEncoding.ENCODING_1)
			load1(input);
		else if (encoding == SetEncoding.ENCODING_3)
			load3(input);
		else if (encoding == SetEncoding.ENCODING_5)
			load5(input);
		else
			throw new IllegalArgumentException("Unknown item set encoding: " + encoding);
	}

	private void load1(BitInput input) {
		// Items
		int itemSize = input.readInt();
		items = new CustomItem[itemSize];
		for (int counter = 0; counter < itemSize; counter++)
			register(loadItem(input), counter);

		// Recipes
		int recipeAmount = input.readInt();
		recipes = new CustomRecipe[recipeAmount];
		for (int counter = 0; counter < recipeAmount; counter++)
			register(loadRecipe(input), counter);
		
		// There are no drops in this encoding
		blockDropMap = new Drop[BlockType.AMOUNT][0];
		mobDropMap = new EntityDrop[CIEntityType.AMOUNT][0];
	}

	// Just like the ItemSet of Editor doesn't have export2, this doesn't have load2

	private void load3(BitInput input) {
		
		// Items
		int itemSize = input.readInt();
		items = new CustomItem[itemSize];
		for (int counter = 0; counter < itemSize; counter++)
			register(loadItem(input), counter);

		// Recipes
		int recipeAmount = input.readInt();
		recipes = new CustomRecipe[recipeAmount];
		for (int counter = 0; counter < recipeAmount; counter++)
			register(loadRecipe(input), counter);
		
		int numBlockDrops = input.readInt();
		blockDropMap = new Drop[BlockType.AMOUNT][0];
		for (int counter = 0; counter < numBlockDrops; counter++)
			register(BlockDrop.load(input, this));
		
		int numMobDrops = input.readInt();
		mobDropMap = new EntityDrop[CIEntityType.AMOUNT][0];
		for (int counter = 0; counter < numMobDrops; counter++)
			register(EntityDrop.load(input, this));
	}
	
	// Since ENCODING_4 is editor-only, there is no load4 method
	
	private void load5(BitInput input) {
		
		// Projectiles
		int numProjectileCovers = input.readInt();
		projectileCovers = new ProjectileCover[numProjectileCovers];
		for (int index = 0; index < numProjectileCovers; index++) {
			PluginProjectileCover cover = new PluginProjectileCover(input);
			projectileCovers[index] = cover;
			registerItemDamageClaim(cover);
		}
		
		int numProjectiles = input.readInt();
		projectiles = new CIProjectile[numProjectiles];
		for (int index = 0; index < numProjectiles; index++)
			projectiles[index] = CIProjectile.fromBits(input, this);
		
		// Notify the projectiles that all projectiles are loaded
		for (CIProjectile projectile : projectiles)
			projectile.afterProjectilesAreLoaded(this);
		
		// Items
		int itemSize = input.readInt();
		items = new CustomItem[itemSize];
		for (int counter = 0; counter < itemSize; counter++)
			register(loadItem(input), counter);

		// Recipes
		int recipeAmount = input.readInt();
		recipes = new CustomRecipe[recipeAmount];
		for (int counter = 0; counter < recipeAmount; counter++)
			register(loadRecipe(input), counter);
		
		int numBlockDrops = input.readInt();
		blockDropMap = new Drop[BlockType.AMOUNT][0];
		for (int counter = 0; counter < numBlockDrops; counter++)
			register(BlockDrop.load(input, this));
		
		int numMobDrops = input.readInt();
		mobDropMap = new EntityDrop[CIEntityType.AMOUNT][0];
		for (int counter = 0; counter < numMobDrops; counter++)
			register(EntityDrop.load(input, this));
	}

	private CustomItem loadItem(BitInput input) {
		byte encoding = input.readByte();
		switch (encoding) {
		case ItemEncoding.ENCODING_SIMPLE_1 : return loadSimpleItem1(input);
		case ItemEncoding.ENCODING_SIMPLE_2 : return loadSimpleItem2(input);
		case ItemEncoding.ENCODING_SIMPLE_3 : return loadSimpleItem3(input);
		case ItemEncoding.ENCODING_SIMPLE_4 : return loadSimpleItem4(input);
		case ItemEncoding.ENCODING_SIMPLE_5 : return loadSimpleItem5(input);
		case ItemEncoding.ENCODING_SIMPLE_6 : return loadSimpleItem6(input);
		case ItemEncoding.ENCODING_TOOL_2 : return loadTool2(input);
		case ItemEncoding.ENCODING_TOOL_3 : return loadTool3(input);
		case ItemEncoding.ENCODING_TOOL_4 : return loadTool4(input);
		case ItemEncoding.ENCODING_TOOL_5 : return loadTool5(input);
		case ItemEncoding.ENCODING_TOOL_6 : return loadTool6(input);
		case ItemEncoding.ENCODING_SHEAR_5 : return loadShear5(input);
		case ItemEncoding.ENCODING_SHEAR_6 : return loadShear6(input);
		case ItemEncoding.ENCODING_BOW_3 : return loadBow3(input);
		case ItemEncoding.ENCODING_BOW_4 : return loadBow4(input);
		case ItemEncoding.ENCODING_BOW_5 : return loadBow5(input);
		case ItemEncoding.ENCODING_BOW_6 : return loadBow6(input);
		case ItemEncoding.ENCODING_ARMOR_4 : return loadArmor4(input);
		case ItemEncoding.ENCODING_ARMOR_5 : return loadArmor5(input);
		case ItemEncoding.ENCODING_ARMOR_6 : return loadArmor6(input);
		case ItemEncoding.ENCODING_ARMOR_7 : return loadArmor7(input);
		case ItemEncoding.ENCODING_ARMOR_8 : return loadArmor8(input);
		case ItemEncoding.ENCODING_SHIELD_6 : return loadShield6(input);
		case ItemEncoding.ENCODING_SHIELD_7 : return loadShield7(input);
		case ItemEncoding.ENCODING_TRIDENT_7 : return loadTrident7(input);
		case ItemEncoding.ENCODING_TRIDENT_8 : return loadTrident8(input);
		case ItemEncoding.ENCODING_HOE_5 : return loadHoe5(input);
		case ItemEncoding.ENCODING_HOE_6 : return loadHoe6(input);
		case ItemEncoding.ENCODING_WAND_8: return loadWand8(input);
		default : throw new IllegalArgumentException("Unknown encoding: " + encoding);
	}
	}

	private CustomItem loadSimpleItem1(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[0];
		return new SimpleCustomItem(itemType, damage, name, displayName, lore, attributes, new Enchantment[0], 64,
				ItemFlag.getDefaultValues(), new ArrayList<PotionEffect>(), new ArrayList<PotionEffect>(), new String[] {});
	}

	private CustomItem loadSimpleItem2(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		return new SimpleCustomItem(itemType, damage, name, displayName, lore, attributes, new Enchantment[0], 64,
				ItemFlag.getDefaultValues(), new ArrayList<PotionEffect>(), new ArrayList<PotionEffect>(), new String[] {});
	}

	private CustomItem loadSimpleItem3(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		return new SimpleCustomItem(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, 64,
				ItemFlag.getDefaultValues(), new ArrayList<PotionEffect>(), new ArrayList<PotionEffect>(), new String[] {});
	}

	private CustomItem loadSimpleItem4(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		int stackSize = input.readByte();
		return new SimpleCustomItem(itemType, damage, name, displayName, lore, attributes, defaultEnchantments,
				stackSize, ItemFlag.getDefaultValues(), new ArrayList<PotionEffect>(), new ArrayList<PotionEffect>(), new String[] {});
	}

	private CustomItem loadSimpleItem5(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		int stackSize = input.readByte();
		boolean[] itemFlags = input.readBooleans(6);
		return new SimpleCustomItem(itemType, damage, name, displayName, lore, attributes, defaultEnchantments,
				stackSize, itemFlags, new ArrayList<PotionEffect>(), new ArrayList<PotionEffect>(), new String[] {});
	}
	
	private CustomItem loadSimpleItem6(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		int stackSize = input.readByte();
		boolean[] itemFlags = input.readBooleans(6);
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		return new SimpleCustomItem(itemType, damage, name, displayName, lore, attributes, defaultEnchantments,
				stackSize, itemFlags, playerEffects, targetEffects, commands);
	}

	private CustomItem loadTool2(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		int durability = input.readInt();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		return new CustomTool(itemType, damage, name, displayName, lore, attributes, new Enchantment[0], durability,
				allowEnchanting, allowAnvil, new NoIngredient(), ItemFlag.getDefaultValues(),
				CustomToolDurability.defaultEntityHitDurabilityLoss(itemType),
				CustomToolDurability.defaultBlockBreakDurabilityLoss(itemType), 
				new ArrayList<PotionEffect>(), new ArrayList<PotionEffect>(), new String[] {});
	}

	private CustomItem loadTool3(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		int durability = input.readInt();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient(input);
		return new CustomTool(itemType, damage, name, displayName, lore, attributes, new Enchantment[0], durability,
				allowEnchanting, allowAnvil, repairItem, ItemFlag.getDefaultValues(),
				CustomToolDurability.defaultEntityHitDurabilityLoss(itemType),
				CustomToolDurability.defaultBlockBreakDurabilityLoss(itemType), 
				new ArrayList<PotionEffect>(), new ArrayList<PotionEffect>(), new String[] {});
	}

	private CustomItem loadTool4(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient(input);
		return new CustomTool(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, durability,
				allowEnchanting, allowAnvil, repairItem, ItemFlag.getDefaultValues(),
				CustomToolDurability.defaultEntityHitDurabilityLoss(itemType),
				CustomToolDurability.defaultBlockBreakDurabilityLoss(itemType), 
				new ArrayList<PotionEffect>(), new ArrayList<PotionEffect>(), new String[] {});
	}

	private CustomItem loadTool5(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient(input);
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		return new CustomTool(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, durability,
				allowEnchanting, allowAnvil, repairItem, itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				new ArrayList<PotionEffect>(), new ArrayList<PotionEffect>(), new String[] {});
	}
	
	private CustomItem loadTool6(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient(input);
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		return new CustomTool(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, durability,
				allowEnchanting, allowAnvil, repairItem, itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				playerEffects, targetEffects, commands);
	}
	
	
	
	private CustomItem loadHoe5(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient(input);
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int tillDurabilityLoss = input.readInt();
		return new CustomHoe(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, durability,
				allowEnchanting, allowAnvil, repairItem, itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				tillDurabilityLoss, new ArrayList<PotionEffect>(), new ArrayList<PotionEffect>(), new String[] {});
	}
	
	private CustomItem loadHoe6(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient(input);
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int tillDurabilityLoss = input.readInt();
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		return new CustomHoe(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, durability,
				allowEnchanting, allowAnvil, repairItem, itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				tillDurabilityLoss, playerEffects, targetEffects, commands);
	}
	
	private CustomItem loadShear5(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient(input);
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int shearDurabilityLoss = input.readInt();
		return new CustomShears(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, durability,
				allowEnchanting, allowAnvil, repairItem, itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				shearDurabilityLoss, new ArrayList<PotionEffect>(), new ArrayList<PotionEffect>(), new String[] {});
	}
	
	private CustomItem loadShear6(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient(input);
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int shearDurabilityLoss = input.readInt();
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		return new CustomShears(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, durability,
				allowEnchanting, allowAnvil, repairItem, itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				shearDurabilityLoss, playerEffects, targetEffects, commands);
	}

	private CustomItem loadArmor4(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient(input);
		Color color;
		if (itemType.isLeatherArmor()) {
			color = Color.fromRGB(input.readByte() & 0xFF, input.readByte() & 0xFF, input.readByte() & 0xFF);
		} else {
			color = null;
		}
		return new CustomArmor(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, durability,
				allowEnchanting, allowAnvil, repairItem, color, ItemFlag.getDefaultValues(), 0, 0,
				new DamageResistances(), new ArrayList<PotionEffect>(), new ArrayList<PotionEffect>(), new String[] {});
	}

	private CustomItem loadArmor5(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient(input);
		Color color;
		if (itemType.isLeatherArmor()) {
			color = Color.fromRGB(input.readByte() & 0xFF, input.readByte() & 0xFF, input.readByte() & 0xFF);
		} else {
			color = null;
		}
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		return new CustomArmor(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, durability,
				allowEnchanting, allowAnvil, repairItem, color, itemFlags, entityHitDurabilityLoss,
				blockBreakDurabilityLoss, new DamageResistances(), new ArrayList<PotionEffect>(), 
				new ArrayList<PotionEffect>(), new String[] {});
	}

	private CustomItem loadArmor6(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient(input);
		Color color;
		if (itemType.isLeatherArmor()) {
			color = Color.fromRGB(input.readByte() & 0xFF, input.readByte() & 0xFF, input.readByte() & 0xFF);
		} else {
			color = null;
		}
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		DamageResistances damageResistances = DamageResistances.load12(input);
		return new CustomArmor(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, durability,
				allowEnchanting, allowAnvil, repairItem, color, itemFlags, entityHitDurabilityLoss,
				blockBreakDurabilityLoss, damageResistances, new ArrayList<PotionEffect>(), 
				new ArrayList<PotionEffect>(), new String[] {});
	}
	
	private CustomItem loadArmor7(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient(input);
		Color color;
		if (itemType.isLeatherArmor()) {
			color = Color.fromRGB(input.readByte() & 0xFF, input.readByte() & 0xFF, input.readByte() & 0xFF);
		} else {
			color = null;
		}
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		DamageResistances damageResistances = DamageResistances.load14(input);
		return new CustomArmor(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, durability,
				allowEnchanting, allowAnvil, repairItem, color, itemFlags, entityHitDurabilityLoss,
				blockBreakDurabilityLoss, damageResistances, new ArrayList<PotionEffect>(), 
				new ArrayList<PotionEffect>(), new String[] {});
	}
	
	private CustomItem loadArmor8(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient(input);
		Color color;
		if (itemType.isLeatherArmor()) {
			color = Color.fromRGB(input.readByte() & 0xFF, input.readByte() & 0xFF, input.readByte() & 0xFF);
		} else {
			color = null;
		}
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		DamageResistances damageResistances = DamageResistances.load14(input);
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		return new CustomArmor(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, durability,
				allowEnchanting, allowAnvil, repairItem, color, itemFlags, entityHitDurabilityLoss,
				blockBreakDurabilityLoss, damageResistances, playerEffects, targetEffects, commands);
	}

	private CustomBow loadBow3(BitInput input) {
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
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
		Ingredient repairItem = loadIngredient(input);
		return new CustomBow(damage, name, displayName, lore, attributes, new Enchantment[0], durability,
				damageMultiplier, speedMultiplier, knockbackStrength, gravity, allowEnchanting, allowAnvil, repairItem,
				ItemFlag.getDefaultValues(), 0, 0, 1, new ArrayList<PotionEffect>(), new ArrayList<PotionEffect>(), new String[] {});
	}

	private CustomBow loadBow4(BitInput input) {
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
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
		Ingredient repairItem = loadIngredient(input);
		return new CustomBow(damage, name, displayName, lore, attributes, defaultEnchantments, durability,
				damageMultiplier, speedMultiplier, knockbackStrength, gravity, allowEnchanting, allowAnvil, repairItem,
				ItemFlag.getDefaultValues(), 0, 0, 1, new ArrayList<PotionEffect>(), new ArrayList<PotionEffect>(), new String[] {});
	}

	private CustomBow loadBow5(BitInput input) {
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
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
		Ingredient repairItem = loadIngredient(input);
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int shootDurabilityLoss = input.readInt();
		return new CustomBow(damage, name, displayName, lore, attributes, defaultEnchantments, durability,
				damageMultiplier, speedMultiplier, knockbackStrength, gravity, allowEnchanting, allowAnvil, repairItem,
				itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, shootDurabilityLoss, 
				new ArrayList<PotionEffect>(), new ArrayList<PotionEffect>(), new String[] {});
	}
	
	private CustomBow loadBow6(BitInput input) {
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
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
		Ingredient repairItem = loadIngredient(input);
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int shootDurabilityLoss = input.readInt();
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		return new CustomBow(damage, name, displayName, lore, attributes, defaultEnchantments, durability,
				damageMultiplier, speedMultiplier, knockbackStrength, gravity, allowEnchanting, allowAnvil, repairItem,
				itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, shootDurabilityLoss, 
				playerEffects, targetEffects, commands);
	}
	
	private CustomItem loadShield6(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient(input);
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		double durabilityThreshold = input.readDouble();
		return new CustomShield(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, durability,
				allowEnchanting, allowAnvil, repairItem, itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, durabilityThreshold, 
				new ArrayList<PotionEffect>(), new ArrayList<PotionEffect>(), new String[] {});
	}
	
	private CustomItem loadShield7(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient(input);
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		double durabilityThreshold = input.readDouble();
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		return new CustomShield(itemType, damage, name, displayName, lore, attributes, defaultEnchantments, durability,
				allowEnchanting, allowAnvil, repairItem, itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, durabilityThreshold, 
				playerEffects, targetEffects, commands);
	}
	
	private CustomItem loadTrident7(BitInput input) {
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient(input);
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int throwDurabilityLoss = input.readInt();
		double throwDamageMultiplier = input.readDouble();
		double speedMultiplier = input.readDouble();
		return new CustomTrident(damage, name, displayName, lore, attributes, defaultEnchantments, 
				durability, allowEnchanting, allowAnvil, throwDamageMultiplier, speedMultiplier, repairItem, 
				itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, throwDurabilityLoss, 
				new ArrayList<PotionEffect>(), new ArrayList<PotionEffect>(), new String[] {});
	}
	
	private CustomItem loadTrident8(BitInput input) {
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient(input);
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int throwDurabilityLoss = input.readInt();
		double throwDamageMultiplier = input.readDouble();
		double speedMultiplier = input.readDouble();
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		return new CustomTrident(damage, name, displayName, lore, attributes, defaultEnchantments, 
				durability, allowEnchanting, allowAnvil, throwDamageMultiplier, speedMultiplier, repairItem, 
				itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, throwDurabilityLoss, 
				playerEffects, targetEffects, commands);
	}
	
	private CustomItem loadWand8(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		boolean[] itemFlags = input.readBooleans(6);
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		
		String projectileName = input.readString();
		int cooldown = input.readInt();
		WandCharges charges;
		if (input.readBoolean())
			charges = WandCharges.fromBits(input);
		else
			charges = null;
		int amountPerShot = input.readInt();
		
		CIProjectile projectile = getProjectileByName(projectileName);
		
		return new CustomWand(itemType, damage, name, displayName, lore, attributes, defaultEnchantments,
				itemFlags, playerEffects, targetEffects, commands,
				projectile, cooldown, charges, amountPerShot);
	}

	private AttributeModifier loadAttribute2(BitInput input) {
		return new AttributeModifier(Attribute.valueOf(input.readJavaString()), Slot.valueOf(input.readJavaString()),
				Operation.values()[(int) input.readNumber((byte) 2, false)], input.readDouble());
	}

	private void register(CustomItem item, int index) {
		items[index] = item;
		registerItemDamageClaim(item);
	}
	
	private void registerItemDamageClaim(ItemDamageClaim item) {
		Map<Short, ItemDamageClaim> map = customItemMap.get(item.getMaterial());
		if (map == null) {
			map = new HashMap<>();
			customItemMap.put(item.getMaterial(), map);
		}
		map.put(item.getItemDamage(), item);
	}

	private CustomRecipe loadRecipe(BitInput input) {
		byte encoding = input.readByte();
		ItemStack result = loadResult(input);
		if (encoding == RecipeEncoding.SHAPED_RECIPE)
			return loadShapedRecipe(input, result);
		if (encoding == RecipeEncoding.SHAPELESS_RECIPE)
			return loadShapelessRecipe(input, result);
		throw new IllegalArgumentException("Unknown recipe encoding: " + encoding);
	}

	private CustomRecipe loadShapelessRecipe(BitInput input, ItemStack result) {
		Ingredient[] ingredients = new Ingredient[(int) input.readNumber((byte) 4, false)];
		for (int index = 0; index < ingredients.length; index++)
			ingredients[index] = loadIngredient(input);
		return new ShapelessCustomRecipe(ingredients, result);
	}

	private CustomRecipe loadShapedRecipe(BitInput input, ItemStack result) {
		Ingredient[] ingredients = new Ingredient[9];
		for (int index = 0; index < ingredients.length; index++)
			ingredients[index] = loadIngredient(input);
		return new ShapedCustomRecipe(result, ingredients);
	}

	@SuppressWarnings("deprecation")
	private ItemStack loadResult(BitInput input) {
		byte encoding = input.readByte();
		byte amount = (byte) (1 + input.readNumber((byte) 6, false));
		if (encoding == RecipeEncoding.Result.VANILLA_SIMPLE)
			return ItemHelper.createStack(input.readJavaString(), amount);
		if (encoding == RecipeEncoding.Result.VANILLA_DATA) {
			ItemStack stack = ItemHelper.createStack(input.readJavaString(), amount);
			MaterialData data = stack.getData();
			data.setData((byte) input.readNumber((byte) 4, false));
			stack.setData(data);
			stack.setDurability(data.getData());
			return stack;
		}
		if (encoding == RecipeEncoding.Result.VANILLA_ADVANCED_1)
			throw new UnsupportedOperationException("Advanced vanilla results are not yet supported");
		if (encoding == RecipeEncoding.Result.CUSTOM)
			return getItem(input.readJavaString()).create(amount);
		throw new IllegalArgumentException("Unknown result encoding: " + encoding);
	}

	private Ingredient loadIngredient(BitInput input) {
		byte encoding = input.readByte();
		if (encoding == RecipeEncoding.Ingredient.NONE)
			return new NoIngredient();
		if (encoding == RecipeEncoding.Ingredient.VANILLA_SIMPLE)
			return new SimpleVanillaIngredient(CIMaterial.valueOf(input.readJavaString()));
		if (encoding == RecipeEncoding.Ingredient.VANILLA_DATA)
			return new DataVanillaIngredient(CIMaterial.valueOf(input.readJavaString()),
					(byte) input.readNumber((byte) 4, false));
		if (encoding == RecipeEncoding.Ingredient.VANILLA_ADVANCED_1)
			throw new UnsupportedOperationException("Advanced vanilla ingredients are not yet supported.");
		if (encoding == RecipeEncoding.Ingredient.CUSTOM)
			return new CustomIngredient(getItem(input.readJavaString()));
		throw new IllegalArgumentException("Unknown ingredient encoding: " + encoding);
	}

	private void register(CustomRecipe recipe, int index) {
		recipes[index] = recipe;
	}
	
	private void register(BlockDrop drop) {
		Drop[] old = blockDropMap[drop.getBlock().ordinal()];
		Drop[] newDrops = Arrays.copyOf(old, old.length + 1);
		newDrops[old.length] = drop.getDrop();
		blockDropMap[drop.getBlock().ordinal()] = newDrops;
	}
	
	private static final Drop[] NO_DROPS = {};
	
	public Drop[] getDrops(CIMaterial block) {
		if (block == null)
			return NO_DROPS;
		BlockType blockType = BlockType.fromBukkitMaterial(block);
		if (blockType != null) {
			return blockDropMap[blockType.ordinal()];
		} else {
			return NO_DROPS;
		}
	}
	
	private void register(EntityDrop drop) {
		EntityDrop[] old = mobDropMap[drop.getEntityType().ordinal()];
		EntityDrop[] newDrops = Arrays.copyOf(old, old.length + 1);
		newDrops[old.length] = drop;
		mobDropMap[drop.getEntityType().ordinal()] = newDrops;
	}
	
	public Drop[] getDrops(Entity entity) {
		CIEntityType entityType;
		if (entity instanceof Player) {
			Player player = (Player) entity;
			
			// The first check attempts to prevent the need for the possibly expensive second check
			if (player.hasMetadata("NPC") || !Bukkit.getOnlinePlayers().contains(player)) {
				entityType = CIEntityType.NPC;
			} else {
				entityType = CIEntityType.PLAYER;
			}
		} else {
			entityType = CIEntityType.fromBukkitEntityType(entity.getType());
		}
		
		if (entityType != null) {
			EntityDrop[] entityDrops = mobDropMap[entityType.ordinal()];
			int counter = 0;
			for (EntityDrop drop : entityDrops) {
				if (drop.getRequiredName() == null || drop.getRequiredName().equals(entity.getName())) {
					counter++;
				}
			}
			if (counter == 0) {
				return NO_DROPS;
			}
			
			Drop[] drops = new Drop[counter];
			counter = 0;
			for (EntityDrop drop : entityDrops) {
				if (drop.getRequiredName() == null || drop.getRequiredName().equals(entity.getName())) {
					drops[counter++] = drop.getDrop();
				}
			}
			
			return drops;
		} else {
			return NO_DROPS;
		}
	}

	public CustomRecipe[] getRecipes() {
		return recipes;
	}

	public CustomItem getItem(String name) {
		for (CustomItem item : items)
			if (item.getName().equals(name))
				return item;
		return null;
	}

	public ItemDamageClaim getItemDamageClaim(ItemStack item) {
		if (item != null && item.hasItemMeta() && item.getItemMeta().isUnbreakable()) {
			Map<Short, ItemDamageClaim> map = customItemMap.get(CIMaterial.valueOf(ItemHelper.getMaterialName(item)));
			if (map != null) 
				return map.get(item.getDurability());
		}
		return null;
	}
	
	public CustomItem getItem(ItemStack item) {
		ItemDamageClaim claim = getItemDamageClaim(item);
		if (claim instanceof CustomItem)
			return (CustomItem) claim;
		else
			return null;
	}
	
	public ProjectileCover getCover(ItemStack item) {
		ItemDamageClaim claim = getItemDamageClaim(item);
		if (claim instanceof ProjectileCover)
			return (ProjectileCover) claim;
		else
			return null;
	}

	/**
	 * Don't modify this array, only read it!
	 * 
	 * @return The array containing all currently loaded custom items
	 */
	public CustomItem[] getBackingItems() {
		return items;
	}

	@Override
	public CustomItem getCustomItemByName(String name) {
		return getItem(name);
	}

	@Override
	public CIProjectile getProjectileByName(String name) {
		for (CIProjectile projectile : projectiles)
			if (projectile.name.equals(name))
				return projectile;
		return null;
	}

	@Override
	public ProjectileCover getProjectileCoverByName(String name) {
		for (ProjectileCover cover : projectileCovers)
			if (cover.name.equals(name))
				return cover;
		return null;
	}
}