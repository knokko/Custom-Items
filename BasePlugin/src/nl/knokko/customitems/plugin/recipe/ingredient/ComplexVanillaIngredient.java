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
package nl.knokko.customitems.plugin.recipe.ingredient;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ComplexVanillaIngredient implements Ingredient {
	
	public ComplexVanillaIngredient(Material type, byte data, short durability) {
		this.type = type;
		this.data = data;
		this.durability = durability;
	}
	
	private final Material type;
	private final byte data;
	private final short durability;
	
	@Override
	@SuppressWarnings("deprecation")
	public boolean accept(ItemStack item) {
		// TODO Work this out later
		return item.getType() == type && item.getData().getData() == data && item.getDurability() == durability;
	}

	@Override
	public Material getType() {
		return type;
	}

	@Override
	public short getData() {
		return data;
	}
}