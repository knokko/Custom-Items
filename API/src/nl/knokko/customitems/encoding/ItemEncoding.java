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
package nl.knokko.customitems.encoding;

public class ItemEncoding {
	
	/**
	 * The first item encoding
	 */
	public static final byte ENCODING_SIMPLE_1 = 0;
	
	// All encodings that end with a 2 or greater have support for attribute modifiers
	public static final byte ENCODING_SIMPLE_2 = 1;
	public static final byte ENCODING_TOOL_2 = 2;
	
	/**
	 * Those tools can also have repair materials for anvil
	 */
	public static final byte ENCODING_TOOL_3 = 3;
	public static final byte ENCODING_BOW_3 = 4;
	
	/**
	 * Adds support for default enchantments
	 */
	public static final byte ENCODING_SIMPLE_3 = 5;
	
	/**
	 * Adds support for default enchantments and changes durability to long
	 */
	public static final byte ENCODING_TOOL_4 = 6;
	
	/**
	 * Adds support for default enchantments and changes durability to long
	 */
	public static final byte ENCODING_BOW_4 = 7;
	
	public static final byte ENCODING_ARMOR_4 = 8;
	
	/**
	 * Add maxStackSize
	 */
	public static final byte ENCODING_SIMPLE_4 = 9;
	
	/**
	 * Add support for item flags
	 */
	public static final byte ENCODING_SIMPLE_5 = 10;
	
	/**
	 * Add support for item flags, entity hit durability loss and block break durability loss
	 */
	public static final byte ENCODING_TOOL_5 = 11;
	
	/**
	 * Add support for item flags and durability loss for entity attacking, block breaking and shooting
	 */
	public static final byte ENCODING_BOW_5 = 12;
	
	/**
	 * Add support for item flags and durability loss for entity attacking, block breaking and getting attacked
	 */
	public static final byte ENCODING_ARMOR_5 = 13;
	
	/**
	 * Add support for shear durability loss. This is the first shear encoding
	 */
	public static final byte ENCODING_SHEAR_5 = 14;
	
	/**
	 * Add support for use durability loss. This is the first hoe encoding
	 */
	public static final byte ENCODING_HOE_5 = 15;
	
	/**
	 * Add support for damage resistances.
	 */
	public static final byte ENCODING_ARMOR_6 = 16;
	
	/**
	 * The first encoding of custom shields
	 */
	public static final byte ENCODING_SHIELD_6 = 17;
}