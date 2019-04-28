package nl.knokko.customitems.editor.test;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import nl.knokko.gui.testing.GuiTestHelper;
import nl.knokko.gui.testing.GuiTestProgram;

/**
 * The first test for creating bows. Another less advanced test will be run after the item set has been
 * saved and load once. This test should be run from the edit menu. This test will stop at the texture
 * overview.
 * @author knokko
 *
 */
public class CreateBowTextureTest1 implements GuiTestProgram {

	@Override
	public void test(GuiTestHelper test) {
		test.click("Textures");
		test.click("Load texture");
		test.click("Load bow texture");
		test.click("Create");
		test.assertComponentWithText("You need to give this bow a base texture.");
		
		test.createImage("autotest0.png", 16, 15);
		test.createImage("autotest00.png", 30, 30);
		BufferedImage image1 = test.createImage("autotest1.png", 32, 32);
		BufferedImage image2 = test.createImage("autotest2.png", 16, 16);
		BufferedImage image3 = test.createImage("autotest3.png", 64, 64);
		BufferedImage image4 = test.createImage("autotest4.png", 128, 128);
		
		test.clickNearest("Edit...", test.getComponentWithText("Base texture: "), 0, 4);
		test.click("autotest0.png");
		test.click("Select");
		test.assertComponentWithText("The width (16) of this image should be equal to the height (15)");
		test.click("Create");
		test.assertComponentWithText("You need to give this bow a base texture.");
		test.clickNearest("Edit...", "Base texture: ", 4);
		test.click("autotest1.png");
		test.click("Select");
		test.assertNearestImage("Base texture: ", image1, 4);
		test.click("Create");
		test.assertComponentWithText("You can't leave the name empty.");
		test.click("");
		test.type("test_bow1");
		test.click("Create");
		test.assertComponentWithText("The _ character is the only special character that is allowed in names.");
		test.click("test_bow1");
		test.pressAndRelease(KeyEvent.VK_BACK_SPACE);
		test.type("_first");
		test.click("Create");
		test.assertComponentWithText("Pull 0.0 doesn't have a texture");
		test.clickNearest("Edit...", "0.0", 4);
		test.click("autotest0.png");
		test.click("Select");
		test.assertComponentWithText("The width (16) of this image should be equal to the height (15)");
		test.clickNearest("Edit...", "0.0", 4);
		test.click("autotest1.png");
		test.click("Select");
		test.assertNearestImage("0.0", image1, 5);
		test.click("Create");
		test.assertComponentWithText("Pull 0.65 doesn't have a texture");
		test.clickNearest("Edit...", "0.65", 4);
		test.click("autotest00.png");
		test.click("Select");
		test.assertComponentWithText("The width and height (30) should be a power of 2");
		test.clickNearest("Edit...", "0.65", 4);
		test.click("autotest2.png");
		test.click("Select");
		test.assertNearestImage("0.65", image2, 6);
		test.click("0.65");
		test.pressAndRelease(KeyEvent.VK_BACK_SPACE, 2);
		test.type("45");
		test.click("0.9");
		test.pressAndRelease(KeyEvent.VK_BACK_SPACE);
		test.type('6');
		test.clickNearest("Edit...", "0.6", 4);
		test.click("autotest3.png");
		test.click("Select");
		test.assertNearestImage("0.6", image3, 7);
		test.click("Add pull");
		test.click("0.3");
		test.pressAndRelease(KeyEvent.VK_BACK_SPACE);
		test.type("95");
		test.clickNearest("Edit...", "0.95", 5);
		test.click("autotest4.png");
		test.click("Select");
		test.assertNearestImage("0.95", image4, 9);
		test.click("Create");
		test.assertComponentsWithTexts("Back", "Load texture", "test_bow_first", "Edit", "Delete");
		test.click("Edit");
		test.assertComponentsWithTexts("Cancel", "Apply", "Add pull", "Name: ", "Base texture: ", "Edit...",
				"test_bow_first", "0.0", "0.45", "0.6", "0.95");
		test.assertNearestImage("Base texture: ", image1, 9);
		test.assertNearestImage("0.0", image1, 9);
		test.assertNearestImage("0.45", image2, 9);
		test.assertNearestImage("0.6", image3, 9);
		test.assertNearestImage("0.95", image4, 9);
		test.click("0.45");
		test.pressAndRelease(KeyEvent.VK_BACK_SPACE, 2);
		test.type("35");
		test.assertComponentWithText("0.35");
		test.click("Apply");
		test.click("Edit");
		test.assertComponentWithText("0.35");
		test.click("Cancel");
	}
}