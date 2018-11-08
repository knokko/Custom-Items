package nl.knokko.customitems.plugin.multisupport.crazyenchantments;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.api.CEnchantments;
import me.badbones69.crazyenchantments.api.events.HellForgedUseEvent;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.set.item.CustomItem;
import nl.knokko.customitems.plugin.set.item.CustomTool;

public class CrazyEnchantmentsEventHandler implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onHellForge(HellForgedUseEvent event) {
		ItemStack item = event.getItem();
		if (CustomItem.isCustom(item)) {
			CustomItem custom = CustomItemsPlugin.getInstance().getSet().getItem(item);
			if (custom != null) {
				event.setCancelled(true);
				if (custom instanceof CustomTool) {
					CustomTool tool = (CustomTool) custom;
					tool.increaseDurability(item, Main.CE.getPower(item, CEnchantments.HELLFORGED));
				}
			}
		}
	}
}