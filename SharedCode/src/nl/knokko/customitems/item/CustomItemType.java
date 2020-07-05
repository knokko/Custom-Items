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
package nl.knokko.customitems.item;

import static nl.knokko.customitems.item.CustomItemType.Category.*;

import java.util.Locale;

import nl.knokko.customitems.NameHelper;

import static nl.knokko.customitems.MCVersions.*;

public enum CustomItemType {
	
	IRON_SHOVEL(250, VERSION1_12, VERSION1_16, SHOVEL, PROJECTILE_COVER),
	IRON_PICKAXE(250, VERSION1_12, VERSION1_16, PICKAXE, PROJECTILE_COVER),
	IRON_AXE(250, VERSION1_12, VERSION1_16, AXE, PROJECTILE_COVER),
	FLINT_AND_STEEL(64, VERSION1_12, VERSION1_16, FLINT, PROJECTILE_COVER),
	BOW(384, VERSION1_12, VERSION1_16, Category.BOW),
	IRON_SWORD(250, VERSION1_12, VERSION1_16, SWORD, PROJECTILE_COVER),
	WOOD_SWORD(59, VERSION1_12, VERSION1_16, SWORD, PROJECTILE_COVER),
	WOOD_SHOVEL(59, VERSION1_12, VERSION1_16, SHOVEL, PROJECTILE_COVER),
	WOOD_PICKAXE(59, VERSION1_12, VERSION1_16, PICKAXE, PROJECTILE_COVER),
	WOOD_AXE(59, VERSION1_12, VERSION1_16, AXE, PROJECTILE_COVER),
	STONE_SWORD(131, VERSION1_12, VERSION1_16, SWORD, PROJECTILE_COVER),
	STONE_SHOVEL(131, VERSION1_12, VERSION1_16, SHOVEL, PROJECTILE_COVER),
	STONE_PICKAXE(131, VERSION1_12, VERSION1_16, PICKAXE, PROJECTILE_COVER),
	STONE_AXE(131, VERSION1_12, VERSION1_16, AXE, PROJECTILE_COVER),
	DIAMOND_SWORD(1561, VERSION1_12, VERSION1_16, SWORD, PROJECTILE_COVER),
	DIAMOND_SHOVEL(1561, VERSION1_12, VERSION1_16, SHOVEL, PROJECTILE_COVER),
	DIAMOND_PICKAXE(1561, VERSION1_12, VERSION1_16, PICKAXE, PROJECTILE_COVER),
	DIAMOND_AXE(1561, VERSION1_12, VERSION1_16, AXE, PROJECTILE_COVER),
	GOLD_SWORD(32, VERSION1_12, VERSION1_16, SWORD, PROJECTILE_COVER),
	GOLD_SHOVEL(32, VERSION1_12, VERSION1_16, SHOVEL, PROJECTILE_COVER),
	GOLD_PICKAXE(32, VERSION1_12, VERSION1_16, PICKAXE, PROJECTILE_COVER),
	GOLD_AXE(32, VERSION1_12, VERSION1_16, AXE, PROJECTILE_COVER),
	WOOD_HOE(59, VERSION1_12, VERSION1_16, HOE, DEFAULT, WAND, PROJECTILE_COVER),
	STONE_HOE(131, VERSION1_12, VERSION1_16, HOE, DEFAULT, WAND, PROJECTILE_COVER),
	IRON_HOE(250, VERSION1_12, VERSION1_16, HOE, DEFAULT, WAND, PROJECTILE_COVER),
	DIAMOND_HOE(1561, VERSION1_12, VERSION1_16, HOE, DEFAULT, WAND, PROJECTILE_COVER),
	GOLD_HOE(32, VERSION1_12, VERSION1_16, HOE, DEFAULT, WAND, PROJECTILE_COVER),
	LEATHER_HELMET(55, VERSION1_12, VERSION1_16, HELMET),
	LEATHER_CHESTPLATE(80, VERSION1_12, VERSION1_16, CHESTPLATE),
	LEATHER_LEGGINGS(75, VERSION1_12, VERSION1_16, LEGGINGS),
	LEATHER_BOOTS(65, VERSION1_12, VERSION1_16, BOOTS),
	CHAINMAIL_HELMET(165, VERSION1_12, VERSION1_16, HELMET, PROJECTILE_COVER),
	CHAINMAIL_CHESTPLATE(240, VERSION1_12, VERSION1_16, CHESTPLATE, PROJECTILE_COVER),
	CHAINMAIL_LEGGINGS(225, VERSION1_12, VERSION1_16, LEGGINGS, PROJECTILE_COVER),
	CHAINMAIL_BOOTS(195, VERSION1_12, VERSION1_16, BOOTS, PROJECTILE_COVER),
	IRON_HELMET(165, VERSION1_12, VERSION1_16, HELMET, PROJECTILE_COVER),
	IRON_CHESTPLATE(240, VERSION1_12, VERSION1_16, CHESTPLATE, PROJECTILE_COVER),
	IRON_LEGGINGS(225, VERSION1_12, VERSION1_16, LEGGINGS, PROJECTILE_COVER),
	IRON_BOOTS(195, VERSION1_12, VERSION1_16, BOOTS, PROJECTILE_COVER),
	DIAMOND_HELMET(363, VERSION1_12, VERSION1_16, HELMET, PROJECTILE_COVER),
	DIAMOND_CHESTPLATE(528, VERSION1_12, VERSION1_16, CHESTPLATE, PROJECTILE_COVER),
	DIAMOND_LEGGINGS(495, VERSION1_12, VERSION1_16, LEGGINGS, PROJECTILE_COVER),
	DIAMOND_BOOTS(429, VERSION1_12, VERSION1_16, BOOTS, PROJECTILE_COVER),
	GOLD_HELMET(77, VERSION1_12, VERSION1_16, HELMET, PROJECTILE_COVER),
	GOLD_CHESTPLATE(112, VERSION1_12, VERSION1_16, CHESTPLATE, PROJECTILE_COVER),
	GOLD_LEGGINGS(105, VERSION1_12, VERSION1_16, LEGGINGS, PROJECTILE_COVER),
	GOLD_BOOTS(91, VERSION1_12, VERSION1_16, BOOTS, PROJECTILE_COVER),
	FISHING_ROD(64, VERSION1_12, VERSION1_16, FISHING, PROJECTILE_COVER),
	SHEARS(238, VERSION1_12, VERSION1_16, SHEAR, DEFAULT, WAND, PROJECTILE_COVER),
	CARROT_STICK(25, VERSION1_12, VERSION1_16, CARROTSTICK, PROJECTILE_COVER),
	SHIELD(336, VERSION1_12, VERSION1_16, Category.SHIELD),
	ELYTRA(431, VERSION1_12, VERSION1_16, Category.ELYTRA),
	TRIDENT(250, VERSION1_13, VERSION1_14, Category.TRIDENT);
	
