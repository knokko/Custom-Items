package nl.knokko.customitems.editor.test.mainmenu;

import nl.knokko.gui.testing.GuiTestHelper;

public class ExploreMainMenuTest {

	/**
	 * Tests the buttons in the main menu, new set menu and load set menu.
	 * This test should be run from the main menu and it should end in the main menu.
	 * @param test The test instance
	 */
	public static void test(GuiTestHelper test) {
		test.assertComponentsWithTexts("Edit item set", "Exit editor");
		test.click("New item set");
		
		test.assertComponentsWithTexts("", "Create");
		test.click("Cancel");
		test.assertComponentsWithTexts("New item set", "Exit editor");
		test.click("Edit item set");
		
		test.assertComponentWithText("Refresh");
		test.click("Load back-up");
		
		test.assertComponentWithText("Refresh");
		test.click("Back");
		test.assertComponentWithText("Refresh");
		test.click("Cancel");
		test.assertComponentsWithTexts("New item set", "Edit item set", "Exit editor");
	}
}
