package nl.knokko.customitems.editor.test.editmenu.item;

import nl.knokko.gui.component.menu.TextArrayEditMenu;
import nl.knokko.gui.testing.GuiTestHelper;
import nl.knokko.gui.testing.TestException;

public class LoreTest {
	
	/**
	 * Tests the functionality of the lore sub menu. This method will test if you can add lines and
	 * remove lines and if the saving and positioning works correctly.
	 * @param test The test instance
	 * @param lore1 The first line of lore
	 * @param lore2 The second line of lore
	 */
	public static void test(GuiTestHelper test, String lore1, String lore2) {
		test.click("Change lore...");
		test.assertComponentsWithTexts("Cancel", "Add line", "Apply");
		test.click("Add line");
		test.click("Text...");
		test.backspace(7);
		test.type(lore1);
		test.click("Add line");
		test.click("Text...");
		test.backspace(7);
		test.type(lore2);
		test.clickNearestImage(lore1, TextArrayEditMenu.ADD_IMAGE, 4);
		float y1 = test.getComponentWithText(lore1).getState().getMinY();
		if (test.getComponentWithText("").getState().getMinY() < y1) {
			throw new TestException("The new line should be higher");
		}
		test.clickNearestImage(lore1, TextArrayEditMenu.DELETE_IMAGE, 6);
		if (test.getComponentWithText(lore2).getState().getMinY() != y1){
			throw new TestException("The minY of the second component should equal the previous minY of the first lore");
		}
		test.click("Apply");
		test.click("Change lore...");
		test.assertComponentsWithTexts("Cancel", "Add line", "Apply", lore2, "");
		if (test.getComponentWithText(lore2).getState().getMinY() != y1){
			throw new TestException("The minY of the second component should not change after applying and coming back");
		}
		test.click("Cancel");
	}
}