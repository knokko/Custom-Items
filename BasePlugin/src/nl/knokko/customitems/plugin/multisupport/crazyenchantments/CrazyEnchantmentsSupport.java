/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2019 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package nl.knokko.customitems.plugin.multisupport.crazyenchantments;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import nl.knokko.customitems.plugin.CustomItemsPlugin;

public class CrazyEnchantmentsSupport {
	
	public static void onEnable() {
		try {
			Class.forName("me.badbones69.crazyenchantments.Main");
			
			
			// Check whether we should use new support or legacy support
			try {
				Class.forName(
						"me.badbones69.crazyenchantments.api.enums.CEnchantments"
				);
				
				Bukkit.getLogger().info("Loading new CrazyEnchantments support");
				
				// Load support for the newer versions
				Bukkit.getPluginManager().registerEvents((Listener) Class.forName(
						"nl.knokko.customitems.plugin.multisupport.crazyenchantments.CrazyEnchantmentsEventHandler"
				).newInstance(), CustomItemsPlugin.getInstance());
			} catch (ClassNotFoundException useLegacy) {
				
				Bukkit.getLogger().info("Loading legacy CrazyEnchantments support");
				
				// Load support for the legacy versions
				Bukkit.getPluginManager().registerEvents((Listener) Class.forName(
						"nl.knokko.customitems.plugin.multisupport.crazyenchantments.CrazyEnchantmentsEventHandlerLegacy"
				).newInstance(), CustomItemsPlugin.getInstance());
			}
		} catch (ClassNotFoundException ex) {
			Bukkit.getLogger().info("Can't load class me.badbones69.crazyenchantments.Main, so I assume Crazy Enchantments is not installed.");
		} catch (InstantiationException e) {
			throw new Error("It should be possible to instantiate CrazyEnchantmentsEventHandler", e);
		} catch (IllegalAccessException e) {
			throw new Error("CrazyEnchantmentsEventHandler should be accessible", e);
		}
	}
}