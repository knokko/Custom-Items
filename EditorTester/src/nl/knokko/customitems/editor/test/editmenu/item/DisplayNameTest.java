package nl.knokko.customitems.editor.test.editmenu.item;

import nl.knokko.gui.testing.GuiTestHelper;

public class DisplayNameTest {
	
	/**
	 * Briefly tests the functionality of the display name of a custom item. Since this is a quite simply
	 * feature, the test is also quite simple.
	 * This test should be run from an item edit menu and should end in that same menu.
	 * @param test The test instance
	 * @param itemName The item name of the custom item, the display name is automatically chosen based on
	 * that item name
	 */
	public static void test(GuiTestHelper test, String itemName) {
		test.click("");
		String displayName = itemName.replace('_', ' ');
		test.type(displayName);
		test.assertComponentWithText(displayName);
	}
}