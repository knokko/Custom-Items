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
package nl.knokko.customitems.editor.set.recipe.ingredient;

import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class DataVanillaIngredient implements Ingredient {
	
	private final CIMaterial type;
	private final byte data;
	
	private String[] info;

	public DataVanillaIngredient(CIMaterial type, byte data) {
		this.type = type;
		this.data = data;
		determineInfo();
	}
	
	public DataVanillaIngredient(BitInput input) {
		type = CIMaterial.valueOf(input.readJavaString());
		data = (byte) input.readNumber((byte) 4, false);
		determineInfo();
	}
	
	private void determineInfo() {
		info = new String[] {
				"Vanilla ingredient:",
				"Type: " + type.name().toLowerCase(),
				"Data: " + data
		};
	}
	
	public CIMaterial getType() {
		return type;
	}
	
	public byte getData() {
		return data;
	}

	@Override
	public void save(BitOutput output) {
		output.addByte(getID());
		output.addJavaString(type.name());
		output.addNumber(data, (byte) 4, false);
	}

	@Override
	public byte getID() {
		return RecipeEncoding.Ingredient.VANILLA_DATA;
	}

	@Override
	public boolean conflictsWith(Ingredient other) {
		if (other instanceof SimpleVanillaIngredient)
			return ((SimpleVanillaIngredient) other).getType() == type;
		if (other instanceof DataVanillaIngredient) {
			DataVanillaIngredient dvi = (DataVanillaIngredient) other;
			return dvi.type == type && dvi.data == data;
		}
		return false;
	}
	
	@Override
	public String toString(String emptyString) {
		return type.name().toLowerCase() + "(" + data + ")";
	}
	
	@Override
	public String toString() {
		return toString(null);
	}

	@Override
	public String[] getInfo(String emptyString) {
		return info;
	}
}