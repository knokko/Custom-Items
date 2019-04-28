package nl.knokko.customitems.editor.test;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import nl.knokko.gui.testing.GuiTestHelper;
import nl.knokko.gui.testing.GuiTestProgram;

/**
 * A test to check if it is possible to load simple textures. A quicker test will be run after saving and
 * loading to check if saving went well. This test should be run from the texture overview and it will end at
 * the edit menu.
 * @author knokko
 *
 */
public class CreateSimpleTextureTest1 implements GuiTestProgram {

	@Override
	public void test(GuiTestHelper test) {
		test.click("Load texture");
		test.click("Load simple texture");
		test.click("Create");
		test.assertComponentWithText("You have to select an image before you can create this.");
		test.click("Edit...");
		test.click("autotest0.png");
		test.click("Select");
		test.assertComponentWithText("The width (16) of this image should be equal to the height (15)");
		test.click("Edit...");
		test.click("autotest00.png");
		test.click("Select");
		test.assertComponentWithText("The width and height (30) should be a power of 2");
		test.click("Create");
		test.assertComponentWithText("You have to select an image before you can create this.");
		BufferedImage image5 = test.createImage("autotest5.png", 64, 64);
		test.click("Edit...");
		test.click("autotest5.png");
		test.click("Select");
		test.assertImageShown(image5);
		test.click("Create");
		test.assertComponentWithText("You can't leave the name empty.");
		test.click("");
		test.type("simple_test_1");
		test.assertComponentWithText("simple_test_1");
		test.click("Create");
		test.assertComponentWithText("The _ character is the only special character that is allowed in names.");
		test.click("simple_test_1");
		test.pressAndRelease(KeyEvent.VK_BACK_SPACE, 2);
		test.click("Create");
		test.assertComponentsWithTexts("Back", "Load texture", "Edit", "Delete", "simple_test", "test_bow_first");
		test.clickNearest("Edit", "simple_test", 2);
		test.assertImageShown(image5);
		test.click("simple_test");
		test.type("_one");
		test.assertComponentWithText("simple_test_one");
		test.click("Apply");
		test.assertComponentWithText("simple_test_one");
		test.clickNearest("Edit", "simple_test_one", 2);
		test.assertComponentWithText("Edit...");
		test.assertComponentWithText("simple_test_one");
		test.click("Cancel");
		test.click("Back");
	}
}