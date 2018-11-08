package nl.knokko.customitems.plugin.multisupport.crazyenchantments;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import nl.knokko.customitems.plugin.CustomItemsPlugin;

public class CrazyEnchantmentsSupport {
	
	public static void onEnable() {
		try {
			Class.forName("me.badbones69.crazyenchantments.Main");
			Bukkit.getPluginManager().registerEvents((Listener) Class.forName("nl.knokko.customitems.plugin.multisupport.crazyenchantments.CrazyEnchantmentsEventHandler").newInstance(), CustomItemsPlugin.getInstance());
		} catch (ClassNotFoundException ex) {
			Bukkit.getLogger().info("Can't load class me.badbones69.crazyenchantments.Main, so I assume Crazy Enchantments is not installed.");
		} catch (InstantiationException e) {
			throw new Error("It should be possible to instantiate CrazyEnchantmentsEventHandler", e);
		} catch (IllegalAccessException e) {
			throw new Error("CrazyEnchantmentsEventHandler should be accessible", e);
		}
	}
}