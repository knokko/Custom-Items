package nl.knokko.customitems.plugin.item;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.objects.ObjectSet;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import org.bukkit.inventory.ItemStack;

public class CustomItems {
	
	private static Map<Material, Short2ObjectMap<CustomItem>> customItemMap = new EnumMap<Material, Short2ObjectMap<CustomItem>>(Material.class);
	
	public static CustomItem getCustomItem(ItemStack item) {
		if(item != null && item.hasItemMeta() && item.getItemMeta().isUnbreakable()) {
			Short2ObjectMap<CustomItem> map = customItemMap.get(item.getType());
			if(map != null) {
				return map.get(item.getDurability());
			}
		}
		return null;
	}
	
	public static CustomItem getCustomItem(String name) {
		Set<Entry<Material, Short2ObjectMap<CustomItem>>> entrySet = customItemMap.entrySet();
		for(Entry<Material, Short2ObjectMap<CustomItem>> entry : entrySet) {
			ObjectSet<Entry<Short, CustomItem>> set = entry.getValue().entrySet();
			for(Entry<Short, CustomItem> innerEntry : set) {
				if(innerEntry.getValue().getName().equals(name)) {
					return innerEntry.getValue();
				}
			}
		}
		return null;
	}
	
	public static void register(CustomItem item) {
		Short2ObjectMap<CustomItem> map = customItemMap.get(item.getMaterial());
		if(map == null) {
			map = new org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.shorts.Short2ObjectAVLTreeMap<>();
			customItemMap.put(item.getMaterial(), map);
		}
		map.put(item.getItemDamage(), item);
	}
}