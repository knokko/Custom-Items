/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2018 knokko
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
package nl.knokko.customitems.plugin.set.item;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;

import nl.knokko.core.plugin.item.attributes.ItemAttributes;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.CustomItemType.Category;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;

public class CustomTool extends CustomItem {
	
	private static final String DURABILITY_SPLIT = " / ";
	
	private static String createDurabilityLine(long current, long max) {
		return CustomItemsPlugin.getInstance().getLanguageFile().getDurabilityPrefix() + " " + current + DURABILITY_SPLIT + max;
	}
	
	private static long parseDurability(String line) {
		int indexBound = line.lastIndexOf(DURABILITY_SPLIT);
		int indexStart = line.lastIndexOf(' ', indexBound - 1) + 1;
		return Long.parseLong(line.substring(indexStart, indexBound));
	}
	
	private final long maxDurability;
	
	private final boolean allowEnchanting;
	private final boolean allowAnvil;
	
	private final Ingredient repairItem;
	
	private final boolean isSword;

	public CustomTool(CustomItemType itemType, short itemDamage, String name, String displayName, String[] lore, 
			AttributeModifier[] attributes, long maxDurability, boolean allowEnchanting, boolean allowAnvil, 
			Ingredient repairItem) {
		super(itemType, itemDamage, name, displayName, lore, attributes);
		this.maxDurability = maxDurability;
		this.allowEnchanting = allowEnchanting;
		this.allowAnvil = allowAnvil;
		this.repairItem = repairItem;
		isSword = itemType.getMainCategory() == Category.SWORD;
	}
	
	@Override
	public boolean canStack() {
		return false;
	}
	
	@Override
	public boolean allowVanillaEnchanting() {
		return allowEnchanting;
	}
	
	@Override
	public boolean allowAnvilActions() {
		return allowAnvil;
	}
	
	public Ingredient getRepairItem() {
		return repairItem;
	}
	
	@Override
	public ItemStack create(int amount) {
		return create(amount, maxDurability);
	}
	
