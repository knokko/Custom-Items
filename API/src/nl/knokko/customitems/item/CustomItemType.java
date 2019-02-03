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

public enum CustomItemType {
	
	IRON_SHOVEL(250, SHOVEL),
	IRON_PICKAXE(250, PICKAXE),
	IRON_AXE(250, AXE),
	FLINT_AND_STEEL(64, FLINT),
	BOW(384, Category.BOW),
	IRON_SWORD(250, SWORD),
	WOOD_SWORD(59, SWORD),
	WOOD_SHOVEL(59, SHOVEL),
	WOOD_PICKAXE(59, PICKAXE),
	WOOD_AXE(59, AXE),
	STONE_SWORD(131, SWORD),
	STONE_SHOVEL(131, SHOVEL),
	STONE_PICKAXE(131, PICKAXE),
	STONE_AXE(131, AXE),
	DIAMOND_SWORD(1561, SWORD),
	DIAMOND_SHOVEL(1561, SHOVEL),
	DIAMOND_PICKAXE(1561, PICKAXE),
	DIAMOND_AXE(1561, AXE),
	GOLD_SWORD(32, SWORD),
	GOLD_SHOVEL(32, SHOVEL),
	GOLD_PICKAXE(32, PICKAXE),
	GOLD_AXE(32, AXE),
	WOOD_HOE(59, HOE, DEFAULT),
	STONE_HOE(131, HOE, DEFAULT),
	IRON_HOE(250, HOE, DEFAULT),
	DIAMOND_HOE(1561, HOE, DEFAULT),
	GOLD_HOE(32, HOE, DEFAULT),
	LEATHER_HELMET(55, HELMET),
	LEATHER_CHESTPLATE(80, CHESTPLATE),
	LEATHER_LEGGINGS(75, LEGGINGS),
	LEATHER_BOOTS(65, BOOTS),
	CHAINMAIL_HELMET(165, HELMET),
	CHAINMAIL_CHESTPLATE(240, CHESTPLATE),
	CHAINMAIL_LEGGINGS(225, LEGGINGS),
	CHAINMAIL_BOOTS(195, BOOTS),
	IRON_HELMET(165, HELMET),
	IRON_CHESTPLATE(240, CHESTPLATE),
	IRON_LEGGINGS(225, LEGGINGS),
	IRON_BOOTS(195, BOOTS),
	DIAMOND_HELMET(363, HELMET),
	DIAMOND_CHESTPLATE(528, CHESTPLATE),
	DIAMOND_LEGGINGS(495, LEGGINGS),
	DIAMOND_BOOTS(429, BOOTS),
	GOLD_HELMET(77, HELMET),
	GOLD_CHESTPLATE(112, CHESTPLATE),
	GOLD_LEGGINGS(105, LEGGINGS),
	GOLD_BOOTS(91, BOOTS),
	FISHING_ROD(64, FISHING),
	SHEARS(238, SHEAR, DEFAULT),
	CARROT_STICK(25, CARROTSTICK),
	SHIELD(336, Category.SHIELD),
	ELYTRA(431, Category.ELYTRA);
	
	private final short maxDurability;
	private final Category[] categories;
	
	CustomItemType(int maxDurability, Category... categories){
		this.maxDurability = (short) maxDurability;
		this.categories = categories;
	}
	
	@Override
	public String toString() {
		return name().charAt(0) + name().substring(1).toLowerCase().replaceAll("_", " ");
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
		ELYTRA;
	}
}