	private final short maxDurability;
	private final Category[] categories;
	
	private final String minecraftName;
	private final String textureName12;
	private final String modelName12;
	private final String textureName14;
	private final String modelName14;
	
	public final int firstVersion, lastVersion;
	
	CustomItemType(int maxDurability, int firstVersion, int lastVersion, Category... categories){
		this.maxDurability = (short) maxDurability;
		this.categories = categories;
		this.firstVersion = firstVersion;
		this.lastVersion = lastVersion;
		
		String lowerCaseName = this.name().toLowerCase(Locale.ROOT);
		if (lowerCaseName.equals("carrot_stick")) {
			this.minecraftName = "carrot_on_a_stick";
			this.textureName12 = this.minecraftName;
			this.modelName12 = this.minecraftName;
			this.textureName14 = this.minecraftName;
			this.modelName14 = this.minecraftName;
		}
		else if (lowerCaseName.startsWith("gold")) {
			this.minecraftName = lowerCaseName.replace("gold", "golden");
			this.textureName12 = lowerCaseName;
			this.modelName12 = this.minecraftName;
			this.textureName14 = this.minecraftName;
			this.modelName14 = this.minecraftName;
		}
		else if (lowerCaseName.startsWith("wood")) {
			this.minecraftName = lowerCaseName.replace("wood", "wooden");
			this.textureName12 = lowerCaseName;
			this.modelName12 = this.minecraftName;
			this.textureName14 = this.minecraftName;
			this.modelName14 = this.minecraftName;
		}
		else {
			this.minecraftName = lowerCaseName;
			this.textureName12 = lowerCaseName;
			this.modelName12 = lowerCaseName;
			this.textureName14 = lowerCaseName;
			this.modelName14 = lowerCaseName;
		}
	}
	
	@Override
	public String toString() {
		return NameHelper.getNiceEnumName(name(), firstVersion, lastVersion);
	}
	
	/**
	 * @return The in-game name of the item that this CustomItemType represents
	 */
	public String getMinecraftName() {
		return minecraftName;
	}
	
	/**
	 * The file name (without extension) of the texture of the in-game item represented by this CustomItemType
	 * in the assets/minecraft/textures/items folder. This method is only for minecraft version 1.12 (these
	 * names are different in other versions of minecraft).
	 */
	public String getTextureName12() {
		return textureName12;
	}
	
	/**
	 * The file name (without extension) of the model of the in-game item represented by this CustomItemType
	 * in the assets/minecraft/models/item folder. This method is only for minecraft version 1.12 (currently,
	 * the names are the same as in minecraft 1.12, but I had rather keep these methods separated just in case).
	 */
	public String getModelName12() {
		return modelName12;
	}
	
	/**
	 * The file name (without extension) of the texture of the in-game item represented by this CustomItemType
	 * in the assets/minecraft/textures/item folder. This method is only for minecraft version 1.14 (these
	 * names are different in other versions of minecraft).
	 */
	public String getTextureName14() {
		return textureName14;
	}
	
	/**
	 * The file name (without extension) of the model of the in-game item represented by this CustomItemType
	 * in the assets/minecraft/models/item folder. This method is only for minecraft version 1.14 (currently,
	 * the names are the same as in minecraft 1.12, but I had rather keep these methods separated just in case).
	 */
	public String getModelName14() {
		return modelName14;
	}
	
	public short getMaxDurability() {
		return maxDurability;
	}
	
	public boolean canServe(Category category) {
		for (Category current : categories)
			if (current == category)
				return true;
		return false;
	}
	
	public Category getMainCategory() {
		return categories[0];
	}
	
	public boolean isLeatherArmor() {
		return this == LEATHER_BOOTS || this == LEATHER_LEGGINGS || this == LEATHER_CHESTPLATE
				|| this == LEATHER_HELMET;
	}
	
	public static enum Category {
		
		DEFAULT,
		SWORD,
		AXE,
		PICKAXE,
		SHOVEL,
		HOE,
		BOW,
		HELMET,
		CHESTPLATE,
		LEGGINGS,
		BOOTS,
		FISHING,
		SHEAR,
		FLINT,
		CARROTSTICK,
		SHIELD,
		ELYTRA,
		TRIDENT,
		PROJECTILE_COVER,
		WAND;
	}
}