	public ItemStack create(int amount, long durability) {
		if (amount != 1) throw new IllegalArgumentException("Amount must be 1, but is " + amount);
		ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);
        meta.setDisplayName(displayName);
        List<String> itemLore = new ArrayList<String>(lore.length + 2);
        if (maxDurability == nl.knokko.customitems.item.CustomItem.UNBREAKABLE_TOOL_DURABILITY) {
        	itemLore.add(CustomItemsPlugin.getInstance().getLanguageFile().getUnbreakable());
        } else {
        	itemLore.add(createDurabilityLine(maxDurability, maxDurability));
        }
        itemLore.add("");
        for (String s : lore)
    		itemLore.add(s);
    	meta.setLore(itemLore);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        item.setDurability(itemDamage);
        return ItemAttributes.setAttributes(ItemAttributes.clearAttributes(item), attributeModifiers);
	}
	
	@Override
	public void onBlockBreak(Player player, ItemStack tool, Block block) {
		if (itemType.getMainCategory() != Category.HOE && itemType.getMainCategory() != Category.BOW && decreaseDurability(tool, isSword ? 2 : 1))
			player.getInventory().setItemInMainHand(null);
	}
	
	@Override
	public void onEntityHit(LivingEntity attacker, ItemStack tool, Entity target) {
		if (itemType.getMainCategory() != Category.HOE && itemType.getMainCategory() != Category.BOW && decreaseDurability(tool, isSword ? 1 : 2))
			attacker.getEquipment().setItemInMainHand(null);
	}
	
	public boolean forbidDefaultUse(ItemStack item) {
    	return false;
    }
	
	/**
	 * @param stack The (custom) item stack to decrease the durability of
	 * @return True if the stack breaks, false if it only loses durability
	 */
	public boolean decreaseDurability(ItemStack stack, int damage) {
		if (Math.random() <= 1.0 / (1 + stack.getEnchantmentLevel(Enchantment.DURABILITY))) {
			ItemMeta meta = stack.getItemMeta();
			List<String> lore = meta.getLore();
			String oldFirstLine = lore.get(0);
			String durabilityString = getDurabilityString(lore);
			// Check whether or not the tool is unbreakable
			if (durabilityString != null) {
				long durability = parseDurability(durabilityString);
				if (durability > damage) {
					durability -= damage;
					lore.set(lore.indexOf(durabilityString), createDurabilityLine(durability, maxDurability));
					meta.setLore(lore);
					stack.setItemMeta(meta);
					return false;
				} else {
					return true;
				}
			} else {
				// Dirty hack to replace old unbreakable string by new unbreakable string under circumstances
				if (!oldFirstLine.equals(lore.get(0))) {
					meta.setLore(lore);
					stack.setItemMeta(meta);
				}
				return false;
			}
		} else {
			return false;
		}
	}
	
	public long increaseDurability(ItemStack stack, int amount) {
		ItemMeta meta = stack.getItemMeta();
		List<String> lore = meta.getLore();
		String durabilityString = getDurabilityString(lore);
		// Check whether or not the tool is unbreakable
		if (durabilityString != null) {
			long durability = parseDurability(durabilityString);
			long old = durability;
			durability += amount;
			if (durability > maxDurability)
				durability = maxDurability;
			lore.set(lore.indexOf(durabilityString), createDurabilityLine(durability, maxDurability));
			meta.setLore(lore);
			stack.setItemMeta(meta);
			return durability - old;
		} else {
			return 0;
		}
	}
	
	public long getDurability(ItemStack stack) {
		ItemMeta meta = stack.getItemMeta();
		List<String> lore = meta.getLore();
		String durabilityString = getDurabilityString(lore);
		// Check whether or not the tool is unbreakable
		if (durabilityString != null) {
			return parseDurability(durabilityString);
		} else {
			return CustomItem.UNBREAKABLE_TOOL_DURABILITY;
		}
	}
	
	private String getDurabilityString(List<String> lore) {
		String durabilityPrefix = CustomItemsPlugin.getInstance().getLanguageFile().getDurabilityPrefix();
		
		// If the config has not been changed and the item is not unbreakable, it should be found here
		for (String line : lore) {
			if (line.startsWith(durabilityPrefix)) {
				int indexSplit = line.lastIndexOf(DURABILITY_SPLIT);
				if (indexSplit != -1) {
					try {
						long maxDurability = Long.parseLong(line.substring(indexSplit + DURABILITY_SPLIT.length()));
						if (maxDurability > 0) {
							long durability = Long.parseLong(line.substring(line.lastIndexOf(' ', indexSplit - 1) + 1, indexSplit));
							if (durability > 0) {
								return line;
							}
						}
					} catch (NumberFormatException ex) {
						// This means that the item has lore that looks like the durability format
					}
				}
			}
		}
		
		// Maybe, the item is unbreakable
		String unbreakableString = CustomItemsPlugin.getInstance().getLanguageFile().getUnbreakable();
		for (String line : lore) {
			if (line.equals(unbreakableString)) {
				return null;
			}
		}
		
		// It looks like the config has been changed, this might get a bit dirty...
		int potentialMatches = 0;
		for (String line : lore) {
			int indexSplit = line.lastIndexOf(DURABILITY_SPLIT);
			if (indexSplit != -1) {
				try {
					long maxDurability = Long.parseLong(line.substring(indexSplit + DURABILITY_SPLIT.length()));
					if (maxDurability > 0) {
						long durability = Long.parseLong(line.substring(line.lastIndexOf(' ', indexSplit - 1) + 1, indexSplit));
						if (durability > 0) {
							potentialMatches++;
						}
					}
				} catch (NumberFormatException ex) {
					// This means that the item has lore that looks like the durability format
				}
			}
		}
		
		if (potentialMatches == 0) {
			
			// The unbreakable string must have changed, the first line should be the previous one, but it
			// is not entirely safe unfortunately...
			lore.set(0, unbreakableString);
			return null;
		} else if (potentialMatches == 1) {
			
			// Let's hope the item was not unbreakable...
			for (String line : lore) {
				int indexSplit = line.lastIndexOf(DURABILITY_SPLIT);
				if (indexSplit != -1) {
					try {
						long maxDurability = Long.parseLong(line.substring(indexSplit + DURABILITY_SPLIT.length()));
						if (maxDurability > 0) {
							long durability = Long.parseLong(line.substring(line.lastIndexOf(' ', indexSplit - 1) + 1, indexSplit));
							if (durability > 0) {
								return line;
							}
						}
					} catch (NumberFormatException ex) {
						// This means that the item has lore that looks like the durability format
					}
				}
			}
		} else {
			
			// There are at least 2 lines that could be it, so there is no way to safely choose the right one
			return null;
		}
		throw new Error("This line should be unreachable!");
	}
	
	public long getMaxDurability() {
		return maxDurability;
	}
}