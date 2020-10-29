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
		if (wasSolid && blockBreakDurabilityLoss != 0) {
			
			ItemStack decreased = decreaseDurability(tool, blockBreakDurabilityLoss);
			if (decreased == null) {
				CustomItemsEventHandler.playBreakSound(player);
			}
			if (decreased != tool) {
				player.getInventory().setItemInMainHand(decreased);
			}
		}
	}
	
	@Override
	public void onEntityHit(LivingEntity attacker, ItemStack tool, Entity target) {
		super.onEntityHit(attacker, tool, target);
		if (entityHitDurabilityLoss != 0) {
			ItemStack decreased = decreaseDurability(tool, entityHitDurabilityLoss);
			if (decreased == null && attacker instanceof Player) {
				CustomItemsEventHandler.playBreakSound((Player) attacker);
			}
			if (decreased != tool) {
				attacker.getEquipment().setItemInMainHand(decreased);
			}
		}
	}
	
	public boolean forbidDefaultUse(ItemStack item) {
    	return false;
    }
	
	/**
	 * @param stack The (custom) item stack to decrease the durability of
	 * @return The same item stack if nothing changed, or a new ItemStack that should
	 * replace the old one (null if the stack should break). 
	 * 
	 * It is the task of the caller to ensure that the old one really gets replaced!
	 */
	public ItemStack decreaseDurability(ItemStack stack, int damage) {
		if (isUnbreakable() || !stack.hasItemMeta()) {
			return stack;
		}
		if (Math.random() <= 1.0 / (1 + stack.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.DURABILITY))) {
			
			// Don't to anything if this tool is unbreakable
			if (maxDurability != UNBREAKABLE_TOOL_DURABILITY) {
				
				ItemStack[] pResult = {stack};
				Long[] pNewDurability = {null};
				
				CustomItemNBT.readWrite(stack, nbt -> {
					Long durability = nbt.getDurability();
					if (durability != null) {
						if (durability > damage) {
							durability -= damage;
							nbt.setDurability(durability);
							pNewDurability[0] = durability;
						} else {
							
							// If this block is reached, the item will break
							pNewDurability[0] = 0L;
						}
					} else {
						/*
						 * If this happens, the item stack doesn't have durability
						 * stored in its lore, even though it should be breakable.
						 * This probably means that the custom item used to be
						 * unbreakable in the previous version of the item set, but
						 * became breakable in the current version of the item set.
						 * We have a repeating task to frequently check for these
						 * problems, so we will just do nothing and wait for the
						 * repeating task to fix this.
						 */
					}
				}, newStack -> pResult[0] = newStack);
				stack = pResult[0];
				
				if (pNewDurability[0] != null) {
					long newDurability = pNewDurability[0];
					if (newDurability == 0) {
						return null;
					}
					ItemMeta meta = stack.getItemMeta();
					meta.setLore(createLore(newDurability));
					stack.setItemMeta(meta);
				}
				
				return stack;
			} else {
				
				// The item is unbreakable, so just return the same item right away
				return stack;
			}
		} else {
			
			// The item has durability enchantment, and shouldn't receive damage now
			return stack;
		}
	}
	
	public static class IncreaseDurabilityResult {
		
		public final ItemStack stack;
		public final long increasedAmount;
		
		IncreaseDurabilityResult(ItemStack stack, long increasedAmount) {
			this.stack = stack;
			this.increasedAmount = increasedAmount;
		}
	}
	
	public IncreaseDurabilityResult increaseDurability(ItemStack stack, int amount) {
		if (isUnbreakable() || !stack.hasItemMeta()) {
			return new IncreaseDurabilityResult(stack, 0);
		}
		
		ItemStack[] pStack = {stack};
		long[] pIncreasedAmount = {0L};
		long[] pNewDurability = {-1L};
		
		CustomItemNBT.readWrite(stack, nbt -> {
			Long oldDurability = nbt.getDurability();
			if (oldDurability != null) {
				long newDurability;
				if (oldDurability + amount <= maxDurability) {
					newDurability = oldDurability + amount;
				} else {
					newDurability = maxDurability;
				}
				pIncreasedAmount[0] = newDurability - oldDurability;
				pNewDurability[0] = newDurability;
				nbt.setDurability(newDurability);
			} else {
				/*
				 * If this happens, the item stack doesn't have durability
				 * stored in its lore, even though it should be breakable.
				 * This probably means that the custom item used to be
				 * unbreakable in the previous version of the item set, but
				 * became breakable in the current version of the item set.
				 * We have a repeating task to frequently check for these
				 * problems, so we will just do nothing and wait for the
				 * repeating task to fix this.
				 */
			}
		}, newStack -> pStack[0] = newStack);
		stack = pStack[0];
		long increasedAmount = pIncreasedAmount[0];
		
		if (increasedAmount > 0) {
			long newDurability = pNewDurability[0];
			ItemMeta meta = stack.getItemMeta();
			meta.setLore(createLore(newDurability));
			stack.setItemMeta(meta);
		}
		
		return new IncreaseDurabilityResult(stack, increasedAmount);
	}
	
	protected boolean isUnbreakable() {
		return maxDurability == UNBREAKABLE_TOOL_DURABILITY;
	}
	
	public long getDurability(ItemStack stack) {
		long[] pResult = {0};
		CustomItemNBT.readOnly(stack, nbt -> {
			Long durability = nbt.getDurability();
			if (durability != null) {
				pResult[0] = durability;
			} else {
				pResult[0] = UNBREAKABLE_TOOL_DURABILITY;
			}
		});
		return pResult[0];
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