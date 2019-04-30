package nl.knokko.customitems.editor.test.editmenu.item;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import nl.knokko.gui.component.menu.TextArrayEditMenu;
import nl.knokko.gui.testing.GuiTestHelper;
import nl.knokko.gui.testing.GuiTestProgram;
import nl.knokko.gui.testing.TestException;

public class CreateSimpleItemTest1 implements GuiTestProgram {
	
	private static final String LORE1 = "The very first line of lore";
	private static final String LORE2 = "The second line of lore";

	@Override
	public void test(GuiTestHelper test) {
		test.click("Items");
		test.assertComponentsWithTexts("Back", "Create item");
		test.click("Create item");
		test.assertComponentsWithTexts("Cancel", "Simple Item", "Sword", "Axe", "Pickaxe", "Shovel", "Hoe",
				"Shear", "Bow", "Helmet", "Chestplate", "Leggings", "Boots");
		test.click("Simple Item");
		test.assertComponentsWithTexts("Cancel", "Create", "Name: ", "Internal item type: ", 
				"Internal item damage: ", "Display name: ", "Lore: ", "Attribute modifiers: ",
				"Default enchantments: ", "Texture: ", "", "Diamond hoe", "1", "Change lore...",
				"Change attributes...", "Change enchantments...", "None", "Max stacksize:", "64");
		test.click("Create");
		test.assertComponentWithText("You can't leave the name empty.");
		test.clickNearest("", "Name: ", 2);
		test.type("simple test item");
		test.click("Create");
		test.assertComponentWithText("The _ character is the only special character that is allowed in names.");
		test.click("simple test item");
		test.pressAndRelease(KeyEvent.VK_BACK_SPACE, 10);
		test.type("_test_item");
		test.click("Create");
		test.assertComponentWithText("Every item needs a texture");
		test.click("None");
		test.click("simple_test_one");
		test.click("");
		test.type("Test Item 1");
		test.assertComponentWithText("simple_test_one");
		test.click("64");
		test.pressAndRelease(KeyEvent.VK_BACK_SPACE);
		test.type('1');
		test.click("Change lore...");
		test.assertComponentsWithTexts("Cancel", "Add line", "Apply");
		test.click("Add line");
		test.click("Text...");
		test.pressAndRelease(KeyEvent.VK_BACK_SPACE, 7);
		test.type(LORE1);
		test.click("Add line");
		test.click("Text...");
		test.pressAndRelease(KeyEvent.VK_BACK_SPACE, 7);
		test.type(LORE2);
		test.clickNearestImage(LORE1, TextArrayEditMenu.ADD_IMAGE, 4);
		float y1 = test.getComponentWithText(LORE1).getState().getMinY();
		if (test.getComponentWithText("").getState().getMinY() < y1) {
			throw new TestException("The new line should be higher");
		}
		test.clickNearestImage(LORE1, TextArrayEditMenu.DELETE_IMAGE, 6);
		if (test.getComponentWithText(LORE2).getState().getMinY() != y1){
			throw new TestException("The minY of the second component should equal the previous minY of the first lore");
		}
		test.click("Apply");
		test.click("Change lore...");
		test.assertComponentsWithTexts("Cancel", "Add line", "Apply", LORE2, "");
		if (test.getComponentWithText(LORE2).getState().getMinY() != y1){
			throw new TestException("The minY of the second component should not change after applying and coming back");
		}
		test.click("Cancel");
		test.click("Change attributes...");
		test.assertComponentsWithTexts("Cancel", "New Attribute", "Apply");
		test.click("New Attribute");
		BufferedImage deleteImage = (BufferedImage) test.getComponentWithText("Slot").getState().getWindow().getTextureLoader().loadTexture("nl/knokko/gui/images/icons/delete.png").getImage();
		test.assertImageShown(deleteImage);
		test.assertComponentsWithTexts("generic.attackDamage", "mainhand", "Add", "Value: ", "5.0");
	}
}