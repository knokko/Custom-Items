package nl.knokko.customitems.editor.test;

import nl.knokko.gui.testing.GuiTestHelper;
import nl.knokko.gui.testing.GuiTestProgram;

/**
 * This is the first combined test program. It tests the basic functionality of the menus and then creates
 * the first part of the test item set. It should be run from the main menu.
 * @author knokko
 *
 */
public class EditorTest1 implements GuiTestProgram {

	@Override
	public void test(GuiTestHelper test) {
		new ExploreMainMenuTest().test(test);
		new CreateItemSetTest("automatic_test").test(test);
		new ExploreEditMenuTest().test(test);
	}
}