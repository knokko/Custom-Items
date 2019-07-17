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
package nl.knokko.customitems.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import nl.knokko.customitems.plugin.command.CommandCustomItems;
import nl.knokko.customitems.plugin.multisupport.crazyenchantments.CrazyEnchantmentsSupport;
import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitInputStream;

public class CustomItemsPlugin extends JavaPlugin {

	private static CustomItemsPlugin instance;

	private ItemSet set;
	private LanguageFile languageFile;

	public static CustomItemsPlugin getInstance() {
		return instance;
	}

	public CustomItemsPlugin() {
	}

	@Override
	public void onEnable() {
		super.onEnable();
		instance = this;
		languageFile = new LanguageFile(new File(getDataFolder() + "/lang.yml"));

		// Load the set after creating language file instance because the set needs the
		// durability prefix
		loadSet();
		getCommand("customitems").setExecutor(new CommandCustomItems(languageFile));
		Bukkit.getPluginManager().registerEvents(new CustomItemsEventHandler(), this);
		CrazyEnchantmentsSupport.onEnable();
	}

	@Override
	public void onDisable() {
		instance = null;
		super.onDisable();
	}

	private void loadSet() {
		File folder = getDataFolder();
		folder.mkdirs();
		File[] files = folder.listFiles((File file, String name) -> {
			return name.endsWith(".cis");
		});
		if (files != null) {
			if (files.length == 1) {
				File file = files[0];
				try {
					BitInput input = new BitInputStream(new FileInputStream(file));
					set = new ItemSet(input);
					input.terminate();
				} catch (Exception ex) {
					Bukkit.getLogger().log(Level.SEVERE, "Failed to load the custom item set " + file, ex);
					set = new ItemSet();
				}
			} else if (files.length == 0) {
				Bukkit.getLogger().log(Level.WARNING,
						"No custom item set could be found in the Custom Items plugin data folder. It should contain a single file that ends with .cis");
				set = new ItemSet();
			} else {
				File file = files[0];
				Bukkit.getLogger()
						.warning("Multiple custom item sets were found, so the item set " + file + " will be loaded.");
				try {
					BitInput input = new BitInputStream(new FileInputStream(file));
					set = new ItemSet(input);
					input.terminate();
				} catch (Exception ex) {
					Bukkit.getLogger().log(Level.SEVERE, "Failed to load the custom item set " + file, ex);
					set = new ItemSet();
				}
			}
		} else {
			Bukkit.getLogger().warning("Something is wrong with the Custom Items Plug-in data folder");
			set = new ItemSet();
		}
	}

	public ItemSet getSet() {
		return set;
	}

	public LanguageFile getLanguageFile() {
		return languageFile;
	}
}