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
package nl.knokko.customitems.editor.test;

import nl.knokko.customitems.editor.test.editmenu.ExploreEditMenuTest;
import nl.knokko.customitems.editor.test.editmenu.item.CreateBowTest;
import nl.knokko.customitems.editor.test.editmenu.item.CreateHoeTest;
import nl.knokko.customitems.editor.test.editmenu.item.CreateShearsTest;
import nl.knokko.customitems.editor.test.editmenu.item.CreateSimpleItemTest;
import nl.knokko.customitems.editor.test.editmenu.item.CreateToolTest;
import nl.knokko.customitems.editor.test.editmenu.recipe.AddRecipeTest;
import nl.knokko.customitems.editor.test.editmenu.texture.CreateBowTextureTest1;
import nl.knokko.customitems.editor.test.editmenu.texture.CreateSimpleTextureTest1;
import nl.knokko.customitems.editor.test.mainmenu.CreateItemSetTest;
import nl.knokko.customitems.editor.test.mainmenu.ExploreMainMenuTest;
import nl.knokko.gui.testing.GuiTestHelper;
import nl.knokko.gui.testing.GuiTestProgram;

/**
 * This is the first combined test program. It tests the basic functionality of the menus and then creates
 * the first part of the test item set. It should be run from the main menu.
 * @author knokko
 *
 */
public class EditorTestFull implements GuiTestProgram {

	@Override
	public void test(GuiTestHelper test) {
		test.setDelayTime(8);
		ExploreMainMenuTest.test(test);
		CreateItemSetTest.test(test, "automatic test");
		ExploreEditMenuTest.test(test);
		
		// Test creating textures
		CreateBowTextureTest1.test(test, "test_bow_first");
		CreateSimpleTextureTest1.test(test, "simple_test");
		
		// Test creating items
		CreateSimpleItemTest.create(test, "simple_test_item", "simple_test_one", "52",
				"The very first line of lore", "The second line of lore", 
				"generic.armor", "head", "Multiply", "1.3", 
				"generic.attackSpeed", "offhand", "Add", "4.0", 
				"fire aspect", "5", "knockback", "1");
		test.assertComponentsWithTexts("Back", "Create item", "Edit", "Delete", "simple_test_item");
		CreateToolTest.create(test, "Axe", "test_aks", "simple_test_one", "1234", "Custom Item", 
				"simple_test_item", "4", "3", "Test Test Chop", "Chop Chop akse", "generic.attackSpeed", 
				"offhand", "Add factor", "3.14", "generic.movementSpeed", "mainhand", "Multiply", "1.3", 
				"knockback", "4", "damage undead", "7");
		CreateToolTest.create(test, "Shovel", "test_shovel", "simple_test_one", "4312", 
				"Vanilla item with datavalue", "anvil", "2", "1", "For digging graves", "Or just normal stuff", 
				"generic.armor", "mainhand","Multiply", "3.0", "generic.luck", "mainhand", "Add", "1.0", 
				"mending", "3", "dig speed", "5");
		CreateToolTest.create(test, "Sword", "test_sword", "simple_test_one", "3214", "Vanilla item with datavalue", 
				"apple", "6", "7", "Test Test Slash", "Smite sword", "generic.attackDamage", 
				"mainhand", "Multiply", "1.8", "generic.knockbackResistance", "mainhand", "Add", "0.7", 
				"fire aspect", "2", "loot bonus mobs", "1");
		CreateToolTest.create(test, "Pickaxe", "test_pick", "simple_test_one", "4132", "Empty", 
				null, "4", "1", "Test Test Break", "Breaking pick", "generic.luck", 
				"mainhand", "Multiply", "1.6", "generic.knockbackResistance", "offhand", "Add", "0.2", 
				"dig speed", "8", "silk touch", "4");
		CreateHoeTest.create(test, "test_hoe", "simple_test_one", "2431", "Custom Item", "simple_test_item", 
				"1", "2", "3", "A fighting hoe", "Can also till dirt", 
				"generic.attackSpeed", "mainhand", "Multiply", "2.0", 
				"generic.attackDamage", "mainhand", "Add", "8.0", 
				"loot bonus blocks", "2", "loot bonus mobs", "5");
		CreateShearsTest.create(test, "test_shears", "simple_test_one", "64", "Simple vanilla item", "arrow", 
				"4", "3", "2", "Improve your defences", "Or just obtain wool from sheeps", 
				"generic.maxHealth", "offhand", "Add", "6.0", 
				"generic.armorToughness", "offhand", "Add", "3.0", "damage all", "8", "binding curse", "1");
		CreateBowTest.create(test, "fragile_bow", "test_bow_first", "34", "Simple vanilla item", "arrow", 
				"5", "6", "7", "A quite fragile bow", "Also deals more damage", "8", "3.14", "9", 
				"generic.knockbackResistance", "offhand", "Add", "0.4", 
				"generic.attackDamage", "mainhand", "Multiply", "2.0", 
				"arrow infinite", "7", "durability", "2");
		test.assertComponentsWithTexts("simple_test_item", "test_aks", "fragile_bow");
		// TODO Also create EditorTestQuick
		
		// Test adding recipes
		test.click("Back");
		test.click("Recipes");
		AddRecipeTest.addShapelessRecipe(test, "Custom Item;test_hoe", "1", "Custom Item;test_aks",
				"Simple vanilla item;apple", "Vanilla item with datavalue;anvil;OK");
		
	}
}