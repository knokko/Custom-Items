package nl.knokko.customitems.editor.test.editmenu.item;

import java.awt.image.BufferedImage;

import nl.knokko.gui.testing.GuiTestHelper;

public class AttributeModTest {
	
	/**
	 * Tests the functionality of the attribute modifiers submenu in an item edit menu. The attribute modifiers
	 * that should be set eventually are the parameters of this method.
	 * This test should be run from an item edit menu and should end in that same menu.
	 * @param test The test instance
	 * @param attribute1 The attribute name of the first attribute modifier
	 * @param slot1 The slot of the first attribute modifier
	 * @param op1 The operation of the first attribute modifier
	 * @param value1 The value (as string) of the first attribute modifier
	 * @param attribute2 The attribute name of the second attribute modifier
	 * @param slot2 The slot of the second attribute modifier
	 * @param op2 The operation of the second attribute modifier
	 * @param value2 The value (as string) of the second attribute modifier
	 */
	public static void test(GuiTestHelper test, String attribute1, String slot1, String op1, String value1,
			String attribute2, String slot2, String op2, String value2) {
		test.click("Change attributes...");
		test.assertComponentsWithTexts("Cancel", "New Attribute", "Apply");
		test.click("New Attribute");
		BufferedImage deleteImage = (BufferedImage) test.getComponentWithText("mainhand").getState().getWindow().getTextureLoader().loadTexture("nl/knokko/gui/images/icons/delete.png").getImage();
		test.assertImageShown(deleteImage);
		test.assertComponentsWithTexts("generic.attackDamage", "mainhand", "Add", "Value: ", "5.0");
		test.click("generic.attackDamage");
		test.click(attribute1);
		test.click("mainhand");
		test.click(slot1);
		test.click("Add");
		test.click(op1);
		test.click("5.0");
		test.backspace(3);
		test.type("test");
		test.click("Apply");
		test.assertComponentWithText("All values must be numbers");
		test.click("test");
		test.backspace(4);
		test.type(value1);
		test.click("New Attribute");
		
		// Click nearest to make sure we edit the right attribute
		test.clickNearest("generic.attackDamage", "New Attribute", attribute1.equals("generic.attackDamage") ? 2 : 1);
		test.click(attribute2);
		test.clickNearest("mainhand", "New Attribute", slot1.equals("mainhand") ? 2 : 1);
		test.click(slot2);
		test.clickNearest("Add", "New Attribute", op1.equals("Add") ? 2 : 1);
		test.click(op2);
		test.clickNearest("5.0", "New Attribute", value1.equals("5.0") ? 2 : 1);
		test.backspace(3);
		test.type(value2);
		test.assertComponentWithText(value2);
		test.click("Apply");
		test.click("Change attributes...");
		test.assertComponentsWithTexts("Cancel", "New Attribute", "Apply", attribute1, slot1, op1, value1,
				attribute2, slot2, op2, value2);
		test.click(value2);
		test.type("1");
		test.assertComponentWithText(value2 + "1");
		test.click("Apply");
		test.click("Change attributes...");
		test.click(value2 + "1");
		test.backspace(1);
		test.assertComponentWithText(value2);
		test.click("Apply");
	}
}