package nl.knokko.customitems.editor.test.editmenu.item;

import java.awt.image.BufferedImage;

import nl.knokko.gui.testing.GuiTestHelper;

public class EnchantmentsTest {
	
	public static void test(GuiTestHelper test, String enchantment1, String level1, String enchantment2, String level2) {
		test.click("Change enchantments...");
		test.assertComponentsWithTexts("Cancel", "New Enchantment", "Apply");
		test.click("New Enchantment");
		test.assertComponentsWithTexts("durability", "Level: ", "2");
		BufferedImage deleteImage = (BufferedImage) test.getComponentWithText("Level: ").getState().getWindow().getTextureLoader().loadTexture("nl/knokko/gui/images/icons/delete.png").getImage();
		test.assertImageShown(deleteImage);
		test.click("durability");
		test.click(enchantment1);
		test.click("2");
		test.backspace(1);
		test.type(level1);
		test.click("New Enchantment");
		test.clickNearestImage("New Enchantment", deleteImage, 2);
		test.assertComponentsWithTexts(enchantment1, level1);
		test.click("New Enchantment");
		test.click("durability");
		test.click(enchantment2);
		test.clickNearest("2", "New Enchantment", level1.equals("2") ? 2 : 1);
		test.backspace(1);
		test.type(level2);
		test.assertComponentWithText(level2);
		test.click("Apply");
		test.click("Change enchantments...");
		test.assertComponentsWithTexts(enchantment1, level1, enchantment2, level2);
		test.click("Cancel");
	}
}