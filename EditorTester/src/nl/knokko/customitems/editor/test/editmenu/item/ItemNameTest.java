package nl.knokko.customitems.editor.test.editmenu.item;

import nl.knokko.gui.testing.GuiTestHelper;

public class ItemNameTest {
	
	public static void test(GuiTestHelper test, String itemName) {
		String badName = itemName.replace('_', ' ');
		test.click("Create");
		test.assertComponentWithText("You can't leave the name empty.");
		test.clickNearest("", "Name: ", 2);
		test.type(badName);
		test.click("Create");
		test.assertComponentWithText("The _ character is the only special character that is allowed in names.");
		test.click(badName);
		test.backspace(badName.length());
		test.type(itemName);
	}
}