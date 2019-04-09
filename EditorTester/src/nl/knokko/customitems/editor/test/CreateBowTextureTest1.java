package nl.knokko.customitems.editor.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import nl.knokko.gui.testing.GuiTestHelper;
import nl.knokko.gui.testing.GuiTestProgram;
import nl.knokko.gui.testing.TestException;

/**
 * The first test for creating bows. Another less advanced test will be run after the item set has been
 * saved and load once. This test should be run from the edit menu.
 * @author knokko
 *
 */
public class CreateBowTextureTest1 implements GuiTestProgram {
	
	private static final BufferedImage BAD_SIZED_IMAGE = new BufferedImage(16, 15, BufferedImage.TYPE_INT_ARGB);
	private static final BufferedImage BAD_SIZED_IMAGE2 = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
	private static final BufferedImage IMAGE1 = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
	private static final BufferedImage IMAGE2 = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
	private static final BufferedImage IMAGE3 = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
	private static final BufferedImage IMAGE4 = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
	
	static {
		Graphics2D g = IMAGE1.createGraphics();
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, 32, 32);
		g.dispose();
		g = IMAGE2.createGraphics();
		g.setColor(Color.green);
		g.fillRect(0, 0, 16, 16);
		g.dispose();
		g = IMAGE3.createGraphics();
		g.setColor(Color.RED);
		g.fillRect(0, 0, 64, 64);
		g.dispose();
		g = IMAGE4.createGraphics();
		g.setColor(Color.YELLOW);
		g.fillRect(0, 0, 128, 128);
		g.dispose();
	}

	@Override
	public void test(GuiTestHelper test) {
		test.setDelayTime(50);
		
		test.click("Textures");
		test.click("Load texture");
		test.click("Load bow texture");
		test.click("Create");
		test.assertComponentWithText("You need to give this bow a base texture.");
		
		try {
			File folder = new File(".").getAbsoluteFile().getParentFile();
			ImageIO.write(BAD_SIZED_IMAGE, "PNG", new File(folder + "/autotest0.png"));
			ImageIO.write(BAD_SIZED_IMAGE2, "PNG", new File(folder + "/autotest00.png"));
			ImageIO.write(IMAGE1, "PNG", new File(folder + "/autotest1.png"));
			ImageIO.write(IMAGE2, "PNG", new File(folder + "/autotest2.png"));
			ImageIO.write(IMAGE3, "PNG", new File(folder + "/autotest3.png"));
			ImageIO.write(IMAGE4, "PNG", new File(folder + "/autotest4.png"));
		} catch (IOException ioex) {
			throw new TestException("Couldn't write necessary images for bow texture test 1:", ioex);
		}
		
		test.clickNearest("Edit...", test.getComponentWithText("Base texture: "), 0, 4);
		test.click("autotest0.png");
		test.click("Select");
		test.assertComponentWithText("The width (16) of this image should be equal to the height (15)");
		test.click("Create");
		test.assertComponentWithText("You need to give this bow a base texture.");
		test.clickNearest("Edit...", "Base texture: ", 4);
		test.click("autotest1.png");
		test.click("Select");
		test.click("Create");
		test.assertComponentWithText("You can't leave the name empty.");
		test.click("");
		test.setDelayTime(20);
		test.type("test_bow1");
		test.setDelayTime(50);
		test.click("Create");
		test.assertComponentWithText("The _ character is the only special character that is allowed in names.");
		test.click("test_bow1");
		test.pressAndRelease(KeyEvent.VK_BACK_SPACE);
		test.setDelayTime(20);
		test.type("_first");
		test.setDelayTime(50);
		test.click("Create");
		test.assertComponentWithText("Pull 0.0 doesn't have a texture");
		test.clickNearest("Edit...", "0.0", 4);
		test.click("autotest0.png");
		test.click("Select");
		test.assertComponentWithText("The width (16) of this image should be equal to the height (15)");
		test.clickNearest("Edit...", "0.0", 4);
		test.click("autotest1.png");
		test.click("Select");
		test.click("Create");
		test.assertComponentWithText("Pull 0.65 doesn't have a texture");
		test.clickNearest("Edit...", "0.65", 4);
		test.click("autotest00.png");
		test.click("Select");
		test.assertComponentWithText("The width and height (30) should be a power of 2");
		test.clickNearest("Edit...", "0.65", 4);
		test.click("autotest2.png");
		test.click("Select");
		test.click("0.65");
		test.pressAndRelease(KeyEvent.VK_BACK_SPACE, 2);
		test.type("45");
		test.click("0.9");
		test.pressAndRelease(KeyEvent.VK_BACK_SPACE);
		test.type('6');
		test.clickNearest("Edit...", "0.6", 4);
		test.click("autotest3.png");
		test.click("Select");
		test.click("Add pull");
		test.click("0.3");
		test.pressAndRelease(KeyEvent.VK_BACK_SPACE);
		test.type("95");
		test.clickNearest("Edit...", "0.95", 5);
		test.click("autotest4.png");
		test.click("Select");
		test.click("Create");
		test.assertComponentWithText("Ehm...");
	}
}