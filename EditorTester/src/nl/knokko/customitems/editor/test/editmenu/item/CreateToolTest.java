package nl.knokko.customitems.editor.test.editmenu.item;

import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.CustomToolDurability;
import nl.knokko.gui.testing.GuiTestHelper;

public class CreateToolTest {
	
	/**
	 * The test for creating a custom tool. It should be started from the Item Overview and it will end in
	 * the Item Overview.
	 * @param test The test instance
	 * @param toolType The tool type of the item to create, it should match one of the buttons you can
	 * click after clicking on 'Create item'.
	 * @param itemName The name that should be given to the item to create
	 * @param textureName The name of the texture the item to create should get
	 * @param maxUses The maximum number of uses the item to create has
	 * @param repairItemCategory The category of the repair item. It should be one of the following:
	 * 'Custom Item', 'Simple vanilla item', 'Vanilla item with datavalue', 'Empty'
	 * @param repairItem The name of the repair item, it will be ignored if the repairItemCategory is empty
	 * @param attackDurLoss The amount of durability that the tool will lose upon attacking creatures
	 * @param breakDurLoss The amount of durability that the tool will lose upon breaking blocks
	 * @param lore1 The first line of lore of the tool to create
	 * @param lore2 The second line of lore of the tool to create
	 * @param attribute1 The first attribute modifier name of the tool to create
	 * @param slot1 The first attribute modifier slot of the tool to create
	 * @param op1 The first attribute modifier operation of the tool to create
	 * @param value1 The first attribute modifier value of the tool to create
	 * @param attribute2 The second attribute modifier name of the tool to create
	 * @param slot2 The second attribute modifier slot of the tool to create
	 * @param op2 The second attribute modifier operation of the tool to create
	 * @param value2 The second attribute modifier value of the tool to create
	 * @param enchantment1 The first enchantment name of the tool to create
	 * @param level1 The first enchantment level of the tool to create
	 * @param enchantment2 The second enchantment name of the tool to create
	 * @param level2 The second enchantment level of the tool to create
	 */
	public static void create(GuiTestHelper test, String toolType, String itemName, String textureName, 
			String maxUses, String repairItemCategory, String repairItem, String attackDurLoss, String breakDurLoss,
			String lore1, String lore2,
			String attribute1, String slot1, String op1, String value1, String attribute2, String slot2, String op2,
			String value2, String enchantment1, String level1, String enchantment2, String level2) {
		test.click("Create item");
		test.click(toolType);
		test.assertComponentsWithTexts("Cancel", "Create", "Name: ", "Internal item type: ",
				"Internal item damage: ", "Display name: ", "Lore: ", "Attribute modifiers: ",
				"Default enchantments: ", "Item flags: ", "Texture: ", "", "1", "Change lore...",
				"Change attributes...", "Change enchantments...", "Change flags...", "None",
				"Allow enchanting", "Allow anvil actions", "Max uses: ", "Repair item: ",
				"Durability loss on attack: ", "Durability loss on block break: ", "500", "2", "1");
		ItemNameTest.test(test, itemName);
		SimpleTextureTest.test(test, textureName);
		DisplayNameTest.test(test, itemName);
		LoreTest.test(test, lore1, lore2);
		AttributeModTest.test(test, attribute1, slot1, op1, value1, attribute2, slot2, op2, value2);
		EnchantmentsTest.test(test, enchantment1, level1, enchantment2, level2);
		
		// Not my most pretty solution ever, but will do the trick
		toolOnly(test, CustomItemType.valueOf("IRON_" + toolType.toUpperCase()), maxUses, repairItemCategory, repairItem, attackDurLoss, breakDurLoss);
		test.click("Create");
		test.assertComponentsWithTexts("Create item", itemName);
	}
	
	public static void repairItem(GuiTestHelper test, String repairItemCategory, String repairItem) {
		test.assertComponentWithText("Repair item: ");
		test.click("None");
		test.assertComponentsWithTexts("Back", "Change", "None");
		test.click("Change");
		test.assertComponentsWithTexts("Cancel", "Custom item", "Simple vanilla item", 
				"Vanilla item with datavalue", "Empty");
		test.click(repairItemCategory);
		if (!repairItemCategory.equals("Empty")) {
			test.click(repairItem);
			if (repairItemCategory.equals("Vanilla item with datavalue")) {
				test.click("OK");
			}
		}
		if (repairItemCategory.equals("Vanilla item with datavalue")) {
			test.assertComponentWithText(repairItem + "(0)");
		} else {
			test.assertComponentWithText(repairItem);
		}
	}
	
	public static void attackDurabilityLoss(GuiTestHelper test, CustomItemType type, String attackDurLoss) {
		int defaultAttackDurLoss = CustomToolDurability.defaultEntityHitDurabilityLoss(type);
		test.click(defaultAttackDurLoss + "");
		test.backspace(1);
		test.click("Create");
		test.assertComponentWithText("The entity hit durability loss should be a positive integer");
		test.click("");
		test.type(attackDurLoss);
	}
	
	public static void breakDurabilityLoss(GuiTestHelper test, CustomItemType type, String breakDurLoss) {
		int defaultBreakDurLoss = CustomToolDurability.defaultBlockBreakDurabilityLoss(type);
		test.click(defaultBreakDurLoss + "");
		test.backspace(1);
		test.click("Create");
		test.assertComponentWithText("The block break durability loss should be a positive integer");
		test.click("");
		test.type(breakDurLoss);
	}
	
	public static void maxUses(GuiTestHelper test, String currentMaxUses, String newMaxUses) {
		test.click(currentMaxUses);
		
		// Better too much backspace than too little
		test.backspace(15);
		test.click("Create");
		test.assertComponentWithText("The durability must ben an integer");
		test.click("");
		test.type(newMaxUses);
	}
	
	public static void toolOnly(GuiTestHelper test, CustomItemType type, String maxUses, String repairItemCategory, 
			String repairItem, String attackDurLoss, String breakDurLoss) {
		test.uncheck("Allow enchanting", 2);
		test.uncheck("Allow anvil actions", 2);
		test.check("Allow enchanting", 2);
		maxUses(test, "500", maxUses);
		repairItem(test, repairItemCategory, repairItem);
		attackDurabilityLoss(test, type, attackDurLoss);
		breakDurabilityLoss(test, type, breakDurLoss);
	}
}