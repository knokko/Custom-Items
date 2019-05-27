package nl.knokko.customitems.editor.test.editmenu.texture;

import java.awt.image.BufferedImage;

import nl.knokko.gui.testing.GuiTestHelper;

public class CreateSimpleTextureTest1 {

	/**
	 * A test to check if it is possible to load simple textures. A quicker test will be run after saving and
	 * loading to check if saving went well. This test should be run from the texture overview and it will 
	 * end at the edit menu.
	 * @param test
	 */
	public static void test(GuiTestHelper test, String textureName) {
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
		test.type(textureName + "_1");
		test.assertComponentWithText(textureName + "_1");
		test.click("Create");
		test.assertComponentWithText("The _ character is the only special character that is allowed in names.");
		test.click(textureName + "_1");
		test.backspace(2);
		test.click("Create");
		test.assertComponentsWithTexts("Back", "Load texture", "Edit", "Delete", textureName);
		test.clickNearest("Edit", textureName, 2);
		test.assertImageShown(image5);
		test.click(textureName);
		test.type("_one");
		test.assertComponentWithText(textureName + "_one");
		test.click("Apply");
		test.assertComponentWithText(textureName + "_one");
		test.clickNearest("Edit", textureName + "_one", 2);
		test.assertComponentWithText("Edit...");
		test.assertComponentWithText(textureName + "_one");
		test.click("Cancel");
		test.click("Back");
	}
}