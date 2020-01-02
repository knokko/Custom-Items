package nl.knokko.customitems.editor.test.editmenu.item;

import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.gui.testing.GuiTestHelper;

public class CreateBowTest {
	
	public static void create(GuiTestHelper test, String itemName, String textureName, 
			String maxUses, String repairItemCategory, String repairItem, 
			String attackDurLoss, String breakDurLoss, String shootDurLoss,
			String lore1, String lore2, 
			String damageMultiplier, String speedMultiplier, String knockbackStrength,
			String attribute1, String slot1, String op1, String value1, 
			String attribute2, String slot2, String op2, String value2, 
			String enchantment1, String level1, String enchantment2, String level2) {
		test.click("Create item");
		test.click("Bow");
		test.assertComponentsWithTexts("Cancel", "Create", "Name: ", "Internal item type: ",
				"INTERNAL item damage: ", "Display name: ", "Lore: ", "Attribute modifiers: ",
				"Default enchantments: ", "Item flags: ", "Texture: ", "", "1", "Change...",
				"None", "Allow enchanting", "Allow anvil actions", "Max uses: ", "Repair item: ",
				"Durability loss on attack:", "Durability loss on block break:",
				"Durability loss on shooting:", "Damage multiplier: ", "Speed multiplier: ",
				"knockback strength: ", "Arrow gravity", "500", "0", "1", "1");
		SimpleTextureTest.test(test, textureName);
		ItemNameTest.test(test, "fragile_bow");
		DisplayNameTest.test(test, itemName);
		LoreTest.test(test, lore1, lore2, 7);
		
		AttributeModTest.test(test, "generic.movementSpeed", "Offhand", "Add factor", "1.5", 
				attribute1, slot1, op1, value1, attribute2, slot2, op2, value2, 7);
		EnchantmentsTest.test(test, enchantment1, level1, enchantment2, level2, 7);
		CreateToolTest.attackDurabilityLoss(test, CustomItemType.BOW, attackDurLoss, 10);
		CreateToolTest.breakDurabilityLoss(test, CustomItemType.BOW, breakDurLoss, 10);
		CreateToolTest.maxUses(test, "500", maxUses);
		CreateToolTest.repairItem(test, repairItemCategory, repairItem);
		
		// The next stuff is bows only
		test.clickNearestEdit("Durability loss on shooting:", 10);
		test.backspace(1);
		test.type(shootDurLoss);
		test.clickNearestEdit("Damage multiplier: ", 10);
		test.backspace(8);
		test.type(damageMultiplier);
		test.clickNearestEdit("Speed multiplier: ", 10);
		test.backspace(8);
		test.type(speedMultiplier);
		test.clickNearestEdit("knockback strength: ", 10);
		test.backspace(1);
		test.type(knockbackStrength);
		test.uncheck("Arrow gravity", 3);
		test.uncheck("Allow enchanting", 3);
		test.click("Create");
	}
}