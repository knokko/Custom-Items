package nl.knokko.customitems.test;

import java.util.ArrayList;

import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItem;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.ItemFlag;

public class TestCustomItem extends CustomItem {

	public TestCustomItem(String name) {
		super(CustomItemType.BOW, (short) 2, name, "Display" + name, 
				new String[0], new AttributeModifier[0], new Enchantment[0], 
				new boolean[ItemFlag.values().length], new ArrayList<>(0),
				new ArrayList<>(0), new String[0]);
	}
}
