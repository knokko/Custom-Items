package nl.knokko.customitems.editor.test.editmenu.item;

import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.gui.testing.GuiTestHelper;

public class CreateShearsTest {
	
	public static void create(GuiTestHelper test, String itemName, String textureName, 
			String maxUses, String repairItemCategory, String repairItem, 
			String attackDurLoss, String breakDurLoss, String shearDurLoss,
			String lore1, String lore2,
			String attribute1, String slot1, String op1, String value1, String attribute2, String slot2, String op2,
			String value2, String enchantment1, String level1, String enchantment2, String level2) {
		test.click("Create item");
		test.click("Shear");
		test.assertComponentsWithTexts("Cancel", "Create", "Name: ", "Internal item type: ",
				"Internal item damage: ", "Display name: ", "Lore: ", "Attribute modifiers: ",
				"Default enchantments: ", "Item flags: ", "Texture: ", "", "1", "Change lore...",
				"Change attributes...", "Change enchantments...", "Change flags...", "None",
				"Allow enchanting", "Allow anvil actions", "Max uses: ", "Repair item: ",
				"Durability loss on attack:", "Durability loss on block break:", "Durability loss on shearing:",
				"500", "0", "1");
		ItemNameTest.test(test, itemName);
		SimpleTextureTest.test(test, textureName);
		DisplayNameTest.test(test, itemName);
		LoreTest.test(test, lore1, lore2);
		
		CustomItemType customItemType = CustomItemType.SHEARS;
		AttributeModTest.test(test, "generic.movementSpeed", "offhand", "Multiply", "1.5", attribute1, slot1, op1, value1, attribute2, slot2, op2, value2);
		EnchantmentsTest.test(test, enchantment1, level1, enchantment2, level2);
		CreateToolTest.toolOnly(test, customItemType, maxUses, repairItemCategory, repairItem, attackDurLoss, breakDurLoss, 7);
		
		// Shears only:
		test.clickNearestEdit("Durability loss on shearing:", 7);
		test.backspace(1);
		test.click("Create");
		test.assertComponentWithText("The shear durability loss must be a positive integer");
		test.clickNearestEdit("Durability loss on shearing:", 7);
		test.type(shearDurLoss);
		test.assertComponentWithText(shearDurLoss);
		
		// Now end like we always end
		test.click("Create");
		test.assertComponentsWithTexts("Create item", itemName);
	}
}