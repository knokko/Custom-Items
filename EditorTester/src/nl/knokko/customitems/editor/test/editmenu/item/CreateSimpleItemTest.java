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
package nl.knokko.customitems.editor.test.editmenu.item;

import nl.knokko.gui.testing.GuiTestHelper;

public class CreateSimpleItemTest {
	
	/**
	 * This method tests the functionality of creating a simple custom item. It should be run from the edit
	 * menu and it will end at the edit menu.
	 * @param test The test instance
	 * @param itemName The name the new custom item should get
	 * @param textureName The name of the texture the item to create should get
	 * @param lore1 The first line of test lore
	 * @param lore2 The second line of test lore
	 */
	public static void create(GuiTestHelper test, String itemName, String textureName, String lore1, String lore2) {
		test.click("Items");
		test.assertComponentsWithTexts("Back", "Create item");
		test.click("Create item");
		test.assertComponentsWithTexts("Cancel", "Simple Item", "Sword", "Axe", "Pickaxe", "Shovel", "Hoe",
				"Shear", "Bow", "Helmet", "Chestplate", "Leggings", "Boots");
		test.click("Simple Item");
		test.assertComponentsWithTexts("Cancel", "Create", "Name: ", "Internal item type: ", 
				"Internal item damage: ", "Display name: ", "Lore: ", "Attribute modifiers: ",
				"Default enchantments: ", "Texture: ", "", "Item flags: ", "Diamond hoe", "1", "Change lore...",
				"Change attributes...", "Change flags...", "Change enchantments...", "None", "Max stacksize:", "64");
		ItemNameTest.test(test, itemName);
		SimpleTextureTest.test(test, textureName);
		DisplayNameTest.test(test, itemName);
		test.click("64");
		test.backspace(1);
		test.type('1');
		test.assertComponentWithText("61");
		LoreTest.test(test, lore1, lore2);
		AttributeModTest.test(test, "generic.armor", "chest", "Chain multiply", "3.14",
				"generic.movementSpeed", "feet", "Multiply", "1.2");
		// TODO test enchantments
	}
}