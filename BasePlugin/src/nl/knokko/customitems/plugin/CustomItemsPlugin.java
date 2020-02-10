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

import java.io.DataInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import nl.knokko.customitems.plugin.command.CommandCustomItems;
import nl.knokko.customitems.plugin.data.PluginData;
import nl.knokko.customitems.plugin.multisupport.crazyenchantments.CrazyEnchantmentsSupport;
import nl.knokko.customitems.plugin.projectile.ProjectileManager;
import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.ByteArrayBitInput;

public class CustomItemsPlugin extends JavaPlugin {

	private static CustomItemsPlugin instance;

	private ItemSet set;
	private LanguageFile languageFile;
	private PluginData data;
	private ProjectileManager projectileManager;
	
	private int maxFlyingProjectiles;
	private boolean showInterestingWarnings;

	public static CustomItemsPlugin getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		instance = this;
		languageFile = new LanguageFile(new File(getDataFolder() + "/lang.yml"));
		loadConfig();

		// Load the set after creating language file instance because the set needs the
		// durability prefix
		loadSet();
		data = PluginData.loadData();
		projectileManager = new ProjectileManager();
		getCommand("customitems").setExecutor(new CommandCustomItems(languageFile));
		Bukkit.getPluginManager().registerEvents(new CustomItemsEventHandler(), this);
		Bukkit.getPluginManager().registerEvents(projectileManager, this);
		CrazyEnchantmentsSupport.onEnable();
	}

	@Override
	public void onDisable() {
		data.saveData();
		projectileManager.destroyCustomProjectiles();
		instance = null;
		super.onDisable();
	}
	
	public int getMaxFlyingProjectiles() {
		return maxFlyingProjectiles;
	}
	
	public boolean showInterestingWarnings() {
		return showInterestingWarnings();
	}
	
	private static final String KEY_MAX_PROJECTILES = "Maximum number of flying projectiles";
	private static final String KEY_INTERESTING_WARNINGS = "Show warnings about interesting items";
	
	private void loadConfig() {
		FileConfiguration config = getConfig();
		if (config.contains(KEY_MAX_PROJECTILES)) {
			this.maxFlyingProjectiles = config.getInt(KEY_MAX_PROJECTILES);
		} else {
			this.maxFlyingProjectiles = 100;
			config.set(KEY_MAX_PROJECTILES, maxFlyingProjectiles);
			saveConfig();
		}
		
		if (config.contains(KEY_INTERESTING_WARNINGS)) {
			this.showInterestingWarnings = config.getBoolean(KEY_INTERESTING_WARNINGS);
		} else {
			this.showInterestingWarnings = true;
			config.set(KEY_INTERESTING_WARNINGS, this.showInterestingWarnings);
			saveConfig();
		}
	}
	
	private void loadSet(File file) {
		try {
			if (file.length() < 1_000_000_000) {
				byte[] bytes = new byte[(int) file.length()];
				DataInputStream fileInput = new DataInputStream(Files.newInputStream(file.toPath()));
				fileInput.readFully(bytes);
				fileInput.close();
				if (file.getName().endsWith(".cis")) {
					BitInput input = new ByteArrayBitInput(bytes);
					set = new ItemSet(input);
					input.terminate();
				} else {
					int counter = 0;
					for (byte b : bytes) {
						if (b >= 'a' && b < ('a' + 16)) {
							counter++;
						}
					}
					
					int byteSize = counter / 2;
					if (byteSize * 2 != counter) {
						Bukkit.getLogger().log(Level.SEVERE, "The item set " + file + " had an odd number of alphabetic characters, which is not allowed!");
						set = new ItemSet();
						return;
					}
					byte[] dataBytes = new byte[byteSize];
					int textIndex = 0;
					for (int dataIndex = 0; dataIndex < byteSize; dataIndex++) {
						int firstPart = bytes[textIndex++];
						while (firstPart < 'a' || firstPart >= 'a' + 16) {
							firstPart = bytes[textIndex++];
						}
						firstPart -= 'a';
						int secondPart = bytes[textIndex++];
						while (secondPart < 'a' || secondPart >= 'a' + 16) {
							secondPart = bytes[textIndex++];
						}
						secondPart -= 'a';
						dataBytes[dataIndex] = (byte) (firstPart + 16 * secondPart);
					}
					BitInput input = new ByteArrayBitInput(dataBytes);
					set = new ItemSet(input);
					input.terminate();
				}
			} else {
				Bukkit.getLogger().log(Level.SEVERE, "The custom item set " + file + " is too big");
				set = new ItemSet();
			}
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to load the custom item set " + file, ex);
			set = new ItemSet();
		}
	}

	private void loadSet() {
		File folder = getDataFolder();
		folder.mkdirs();
		File[] files = folder.listFiles((File file, String name) -> {
			return name.endsWith(".cis") || name.endsWith(".txt");
		});
		if (files != null) {
			if (files.length == 1) {
				File file = files[0];
				loadSet(file);
			} else if (files.length == 0) {
				Bukkit.getLogger().log(Level.WARNING,
						"No custom item set could be found in the Custom Items plugin data folder. It should contain a file that ends with .cis or .txt");
				set = new ItemSet();
			} else {
				File file = files[0];
				Bukkit.getLogger()
						.warning("Multiple custom item sets were found, so the item set " + file + " will be loaded.");
				loadSet(file);
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
	
	public PluginData getData() {
		return data;
	}
	
	public ProjectileManager getProjectileManager() {
		return projectileManager;
	}
}