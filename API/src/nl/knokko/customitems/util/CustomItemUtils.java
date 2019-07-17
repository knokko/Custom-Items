package nl.knokko.customitems.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CustomItemUtils {
	
	public static final boolean IS_PLUGIN;
	
	private static final Method getBukkitMaterial;
	private static final Method getBukkitEntityType;
	
	static {
		{
			boolean isPlugin = false;
			try {
				Class.forName("org.bukkit.Bukkit");
				isPlugin = true;
			} catch (ClassNotFoundException nope) {
				// If the class can't be found, isPlugin will stay false
			}
			IS_PLUGIN = isPlugin;
		}
		
		if (IS_PLUGIN) {
			try {
				getBukkitMaterial = Class.forName("org.bukkit.Material").getMethod("valueOf", String.class);
				getBukkitEntityType = Class.forName("org.bukkit.entity.EntityType").getMethod("valueOf", String.class);
			} catch (Exception ex) {
				throw new Error("Failed to get the valueOf method of org.bukkit.Material", ex);
			}
		} else {
			getBukkitMaterial = null;
			getBukkitEntityType = null;
		}
	}
	
	public static Object getBukkitMaterial(String name) throws IllegalArgumentException, UnsupportedOperationException {
		if (getBukkitMaterial == null) {
			throw new UnsupportedOperationException("The Bukkit API is not available");
		}
		try {
			return getBukkitMaterial.invoke(null, name);
		} catch (InvocationTargetException ex) {
			throw new IllegalArgumentException(ex);
		} catch (IllegalAccessException e) {
			throw new Error("org.bukkit.Material.valueOf should be public");
		}
	}
	
	public static Object getBukkitEntityType(String name) throws IllegalArgumentException, UnsupportedOperationException {
		if (getBukkitEntityType == null) {
			throw new UnsupportedOperationException("The Bukkit API is not available");
		}
		try {
			return getBukkitEntityType.invoke(null, name);
		} catch (InvocationTargetException ex) {
			throw new IllegalArgumentException(ex);
		} catch (IllegalAccessException e) {
			throw new Error("org.bukkit.entity.EntityType.valueOf should be public");
		}
	}
}
