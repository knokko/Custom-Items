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
package nl.knokko.customitems.plugin.set.item;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.plugin.CustomItemsEventHandler;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;

public class CustomTool extends CustomItem {
	
	private static final String DURABILITY_SPLIT = " / ";
	
	private static String prefix() {
		return CustomItemsPlugin.getInstance().getLanguageFile().getDurabilityPrefix();
	}
	
	protected static String createDurabilityLine(long current, long max) {
		return prefix() + " " + current + DURABILITY_SPLIT + max;
	}
	
	private static long parseDurability(String line) {
		int indexBound = line.lastIndexOf(DURABILITY_SPLIT);
		int indexStart = line.lastIndexOf(' ', indexBound - 1) + 1;
		return Long.parseLong(line.substring(indexStart, indexBound));
	}
	
	protected final long maxDurability;
	
	protected final boolean allowEnchanting;
	protected final boolean allowAnvil;
	
	protected final Ingredient repairItem;
	
	protected final int entityHitDurabilityLoss;
	protected final int blockBreakDurabilityLoss;

	public CustomTool(CustomItemType itemType, short itemDamage, String name, String displayName, String[] lore, 
			AttributeModifier[] attributes, Enchantment[] defaultEnchantments, long maxDurability, boolean allowEnchanting, boolean allowAnvil, 
			Ingredient repairItem, boolean[] itemFlags, int entityHitDurabilityLoss, int blockBreakDurabilityLoss, List<PotionEffect> playerEffects, 
			List<PotionEffect> targetEffects, String[] commands) {
		super(itemType, itemDamage, name, displayName, lore, attributes, defaultEnchantments, itemFlags, playerEffects, targetEffects, commands);
		this.maxDurability = maxDurability;
		this.allowEnchanting = allowEnchanting;
		this.allowAnvil = allowAnvil;
		this.repairItem = repairItem;
		this.entityHitDurabilityLoss = entityHitDurabilityLoss;
		this.blockBreakDurabilityLoss = blockBreakDurabilityLoss;
	}
	
	@Override
	protected String getLoreContent12() {
		String lore = super.getLoreContent12();
		String preLore = "\"" + prefix() + " " + maxDurability + " / " + maxDurability + "\",\"\"";
		if (lore.isEmpty()) {
			return preLore;
		} else {
			return preLore + "," + lore;
		}
	}
	
	@Override
	protected String getLoreContent14() {
		String lore = super.getLoreContent14();
		String preLore = "\"\\\"" + prefix() + " " + maxDurability + " / " + maxDurability + "\\\"\",\"\\\"\\\"\"";
		if (lore.isEmpty()) {
			return preLore;
		} else {
			return preLore + "," + lore;
		}
	}
	
	@Override
	public int getMaxStacksize() {
		return 1;
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
	protected List<String> createLore(){
		return createLore(maxDurability);
	}
	
	protected List<String> createLore(long currentDurability){
		List<String> itemLore = new ArrayList<String>(lore.length + 2);
        if (!isUnbreakable()) {
        	itemLore.add(createDurabilityLine(currentDurability, maxDurability));
        	itemLore.add("");
        }
        for (String s : lore)
    		itemLore.add(s);
        
        // Interesting item:
        return itemLore;
	}
	
	public ItemStack create(int amount, long durability) {
		if (amount != 1) throw new IllegalArgumentException("Amount must be 1, but is " + amount);
		return super.create(amount, createLore(durability));
	}
	
	@Override
	public void onBlockBreak(Player player, ItemStack tool, Block block, boolean wasSolid) {
		if (wasSolid && blockBreakDurabilityLoss != 0 && decreaseDurability(tool, blockBreakDurabilityLoss)) {
			CustomItemsEventHandler.playBreakSound(player);
			player.getInventory().setItemInMainHand(null);
		}
	}
	
	@Override
	public void onEntityHit(LivingEntity attacker, ItemStack tool, Entity target) {
		super.onEntityHit(attacker, tool, target);
		if (entityHitDurabilityLoss != 0 && decreaseDurability(tool, entityHitDurabilityLoss)) {
			if (attacker instanceof Player)
				CustomItemsEventHandler.playBreakSound((Player) attacker);
			attacker.getEquipment().setItemInMainHand(null);
		}
	}
	
	public boolean forbidDefaultUse(ItemStack item) {
    	return false;
    }
	
	/**
	 * @param stack The (custom) item stack to decrease the durability of
	 * @return True if the stack breaks, false if it only loses durability
	 */
	public boolean decreaseDurability(ItemStack stack, int damage) {
		if (isUnbreakable() || !stack.hasItemMeta()) {
			return false;
		}
		if (Math.random() <= 1.0 / (1 + stack.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.DURABILITY))) {
			ItemMeta meta = stack.getItemMeta();
			List<String> lore = meta.getLore();
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
				
				// The durability/lore must have been corrupted, so we can't determine the durability
				return false;
			}
		} else {
			return false;
		}
	}
	
	public long increaseDurability(ItemStack stack, int amount) {
		if (isUnbreakable() || !stack.hasItemMeta()) {
			return 0;
		}
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
	
	protected boolean isUnbreakable() {
		return maxDurability == nl.knokko.customitems.item.CustomItem.UNBREAKABLE_TOOL_DURABILITY;
	}
	
	public long getDurability(ItemStack stack) {
		if (isUnbreakable()) {
			return CustomItem.UNBREAKABLE_TOOL_DURABILITY;
		}
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
		if (lore == null || lore.isEmpty()) {
			return null;
		}
		
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
			
			// It looks like the lore of this custom item has been manipulated somehow
			return null;
		} else if (potentialMatches == 1) {
			
			// Then the language config changed and this should be the old durability
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
	
	@Override
	protected void initNBT(CustomItemNBT nbt) {
		if (maxDurability != UNBREAKABLE_TOOL_DURABILITY) {
			nbt.setDurability(maxDurability);
		}
	}
}