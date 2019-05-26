package nl.knokko.customitems.editor.test.editmenu.item;

import nl.knokko.gui.testing.GuiTestHelper;

public class SimpleTextureTest {
	
	public static void test(GuiTestHelper test, String textureName) {
		test.click("Create");
		test.assertComponentWithText("Every item needs a texture");
		test.click("None");
		test.click(textureName);
	}
}
