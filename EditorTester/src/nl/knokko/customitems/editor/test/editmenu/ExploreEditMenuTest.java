package nl.knokko.customitems.editor.test.editmenu;

import nl.knokko.gui.testing.GuiTestHelper;
import nl.knokko.gui.testing.GuiTestProgram;

/**
 * Checks if all menus and buttons are present in the edit menu. This test
 * should be run from the edit menu.
 * 
 * @author knokko
 *
 */
public class ExploreEditMenuTest implements GuiTestProgram {

	@Override
	public void test(GuiTestHelper test) {

		// Edit menu buttons
		test.assertComponentsWithTexts("Quit", "Save", "Save and quit", "Export", "Textures", "Items", "Recipes");

		// Go to the texture menu and check the buttons there
		test.click("Textures");
		test.assertComponentsWithTexts("Back", "Load texture");

		// And go a little deeper...
		test.click("Load texture");
		test.assertComponentsWithTexts("Back", "Load simple texture", "Load bow texture");

		// Look quickly into loading simple textures
		test.click("Load simple texture");
		test.assertComponentsWithTexts("Cancel", "Create", "Edit...", "Name: ", "Texture: ", "");

		// Now look into loading bow textures
		test.click("Cancel");
		test.click("Load texture");
		test.click("Load bow texture");
		test.assertComponentsWithTexts("Cancel", "Create", "Add pull", "Edit...", "Base texture: ", "Name: ", "",
				"Pull: ", "Texture: ", "0.0", "0.65", "0.9");

		// Now look into the items menu
		test.click("Cancel");
		test.click("Back");
		test.click("Items");
		test.assertComponentsWithTexts("Back", "Create item");

		// Look a little into item creation
		test.click("Create item");
		test.assertComponentsWithTexts("Cancel", "Simple Item", "Sword", "Pickaxe", "Axe", "Shovel", "Hoe", "Shear",
				"Bow", "Helmet", "Chestplate", "Leggings", "Boots");
		
		// And look a little into an actual item creation
		test.click("Simple Item");
		test.assertComponentsWithTexts("Cancel", "Create", "Name: ", "Internal item type: ",
				"Internal item damage: ", "Display name: ", "Lore: ", "Attribute modifiers: ",
				"Default enchantments: ", "Texture: ", "", "Diamond hoe", "1", "Change lore...",
				"Change attributes...", "Change enchantments...", "None", "Max stacksize:", "64");
		
		// Now look into recipes
		test.click("Cancel");
		test.click("Back");
		test.click("Recipes");
		test.assertComponentsWithTexts("Back", "Create shaped recipe", "Create shapeless recipe");
		
		// Look into shaped recipes
		test.click("Create shaped recipe");
		test.assertComponentsWithTexts("Cancel", "Apply", "empty");
		
		// Look into simple ingredient modification
		test.click("empty");
		test.assertComponentsWithTexts("Back", "Change", "empty");
		test.click("Change");
		test.assertComponentsWithTexts("Cancel", "Custom Item", "Simple vanilla item",
				"Vanilla item with datavalue", "Empty");
		test.click("Custom Item");
		test.click("Cancel");
		test.click("empty");
		test.click("Change");
		test.click("Simple vanilla item");
		test.assertComponentsWithTexts("Cancel", "Search:", "");
		test.click("Cancel");
		test.click("empty");
		test.click("Change");
		test.click("Vanilla item with datavalue");
		test.assertComponentsWithTexts("Cancel", "Data value: ", "0", "Search:", "");
		test.click("Cancel");
		test.click("Cancel");
		
		// Also look into shapeless recipes
		test.click("Create shapeless recipe");
		test.assertComponentsWithTexts("Cancel", "Add ingredient", "Ingredients", "Result");
		test.click("Add ingredient");
		test.assertComponentsWithTexts("Cancel", "Custom Item", "Simple vanilla item",
				"Vanilla item with datavalue");
		test.click("Cancel");
		test.click("Cancel");
		test.click("Back");
	}
}