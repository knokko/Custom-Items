package nl.knokko.customitems.plugin.util;

import org.bukkit.inventory.ItemStack;

import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.set.item.CustomItem;

public class ItemUtils {

	public static boolean isEmpty(ItemStack stack) {
		return stack == null || 
				ItemHelper.getMaterialName(stack) == CIMaterial.AIR.name() ||
				stack.getAmount() == 0;
	}
	
	public static int getMaxStacksize(ItemStack stack) {
		if (CustomItem.isCustom(stack)) {
			CustomItem customItem = CustomItemsPlugin.getInstance().getSet().getItem(stack);
			if (customItem != null) {
				return customItem.getMaxStacksize();
			}
		}
		
		return stack.getMaxStackSize();
	}
}
