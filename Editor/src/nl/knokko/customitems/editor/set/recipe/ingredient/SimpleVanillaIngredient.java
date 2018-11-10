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
package nl.knokko.customitems.editor.set.recipe.ingredient;

import nl.knokko.customitems.editor.set.item.Material;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class SimpleVanillaIngredient implements Ingredient {
	
	private final Material type;
	
	private String[] info;

	public SimpleVanillaIngredient(Material material) {
		this.type = material;
		determineInfo();
	}
	
	public SimpleVanillaIngredient(BitInput input) {
		type = Material.valueOf(input.readJavaString());
		determineInfo();
	}
	
	private void determineInfo() {
		info = new String[] {
				"Vanilla ingredient:",
				"Type: " + type.name().toLowerCase()
		};
	}
	
	public Material getType() {
		return type;
	}

	@Override
	public void save(BitOutput output) {
		output.addByte(getID());
		output.addJavaString(type.name());
	}

	@Override
	public byte getID() {
		return RecipeEncoding.Ingredient.VANILLA_SIMPLE;
	}

	@Override
	public boolean conflictsWith(Ingredient other) {
		if (other instanceof SimpleVanillaIngredient)
			return ((SimpleVanillaIngredient) other).type == type;
		if (other instanceof DataVanillaIngredient)
			return ((DataVanillaIngredient) other).getType() == type;
		return false;
	}
	
	@Override
	public String toString(String emptyString) {
		return type.name().toLowerCase();
	}

	@Override
	public String[] getInfo(String emptyString) {
		return info;
	}
}