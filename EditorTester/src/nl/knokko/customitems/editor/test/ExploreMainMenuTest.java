package nl.knokko.customitems.editor.test;

import nl.knokko.gui.testing.GuiTestHelper;
import nl.knokko.gui.testing.GuiTestProgram;

/**
 * Tests the buttons in the main menu, new set menu and load set menu.
 * This test should be run from the main menu.
 * @author knokko
 *
 */
public class ExploreMainMenuTest implements GuiTestProgram {

	@Override
	public void test(GuiTestHelper test) {
		test.setDelayTime(50);
		test.assertComponentsWithTexts("Edit item set", "Exit editor");
		test.click("New item set");
		
		// This is the first time we open this menu, so a small delay is allowed
		test.delay(50);
		test.assertComponentsWithTexts("", "Create");
		test.click("Cancel");
		test.assertComponentsWithTexts("New item set", "Exit editor");
		test.click("Edit item set");
		
		// Sometimes, this takes a little longer for some reason
		test.delay(100);
		test.assertComponentWithText("Refresh");
		test.click("Load back-up");
		
		// This can have some delay as well
		test.delay(100);
		test.assertComponentWithText("Refresh");
		test.click("Back");
		test.assertComponentWithText("Refresh");
		test.click("Cancel");
		test.assertComponentsWithTexts("New item set", "Edit item set", "Exit editor");
	}
}
