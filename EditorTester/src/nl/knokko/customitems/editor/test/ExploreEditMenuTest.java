package nl.knokko.customitems.editor.test;

import nl.knokko.gui.testing.GuiTestHelper;
import nl.knokko.gui.testing.GuiTestProgram;

/**
 * Checks if all menus and buttons are present in the edit menu. This test should be run from the edit menu.
 * @author knokko
 *
 */
public class ExploreEditMenuTest implements GuiTestProgram {

	@Override
	public void test(GuiTestHelper test) {
		test.setDelayTime(100);
		
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
		test.assertComponentsWithTexts("Cancel", "Create", "Add pull", "Edit...", "Base texture: ", "Name: "
				, "", "Pull: ", "Texture: ", "0.0", "0.65", "0.9");
		
		// Now look into the items menu
		test.click("Cancel");
		test.click("Back");
		test.click("Items");
		test.assertComponentsWithTexts("Back", "Create item");
		
		// Look a little into item creation
		test.click("Create item");
		test.assertComponentsWithTexts("Cancel", "Simple Item", "Sword", "Pickaxe", "Axe", "Shovel", "Hoe",
				"Shear", "Bow", "Helmet", "Chestplate", "Leggings", "Boots");
	}
}