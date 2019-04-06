package nl.knokko.customitems.editor.test;

import nl.knokko.gui.testing.GuiTestHelper;
import nl.knokko.gui.testing.GuiTestProgram;

/**
 * Tests the buttons in the main menu, new set menu and load set menu.
 * This test should be run from the main menu.
 * @author knokko
 *
 */
public class MainMenuTest implements GuiTestProgram {

	@Override
	public void test(GuiTestHelper test) {
		test.setDelayTime(200);
		test.assertComponentWithText("Edit item set");
		test.assertComponentWithText("Exit editor");
		test.click("New item set");
		test.assertComponentWithText("");
		test.assertComponentWithText("Create");
		test.click("Cancel");
		test.assertComponentWithText("New item set");
		test.assertComponentWithText("Exit editor");
		test.click("Edit item set");
		test.assertComponentWithText("Refresh");
		test.click("Load back-up");
		test.assertComponentWithText("Refresh");
		test.click("Back");
		test.assertComponentWithText("Refresh");
		test.click("Cancel");
		test.assertComponentWithText("New item set");
		test.assertComponentWithText("Edit item set");
		test.assertComponentWithText("Exit editor");
	}
}
