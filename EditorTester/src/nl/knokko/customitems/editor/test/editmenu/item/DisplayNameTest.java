package nl.knokko.customitems.editor.test.editmenu.item;

import nl.knokko.gui.testing.GuiTestHelper;

public class DisplayNameTest {
	
	public static void test(GuiTestHelper test, String itemName) {
		test.click("");
		String displayName = itemName.replace('_', ' ');
		test.type(displayName);
		test.assertComponentWithText(displayName);
	}
}