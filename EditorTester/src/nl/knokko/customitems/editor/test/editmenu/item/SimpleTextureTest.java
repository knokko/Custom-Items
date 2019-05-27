package nl.knokko.customitems.editor.test.editmenu.item;

import nl.knokko.gui.testing.GuiTestHelper;

public class SimpleTextureTest {
	
	/**
	 * This method tests the texture selection for non-bow custom items. It tests if the
	 * editor refuses to continue without selecting a texture and if a texture can
	 * actually be selected.
	 * This test should be run from an item edit menu and should end in that same menu.
	 * @param test The test instance
	 * @param textureName The name of the texture the custom item should get
	 */
	public static void test(GuiTestHelper test, String textureName) {
		test.click("Create");
		test.assertComponentWithText("Every item needs a texture");
		test.click("None");
		test.click(textureName);
	}
}
