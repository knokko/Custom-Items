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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import nl.knokko.core.plugin.CorePlugin;
import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.drops.Drop;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.plugin.recipe.CustomRecipe;
import nl.knokko.customitems.plugin.recipe.ShapedCustomRecipe;
import nl.knokko.customitems.plugin.recipe.ShapelessCustomRecipe;
import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.customitems.plugin.set.item.CustomArmor;
import nl.knokko.customitems.plugin.set.item.CustomBow;
import nl.knokko.customitems.plugin.set.item.CustomHoe;
import nl.knokko.customitems.plugin.set.item.CustomItem;
import nl.knokko.customitems.plugin.set.item.CustomShears;
import nl.knokko.customitems.plugin.set.item.CustomShield;
import nl.knokko.customitems.plugin.set.item.CustomTool;

import static org.bukkit.enchantments.Enchantment.*;

@SuppressWarnings("deprecation")
public class CustomItemsEventHandler implements Listener {

	private Map<UUID, Boolean> shouldInterfere = new HashMap<UUID, Boolean>();

	@EventHandler(ignoreCancelled = true)
	public void onItemUse(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack item = event.getItem();
			CustomItem custom = CustomItemsPlugin.getInstance().getSet().getItem(item);
			if (custom != null) {
				// Don't let custom items be used as their internal item
				if (custom.forbidDefaultUse(item))
					event.setCancelled(true);
				else if (custom instanceof CustomTool) {
					CustomTool tool = (CustomTool) custom;
					if (tool instanceof CustomHoe) {
						CIMaterial type = CIMaterial.valueOf(ItemHelper.getMaterialName(event.getClickedBlock()));
						if ((type == CIMaterial.DIRT || type == CIMaterial.GRASS) && tool.decreaseDurability(item, ((CustomHoe) tool).getTillDurabilityLoss())) {
							playBreakSound(event.getPlayer());
							if (event.getHand() == EquipmentSlot.HAND)
								event.getPlayer().getInventory().setItemInMainHand(null);
							else
								event.getPlayer().getInventory().setItemInOffHand(null);
						}
					}
				}
			}
		}
	}

	public static void playBreakSound(Player player) {
		player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
	}

	@EventHandler(ignoreCancelled = true)
	public void processCustomBowDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Arrow) {
			List<MetadataValue> metas = event.getDamager().getMetadata("CustomBowDamageMultiplier");
			for (MetadataValue meta : metas) {
				event.setDamage(event.getDamage() * meta.asDouble());
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBowShoot(EntityShootBowEvent event) {
		if (CustomItem.isCustom(event.getBow())) {
			CustomItem customItem = CustomItemsPlugin.getInstance().getSet().getItem(event.getBow());
			if (customItem instanceof CustomBow) {
				CustomBow bow = (CustomBow) customItem;
				Entity projectile = event.getProjectile();
				if (projectile instanceof Arrow) {
					if (event.getEntity() instanceof Player
							&& bow.decreaseDurability(event.getBow(), bow.getShootDurabilityLoss())) {
						Player player = (Player) event.getEntity();
						playBreakSound(player);
						ItemStack mainHand = player.getInventory().getItemInMainHand();
						if (mainHand != null && CustomItemsPlugin.getInstance().getSet().getItem(mainHand) == bow) {
							player.getInventory().setItemInMainHand(null);
						} else {
							player.getInventory().setItemInOffHand(null);
						}
					}
					Arrow arrow = (Arrow) projectile;
					arrow.setKnockbackStrength(arrow.getKnockbackStrength() + bow.getKnockbackStrength());
					arrow.setVelocity(arrow.getVelocity().multiply(bow.getSpeedMultiplier()));
					arrow.setGravity(bow.hasGravity());
					arrow.setMetadata("CustomBowDamageMultiplier", new MetadataValue() {

						@Override
						public Object value() {
							return null;
						}

						@Override
						public int asInt() {
							return 0;
						}

						@Override
						public float asFloat() {
							return 0;
						}

						@Override
						public double asDouble() {
							return bow.getDamageMultiplier();
						}

						@Override
						public long asLong() {
							return 0;
						}

						@Override
						public short asShort() {
							return 0;
						}

						@Override
						public byte asByte() {
							return 0;
						}

						@Override
						public boolean asBoolean() {
							return false;
						}

						@Override
						public String asString() {
							return null;
						}

						@Override
						public Plugin getOwningPlugin() {
							return CustomItemsPlugin.getInstance();
						}

						@Override
						public void invalidate() {
						}
					});
				} else {
					Bukkit.getLogger().warning("A bow was shot, but it didn't shoot an arrow");
				}
			} else {
				Bukkit.getLogger().warning("A bow is being used as custom item, but is not a custom bow");
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onShear(PlayerShearEntityEvent event) {
		ItemStack main = event.getPlayer().getInventory().getItemInMainHand();
		ItemStack off = event.getPlayer().getInventory().getItemInOffHand();
		CustomItem customMain = CIMaterial.valueOf(ItemHelper.getMaterialName(main)) == CIMaterial.SHEARS
				? CustomItemsPlugin.getInstance().getSet().getItem(main)
				: null;
		CustomItem customOff = CIMaterial.valueOf(ItemHelper.getMaterialName(off)) == CIMaterial.SHEARS
				? CustomItemsPlugin.getInstance().getSet().getItem(off)
				: null;
		if (customMain != null) {
			if (customMain.forbidDefaultUse(main))
				event.setCancelled(true);
			else if (customMain instanceof CustomShears) {
				CustomShears tool = (CustomShears) customMain;
				if (tool.decreaseDurability(main, tool.getShearDurabilityLoss())) {
					playBreakSound(event.getPlayer());
					event.getPlayer().getInventory().setItemInMainHand(null);
				}
			} else
				Bukkit.getLogger().warning("Interesting custom main shear: " + customMain);
		} else if (customOff != null) {
			if (customOff.forbidDefaultUse(off))
				event.setCancelled(true);
			else if (customOff instanceof CustomShears) {
				CustomShears tool = (CustomShears) customOff;
				if (tool.decreaseDurability(off, tool.getShearDurabilityLoss())) {
					playBreakSound(event.getPlayer());
					event.getPlayer().getInventory().setItemInOffHand(null);
				}
			} else
				Bukkit.getLogger().warning("Interesting custom off shear: " + customOff);
		}
	}

	// Use the highest priority because we want to ignore the event in case it is cancelled
	// and we may need to modify the setDropItems flag of the event
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		ItemSet set = CustomItemsPlugin.getInstance().getSet();
		Drop[] customDrops = set.getDrops(CIMaterial.valueOf(ItemHelper.getMaterialName(event.getBlock())));
		
		Random random = new Random();
		boolean cancelDefaultDrops = false;
		for (Drop drop : customDrops) {
			if (drop.chooseToDrop(random)) {
				if (drop.cancelNormalDrop()) {
					cancelDefaultDrops = true;
				}
				int amountToDrop = drop.chooseDroppedAmount(random);
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), ((CustomItem) drop.getItemToDrop()).create(amountToDrop));
			}
		}
		if (cancelDefaultDrops) {
			event.setDropItems(false);
		}
		
		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		if (CustomItem.isCustom(item)) {
			CustomItem custom = set.getItem(item);
			if (custom != null) {
				custom.onBlockBreak(event.getPlayer(), item, event.getBlock());
			} else {
				Bukkit.getLogger().warning("Interesting item: " + item);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event) {
		Drop[] drops = CustomItemsPlugin.getInstance().getSet().getDrops(event.getEntity());
		Random random = new Random();
		
		for (Drop drop : drops) {
			if (drop.chooseToDrop(random)) {
				if (drop.cancelNormalDrop()) {
					event.getDrops().clear();
				}
				event.getDrops().add(((CustomItem) drop.getItemToDrop()).create(drop.chooseDroppedAmount(random)));
			}
		}
	}

	@EventHandler
	public void onEntityHit(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof LivingEntity) {
			ItemStack weapon = ((LivingEntity) event.getDamager()).getEquipment().getItemInMainHand();
			if (CustomItem.isCustom(weapon)) {
				CustomItem custom = CustomItemsPlugin.getInstance().getSet().getItem(weapon);
				if (custom != null) {
					custom.onEntityHit((LivingEntity) event.getDamager(), weapon, event.getEntity());
				} else {
					Bukkit.getLogger().warning("Interesting item: " + weapon);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			double original = event.getDamage();

			// Only act if armor reduced the damage
			if (isReducedByArmor(event.getCause())) {
				
				int armorDamage = Math.max(1, (int) (original / 4));
				EntityEquipment e = player.getEquipment();
				if (decreaseCustomArmorDurability(e.getHelmet(), armorDamage)) {
					playBreakSound(player);
					e.setHelmet(null);
				}
				if (decreaseCustomArmorDurability(e.getChestplate(), armorDamage)) {
					playBreakSound(player);
					e.setChestplate(null);
				}
				if (decreaseCustomArmorDurability(e.getLeggings(), armorDamage)) {
					playBreakSound(player);
					e.setLeggings(null);
				}
				if (decreaseCustomArmorDurability(e.getBoots(), armorDamage)) {
					playBreakSound(player);
					e.setBoots(null);
				}
			}
			
			// There is no nice shield blocking event, so this dirty check will have to do
			if (player.isBlocking() && event.getFinalDamage() == 0.0) {
				
				CustomShield shield = null;
				boolean offhand = true;
				
				if (CustomItem.isCustom(player.getInventory().getItemInOffHand())) {
					ItemSet set = CustomItemsPlugin.getInstance().getSet();
					CustomItem customMain = set.getItem(player.getInventory().getItemInOffHand());
					if (customMain instanceof CustomShield) {
						shield = (CustomShield) customMain;
					}
				}
				
				if (CustomItem.isCustom(player.getInventory().getItemInMainHand())) {
					ItemSet set = CustomItemsPlugin.getInstance().getSet();
					CustomItem customMain = set.getItem(player.getInventory().getItemInMainHand());
					if (customMain instanceof CustomShield) {
						shield = (CustomShield) customMain;
						offhand = false;
					}
				}
				
				if (shield != null && event.getDamage() >= shield.getDurabilityThreshold()) {
					int lostDurability = (int) (event.getDamage()) + 1;
					if (offhand) {
						if (shield.decreaseDurability(player.getInventory().getItemInOffHand(), lostDurability)) {
							player.getInventory().setItemInOffHand(null);
							playBreakSound(player);
						}
					} else {
						if (shield.decreaseDurability(player.getInventory().getItemInMainHand(), lostDurability)) {
							player.getInventory().setItemInOffHand(null);
							playBreakSound(player);
						}
					}
				}
			}
		}
	}
	
	private boolean decreaseCustomArmorDurability(ItemStack piece, int damage) {
		if (CustomItem.isCustom(piece)) {
			CustomItem custom = CustomItemsPlugin.getInstance().getSet().getItem(piece);
			if (custom instanceof CustomArmor) {
				return ((CustomArmor) custom).decreaseDurability(piece, damage);
			} else {
				Bukkit.getLogger().warning("Strange item " + piece);
				return false;
			}
		} else {
			return false;
		}
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void beforeEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			try {
				DamageSource damageSource = DamageSource.valueOf(event.getCause().name());
				
				Player player = (Player) event.getEntity();

				EntityEquipment e = player.getEquipment();
				short[] damageResistances = new short[4];
				
				applyCustomArmorDamageReduction(e.getHelmet(), damageSource, damageResistances, 0);
				applyCustomArmorDamageReduction(e.getChestplate(), damageSource, damageResistances, 1);
				applyCustomArmorDamageReduction(e.getLeggings(), damageSource, damageResistances, 2);
				applyCustomArmorDamageReduction(e.getBoots(), damageSource, damageResistances, 3);
				
				int totalDamageResistance = 0;
				for (short damageResistance : damageResistances) {
					totalDamageResistance += damageResistance;
				}
				
				if (totalDamageResistance < 100) {
					event.setDamage(event.getDamage() * (100 - totalDamageResistance) * 0.01);
				} else {
					if (totalDamageResistance > 100) {
						double healing = event.getDamage() * (totalDamageResistance - 100) * 0.01;
						double newHealth = player.getHealth() + healing;
						player.setHealth(Math.min(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), newHealth));
					}
					
					event.setCancelled(true);
					event.setDamage(0);
				}
			} catch (IllegalArgumentException ex) {
				// This will happen when the damage cause is not known to this plug-in.
				// This plug-in only knows the damage causes of craftbukkit 1.12, which means that
				// this catch block will be reached when a new damage cause is used in a later version
				// of minecraft.
				Bukkit.getLogger().warning("Unknown damage cause: " + event.getCause());
			}
		}
	}

	private boolean isReducedByArmor(DamageCause c) {
		return c == DamageCause.BLOCK_EXPLOSION || c == DamageCause.CONTACT || c == DamageCause.ENTITY_ATTACK
				|| c == DamageCause.ENTITY_EXPLOSION || c == DamageCause.ENTITY_SWEEP_ATTACK
				|| c == DamageCause.FALLING_BLOCK || c == DamageCause.FLY_INTO_WALL || c == DamageCause.HOT_FLOOR
				|| c == DamageCause.LAVA || c == DamageCause.PROJECTILE;
	}

	private void applyCustomArmorDamageReduction(ItemStack armorPiece, DamageSource source, short[] damageResistances, int resistanceIndex) {
		if (CustomItem.isCustom(armorPiece)) {
			CustomItem custom = CustomItemsPlugin.getInstance().getSet().getItem(armorPiece);
			if (custom instanceof CustomArmor) {
				CustomArmor armor = (CustomArmor) custom;
				if (source != null) {
					damageResistances[resistanceIndex] = armor.getDamageResistances().getResistance(source);
				}
			} else {
				Bukkit.getLogger().warning("Strange armor piece " + armorPiece);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onItemInteract(PlayerInteractAtEntityEvent event) {
		ItemStack item;
		if (event.getHand() == EquipmentSlot.HAND)
			item = event.getPlayer().getInventory().getItemInMainHand();
		else
			item = event.getPlayer().getInventory().getItemInOffHand();
		CustomItem custom = CustomItemsPlugin.getInstance().getSet().getItem(item);
		if (custom != null && custom.forbidDefaultUse(item)) {
			// Don't let custom items be used as their internal item
			event.setCancelled(true);
		}
	}

	/*
	 * The mending enchantment is ignored on unbreakable items
	 * 
	 * @EventHandler(priority = EventPriority.HIGHEST) public void
	 * handleCustomMending(PlayerItemMendEvent event) { if
	 * (CustomItem.isCustom(event.getItem())) { CustomItem custom =
	 * CustomItemsPlugin.getInstance().getSet().getItem(event.getItem()); if (custom
	 * != null) { if (custom instanceof CustomTool) { CustomTool tool = (CustomTool)
	 * custom; int repaired = tool.increaseDurability(event.getItem(),
	 * event.getRepairAmount()); int newXP =
	 * event.getExperienceOrb().getExperience() - repaired / 2; if (newXP < 0)
	 * event.getExperienceOrb().remove();
	 * event.getExperienceOrb().setExperience(newXP); } event.setCancelled(true); }
	 * } }
	 */

	@EventHandler
	public void beforeXP(PlayerExpChangeEvent event) {
		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		if (CustomItem.isCustom(item)) {
			CustomItem custom = CustomItemsPlugin.getInstance().getSet().getItem(item);
			if (custom != null) {
				if (item.containsEnchantment(Enchantment.MENDING) && custom instanceof CustomTool) {
					CustomTool tool = (CustomTool) custom;
					long repaired = tool.increaseDurability(item, event.getAmount() * 2);

					// Let's assume the int range will not be exceeded with a single xp orb
					int newXP = (int) (event.getAmount() - repaired / 2);
					event.setAmount(newXP);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void processAnvil(PrepareAnvilEvent event) {
		ItemStack[] contents = event.getInventory().getStorageContents();
		CustomItem custom1 = null;
		CustomItem custom2 = null;
		if (CustomItem.isCustom(contents[0]))
			custom1 = CustomItemsPlugin.getInstance().getSet().getItem(contents[0]);
		if (CustomItem.isCustom(contents[1]))
			custom2 = CustomItemsPlugin.getInstance().getSet().getItem(contents[1]);
		if (custom1 != null) {
			if (custom1.allowAnvilActions()) {
				if (custom1 instanceof CustomTool) {
					CustomTool tool = (CustomTool) custom1;
					String renameText = event.getInventory().getRenameText();
					String oldName = ItemHelper.getStackName(contents[0]);
					if (custom1 == custom2) {
						long durability1 = tool.getDurability(contents[0]);
						long durability2 = tool.getDurability(contents[1]);
						long resultDurability = Math.min(durability1 + durability2, tool.getMaxDurability());
						Map<Enchantment, Integer> enchantments1 = contents[0].getEnchantments();
						Map<Enchantment, Integer> enchantments2 = contents[1].getEnchantments();
						ItemStack result = tool.create(1, resultDurability);
						int levelCost = 0;
						boolean hasChange = false;
						if (!renameText.isEmpty() && !renameText.equals(oldName)) {
							ItemMeta meta = result.getItemMeta();
							meta.setDisplayName(event.getInventory().getRenameText());
							result.setItemMeta(meta);
							levelCost++;
							hasChange = true;
						} else {
							ItemMeta meta = result.getItemMeta();
							meta.setDisplayName(oldName);
							result.setItemMeta(meta);
						}
						result.addUnsafeEnchantments(enchantments1);
						Set<Entry<Enchantment, Integer>> entrySet = enchantments2.entrySet();
						for (Entry<Enchantment, Integer> entry : entrySet) {
							if (entry.getKey().canEnchantItem(result)) {
								try {
									result.addEnchantment(entry.getKey(), entry.getValue());
									levelCost += entry.getValue() * getItemEnchantFactor(entry.getKey());
									hasChange = true;
								} catch (IllegalArgumentException illegal) {
									// The rules from the wiki
									levelCost++;
								} // Only add enchantments that can be added
							}
						}
						int repairCost1 = 0;
						int repairCost2 = 0;
						ItemMeta meta1 = contents[0].getItemMeta();
						if (meta1 instanceof Repairable) {
							Repairable repairable = (Repairable) meta1;
							repairCost1 = repairable.getRepairCost();
							levelCost += repairCost1;
						}
						ItemMeta meta2 = contents[1].getItemMeta();
						if (meta2 instanceof Repairable) {
							Repairable repairable = (Repairable) meta2;
							repairCost2 = repairable.getRepairCost();
							levelCost += repairCost2;
						}
						ItemMeta resultMeta = result.getItemMeta();
						int maxRepairCost = Math.max(repairCost1, repairCost2);
						int maxRepairCount = (int) Math.round(Math.log(maxRepairCost + 1) / Math.log(2));
						((Repairable) resultMeta).setRepairCost((int) Math.round(Math.pow(2, maxRepairCount + 1) - 1));
						result.setItemMeta(resultMeta);
						if (tool.getDurability(contents[0]) < tool.getMaxDurability()) {
							levelCost += 2;
							hasChange = true;
						}
						if (hasChange) {
							event.setResult(result);
							int finalLevelCost = levelCost;
							Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
								// Apparently, settings the repair cost during the event has no effect
								event.getInventory().setRepairCost(finalLevelCost);
							});
						} else {
							event.setResult(null);
						}
					} else if (contents[1] != null && !ItemHelper.getMaterialName(contents[1]).equals(CIMaterial.AIR.name())) {
						if (ItemHelper.getMaterialName(contents[1]).equals(CIMaterial.ENCHANTED_BOOK.name())) {
							/*
							 * Ehm... yes... I kinda forgot this works fine automatically before writing
							 * this...
							 * 
							 * ItemMeta meta2 = contents[1].getItemMeta(); ItemStack result =
							 * contents[0].clone(); if (meta2 instanceof EnchantmentStorageMeta) {
							 * EnchantmentStorageMeta esm = (EnchantmentStorageMeta) meta2; int levelCost =
							 * 2; Set<Entry<Enchantment,Integer>> entrySet =
							 * esm.getStoredEnchants().entrySet(); for (Entry<Enchantment,Integer> entry :
							 * entrySet) { if (entry.getKey().canEnchantItem(result)) { try {
							 * result.addEnchantment(entry.getKey(), entry.getValue()); levelCost +=
							 * entry.getValue() * getBookEnchantFactor(entry.getKey()); } catch
							 * (IllegalArgumentException illegal) { // The rules from the wiki levelCost++;
							 * } // Only add enchantments that can be added } } int repairCount1 = 0; int
							 * repairCount2 = 0; ItemMeta meta1 = contents[0].getItemMeta(); if (meta1
							 * instanceof Repairable) { Repairable repairable = (Repairable) meta1;
							 * System.out.println("repairable1: " + repairable.getRepairCost());
							 * repairCount1 = repairable.getRepairCost(); levelCost += Math.pow(2,
							 * repairCount1) - 1; } if (meta2 instanceof Repairable) { Repairable repairable
							 * = (Repairable) meta2; System.out.println("repairable2: " +
							 * repairable.getRepairCost()); repairCount2 = repairable.getRepairCost();
							 * levelCost += Math.pow(2, repairCount2) - 1; } if
							 * (!event.getInventory().getRenameText().isEmpty()) { ItemMeta meta =
							 * result.getItemMeta();
							 * meta.setDisplayName(event.getInventory().getRenameText());
							 * result.setItemMeta(meta); levelCost++; } ItemMeta resultMeta =
							 * result.getItemMeta(); ((Repairable)
							 * resultMeta).setRepairCost(Math.max(repairCount1, repairCount2) + 1);
							 * result.setItemMeta(resultMeta); event.setResult(result); int finalLevelCost =
							 * levelCost;
							 * Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance()
							 * , () -> { // Apparently, settings the repair cost during the event has no
							 * effect event.getInventory().setRepairCost(finalLevelCost); }); } else {
							 * event.setResult(null); }
							 */
						} else if (tool.getRepairItem().accept(contents[1])) {
							long durability = tool.getDurability(contents[0]);
							long maxDurability = tool.getMaxDurability();
							long neededDurability = maxDurability - durability;
							if (neededDurability > 0) {
								int neededAmount = (int) Math.ceil(neededDurability * 4.0 / maxDurability);
								int usedAmount = Math.min(neededAmount, contents[1].getAmount());
								long resultDurability = Math.min(durability + tool.getMaxDurability() * usedAmount / 4,
										tool.getMaxDurability());
								ItemStack result = tool.create(1, resultDurability);
								result.addUnsafeEnchantments(contents[0].getEnchantments());
								int levelCost = usedAmount;
								if (!renameText.isEmpty() && !renameText.equals(oldName)) {
									levelCost++;
									ItemMeta meta = result.getItemMeta();
									meta.setDisplayName(event.getInventory().getRenameText());
									result.setItemMeta(meta);
								} else {
									ItemMeta meta = result.getItemMeta();
									meta.setDisplayName(oldName);
									result.setItemMeta(meta);
								}
								int repairCost = 0;
								ItemMeta meta1 = contents[0].getItemMeta();
								if (meta1 instanceof Repairable) {
									Repairable repairable = (Repairable) meta1;
									repairCost = repairable.getRepairCost();
									levelCost += repairCost;
								}
								ItemMeta resultMeta = result.getItemMeta();
								int repairCount = (int) Math.round(Math.log(repairCost + 1) / Math.log(2));
								// TODO repair cost becomes invisible after no change?
								((Repairable) resultMeta)
										.setRepairCost((int) Math.round(Math.pow(2, repairCount + 1) - 1));
								result.setItemMeta(resultMeta);
								event.setResult(result);
								int finalLevelCost = levelCost;
								Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
									// Apparently, settings the repair cost during the event has no effect
									event.getInventory().setRepairCost(finalLevelCost);
									/*
									 * if (finalLevelCost == event.getInventory().getRepairCost()) {
									 * System.out.println("Force level cost update");
									 * Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance()
									 * , () -> { event.getInventory().setItem(0, event.getInventory().getItem(0));
									 * }); }
									 */
								});
							} else {
								event.setResult(null);
							}
						} else {
							event.setResult(null);
						}
					} else {
						// This else block is for the case where the first slot is a custom item and the
						// second slot is empty, so eventually for renaming.
						// This else block is empty because minecraft itself takes care of it.
					}
				} else {
					event.setResult(null);
				}
			} else {
				event.setResult(null);
			}
		} else if (custom2 != null) {
			event.setResult(null);
		}
	}

	private static int getItemEnchantFactor(Enchantment e) {
		if (e.equals(PROTECTION_FIRE) || e.equals(PROTECTION_FALL) || e.equals(PROTECTION_PROJECTILE)
				|| e.equals(DAMAGE_UNDEAD) || e.equals(DAMAGE_ARTHROPODS) || e.equals(KNOCKBACK)
				|| e.equals(DURABILITY)) {
			return 2;
		}
		if (e.equals(PROTECTION_EXPLOSIONS) || e.equals(OXYGEN) || e.equals(WATER_WORKER) || e.equals(DEPTH_STRIDER)
				|| e.equals(FROST_WALKER) || e.equals(FIRE_ASPECT) || e.equals(LOOT_BONUS_MOBS)
				|| e.equals(SWEEPING_EDGE) || e.equals(LOOT_BONUS_BLOCKS) || e.equals(ARROW_KNOCKBACK)
				|| e.equals(ARROW_FIRE) || e.equals(LUCK) || e.equals(LURE) || e.equals(MENDING)) {
			return 4;
		}
		if (e.equals(THORNS) || e.equals(BINDING_CURSE) || e.equals(SILK_TOUCH) || e.equals(ARROW_INFINITE)
				|| e.equals(VANISHING_CURSE)) {
			return 8;
		}
		return 1;
	}

	/*
	 * private static int getBookEnchantFactor(Enchantment e) { if (e ==
	 * Enchantment.PROTECTION_EXPLOSIONS || e == Enchantment.OXYGEN || e ==
	 * Enchantment.WATER_WORKER || e == Enchantment.DEPTH_STRIDER || e ==
	 * Enchantment.FROST_WALKER || e == Enchantment.LOOT_BONUS_MOBS || e ==
	 * Enchantment.SWEEPING_EDGE || e == Enchantment.LOOT_BONUS_BLOCKS || e ==
	 * Enchantment.ARROW_KNOCKBACK || e == Enchantment.ARROW_FIRE || e ==
	 * Enchantment.LUCK || e == Enchantment.LURE || e == Enchantment.MENDING) {
	 * return 2; } if (e == Enchantment.THORNS || e == Enchantment.BINDING_CURSE ||
	 * e == Enchantment.SILK_TOUCH || e == Enchantment.ARROW_INFINITE || e ==
	 * Enchantment.VANISHING_CURSE) { return 4; } return 1; }
	 */

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void cancelEnchanting(PrepareItemEnchantEvent event) {
		CustomItem custom = CustomItemsPlugin.getInstance().getSet().getItem(event.getItem());
		if (custom != null && !custom.allowVanillaEnchanting())
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		SlotType type = event.getSlotType();
		InventoryAction action = event.getAction();
		if (type == SlotType.RESULT) {
			if (event.getInventory() instanceof MerchantInventory) {
				MerchantInventory inv = (MerchantInventory) event.getInventory();
				MerchantRecipe recipe = null;
				try {
					recipe = inv.getSelectedRecipe();
				} catch (NullPointerException npe) {
					// When the player hasn't inserted enough items, above method will
					// throw a NullPointerException. If that happens, recipe will stay
					// null and thus the next if block won't be executed.
				}
				if (recipe != null) {
					if (event.getAction() != InventoryAction.NOTHING) {
						ItemStack[] contents = inv.getContents();
						List<ItemStack> ingredients = recipe.getIngredients();
						int recipeAmount0 = ingredients.get(0).getAmount();
						boolean hasSecondIngredient = ingredients.size() > 1 && ingredients.get(1) != null;
						int recipeAmount1 = hasSecondIngredient ? ingredients.get(1).getAmount() : 0;
						boolean overrule0 = CustomItem.isCustom(contents[0]) && contents[0].getAmount() > recipeAmount0;
						boolean overrule1 = CustomItem.isCustom(contents[1]) && contents[1].getAmount() > recipeAmount1;
						if (overrule0 || overrule1) {

							event.setCancelled(true);
							if (event.isLeftClick()) {
								// The default way of trading
								if (event.getAction() == InventoryAction.PICKUP_ALL) {

									// We will have to do this manually...
									if (event.getCursor() == null || ItemHelper.getMaterialName(event.getCursor()).equals(CIMaterial.AIR.name())) {
										event.setCursor(recipe.getResult());
									} else {
										event.getCursor().setAmount(
												event.getCursor().getAmount() + recipe.getResult().getAmount());
									}
									if (contents[0] != null && !ItemHelper.getMaterialName(contents[0]).equals(CIMaterial.AIR.name())) {
										int newAmount = contents[0].getAmount() - recipeAmount0;
										if (newAmount > 0) {
											contents[0].setAmount(newAmount);
										} else {
											contents[0] = null;
										}
									}
									if (contents[1] != null && !ItemHelper.getMaterialName(contents[1]).equals(CIMaterial.AIR.name())
											&& ingredients.size() > 1 && ingredients.get(1) != null) {
										int newAmount = contents[1].getAmount() - recipeAmount1;
										if (newAmount > 0) {
											contents[1].setAmount(newAmount);
										} else {
											contents[1] = null;
										}
									}
									inv.setContents(contents);
								}

								// Using shift-click for trading
								else if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {

									int trades = contents[0].getAmount() / recipeAmount0;
									if (hasSecondIngredient) {
										int trades2 = contents[1].getAmount() / recipeAmount1;
										if (trades2 < trades) {
											trades = trades2;
										}
									}

									{
										int newAmount = contents[0].getAmount() - trades * recipeAmount0;
										if (newAmount > 0) {
											contents[0].setAmount(newAmount);
										} else {
											contents[0] = null;
										}
									}
									if (hasSecondIngredient) {
										int newAmount = contents[1].getAmount() - trades * recipeAmount1;
										if (newAmount > 0) {
											contents[1].setAmount(newAmount);
										} else {
											contents[1] = null;
										}
									}

									ItemStack result = recipe.getResult();
									for (int counter = 0; counter < trades; counter++) {
										event.getWhoClicked().getInventory().addItem(result);
									}

									inv.setContents(contents);
								}

								// If I forgot a case, it will go in here. Cancel it to prevent dangerous
								// glitches
								else {
									event.setCancelled(true);
								}
							} else {

								// I will only allow left click trading
								event.setCancelled(true);
							}
						}
					}
				}
			}
			if (event.getInventory() instanceof CraftingInventory) {
				if (shouldInterfere.getOrDefault(event.getWhoClicked().getUniqueId(), false)) {
					if (action == InventoryAction.PICKUP_ALL) {
						// This block deals with normal crafting
						Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
							/*
							 * For every itemstack in crafting matrix when 1 item was crafted: actualAmount
							 * = 2 * (initialAmount - 1) desiredAmount = initialAmount - 1 desiredAmount =
							 * actualAmount / 2;
							 */
							ItemStack[] contents = event.getInventory().getContents();
							for (int index = 1; index < contents.length; index++) {
								contents[index].setAmount(contents[index].getAmount() / 2);
							}
						});
					} else if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
						// This block deals with shift clicks on the result slot
						int amountPerCraft = event.getCurrentItem().getAmount();
						int minAmount = 64;
						ItemStack[] contents = event.getInventory().getContents();
						ItemStack result = contents[0].clone();
						for (int index = 1; index < contents.length; index++)
							if (!ItemHelper.getMaterialName(contents[index]).equals(CIMaterial.AIR.name()) && contents[index].getAmount() < minAmount)
								minAmount = contents[index].getAmount();
						event.setResult(Result.DENY);
						for (int index = 1; index < contents.length; index++)
							contents[index].setAmount(contents[index].getAmount() - minAmount);
						event.getInventory().setItem(0, ItemHelper.createStack(CIMaterial.AIR.name(), 1));
						CustomItem customResult = CustomItemsPlugin.getInstance().getSet().getItem(result);
						int amountToGive = amountPerCraft * minAmount;
						if (customResult != null && !customResult.canStack()) {
							for (int counter = 0; counter < amountToGive; counter++) {
								event.getWhoClicked().getInventory().addItem(result);
							}
						} else {
							int maxStacksize = customResult == null ? 64 : customResult.getMaxStacksize();
							for (int counter = 0; counter < amountToGive; counter += maxStacksize) {
								int left = amountToGive - counter;
								if (left > maxStacksize) {
									result.setAmount(maxStacksize);
									event.getWhoClicked().getInventory().addItem(result);
								} else {
									result.setAmount(left);
									event.getWhoClicked().getInventory().addItem(result);
									break;
								}
							}
						}
					} else if (action == InventoryAction.NOTHING) {
						// This case is possible when a custom item is on the cursor because it isn't
						// really stackable
						ItemStack cursor = event.getCursor();
						ItemStack current = event.getCurrentItem();
						if (CustomItem.isCustom(cursor) && CustomItem.isCustom(current)
								&& cursor.getType() == current.getType()
								&& cursor.getDurability() == current.getDurability()) {
							CustomItem custom = CustomItemsPlugin.getInstance().getSet().getItem(current);
							if (custom != null && custom.canStack()
									&& cursor.getAmount() + current.getAmount() <= custom.getMaxStacksize()) {
								Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
									ItemStack[] contents = event.getInventory().getContents();
									for (int index = 1; index < contents.length; index++) {
										if (contents[index].getAmount() > 1)
											contents[index].setAmount(contents[index].getAmount() - 1);
										else
											contents[index] = null;
									}
									event.getInventory().setContents(contents);
									cursor.setAmount(cursor.getAmount() + current.getAmount());
									event.getView().getPlayer().setItemOnCursor(cursor);
									beforeCraft((CraftingInventory) event.getInventory(), event.getView().getPlayer());
								});
							}
						}
					} else {
						// Maybe, there is some edge case I don't know about, so cancel it just to be
						// sure
						event.setResult(Result.DENY);
					}
				}
			} else if (event.getInventory() instanceof AnvilInventory) {
				// By default, Minecraft does not allow players to pick illegal items from
				// anvil, so...
				ItemStack cursor = event.getCursor();
				ItemStack current = event.getCurrentItem();
				if (event.getCurrentItem() == null || ItemHelper.getMaterialName(event.getCurrentItem()).equals(CIMaterial.AIR.name())) {
					event.setCancelled(true);
					Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
						if (event.getWhoClicked() instanceof Player) {
							Player player = (Player) event.getWhoClicked();
							player.setExp(player.getExp());
							player.closeInventory();
						}
					});
				} else if ((cursor == null || ItemHelper.getMaterialName(cursor).equals(CIMaterial.AIR.name())) && CustomItem.isCustom(current)) {
					AnvilInventory ai = (AnvilInventory) event.getInventory();
					CustomItem custom = CustomItemsPlugin.getInstance().getSet().getItem(current);
					ItemStack first = event.getInventory().getItem(0);
					CustomItem customFirst = CustomItemsPlugin.getInstance().getSet().getItem(first);
					if (customFirst != null && !customFirst.allowAnvilActions()) {
						event.setCancelled(true);
						Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
							if (event.getWhoClicked() instanceof Player) {
								Player player = (Player) event.getWhoClicked();
								player.setExp(player.getExp());
							}
						});
					} else if (custom != null && event.getView().getPlayer() instanceof Player) {
						Player player = (Player) event.getView().getPlayer();
						int repairCost = ai.getRepairCost();
						if (player.getLevel() >= repairCost) {
							player.setItemOnCursor(current);
							player.setLevel(player.getLevel() - repairCost);
							ItemStack[] contents = ai.getContents();
							if (custom instanceof CustomTool && contents[1] != null
									&& !ItemHelper.getMaterialName(contents[1]).equals(CIMaterial.AIR.name())) {
								CustomTool tool = (CustomTool) custom;
								if (tool.getRepairItem().accept(contents[1])) {
									long durability = tool.getDurability(contents[0]);
									long maxDurability = tool.getMaxDurability();
									long neededDurability = maxDurability - durability;
									int neededAmount = (int) Math.ceil(neededDurability * 4.0 / maxDurability);
									int usedAmount = Math.min(neededAmount, contents[1].getAmount());
									if (usedAmount < contents[1].getAmount())
										contents[1].setAmount(contents[1].getAmount() - usedAmount);
									else
										contents[1] = null;
								} else {
									contents[1] = null;
								}
							} else {
								contents[1] = null;
							}
							contents[0] = null;
							// apparently, the length of contents is 2
							ai.setContents(contents);
						}
					}
				}
			}
		} else if (action == InventoryAction.NOTHING || action == InventoryAction.PICKUP_ONE
				|| action == InventoryAction.PICKUP_SOME || action == InventoryAction.SWAP_WITH_CURSOR) {
			ItemStack cursor = event.getCursor();
			ItemStack current = event.getCurrentItem();
			// This block makes custom items stackable
			if (CustomItem.isCustom(cursor) && CustomItem.isCustom(current)) {
				ItemSet set = CustomItemsPlugin.getInstance().getSet();
				CustomItem customCursor = set.getItem(cursor);
				CustomItem customCurrent = set.getItem(current);
				if (customCursor != null && customCursor == customCurrent && customCursor.canStack()) {
					event.setResult(Result.DENY);
					if (event.isLeftClick()) {
						int amount = current.getAmount() + cursor.getAmount();
						if (amount <= customCursor.getMaxStacksize()) {
							current.setAmount(amount);
							cursor.setAmount(0);
						} else {
							current.setAmount(customCursor.getMaxStacksize());
							cursor.setAmount(amount - customCursor.getMaxStacksize());
						}
					} else {
						int newAmount = current.getAmount() + 1;
						if (newAmount <= customCurrent.getMaxStacksize()) {
							cursor.setAmount(cursor.getAmount() - 1);
							current.setAmount(newAmount);
						}
					}
					Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
						event.getView().getPlayer().setItemOnCursor(cursor);
					});
				}
			}
		}
		// Force a PrepareAnvilEvent
		if (event.getInventory() instanceof AnvilInventory) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
				event.getInventory().setItem(0, event.getInventory().getItem(0));
			});
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void beforeCraft(PrepareItemCraftEvent event) {
		beforeCraft(event.getInventory(), event.getView().getPlayer());
	}

	public void beforeCraft(CraftingInventory inventory, HumanEntity owner) {
		ItemStack result = inventory.getResult();

		// Block vanilla recipes that attempt to use custom items
		if (result != null && !ItemHelper.getMaterialName(result).equals(CIMaterial.AIR.name())) {
			ItemStack[] ingredients = inventory.getStorageContents();
			for (ItemStack ingredient : ingredients) {
				if (CustomItem.isCustom(ingredient)) {
					inventory.setResult(ItemHelper.createStack(CIMaterial.AIR.name(), 1));
					break;
				}
			}
		}

		// Check if there are any custom recipes matching the ingredients
		CustomRecipe[] recipes = CustomItemsPlugin.getInstance().getSet().getRecipes();
		if (recipes.length > 0) {
			// Determine ingredients
			ItemStack[] ingredients = inventory.getStorageContents();
			ingredients = Arrays.copyOfRange(ingredients, 1, ingredients.length);

			// Shaped recipes first because they have priority
			for (int index = 0; index < recipes.length; index++) {
				if (recipes[index] instanceof ShapedCustomRecipe && recipes[index].shouldAccept(ingredients)) {
					inventory.setResult(recipes[index].getResult());
					shouldInterfere.put(owner.getUniqueId(), true);
					return;
				}
			}

			// No shaped recipe fits, so try the shapeless recipes
			for (int index = 0; index < recipes.length; index++) {
				if (recipes[index] instanceof ShapelessCustomRecipe && recipes[index].shouldAccept(ingredients)) {
					inventory.setResult(recipes[index].getResult());
					shouldInterfere.put(owner.getUniqueId(), true);
					return;
				}
			}
		}
		shouldInterfere.put(owner.getUniqueId(), false);
	}
	
	@EventHandler
	public void onServerCommand(ServerCommandEvent event) {
		try {
			event.setCommand(substituteCommand(event.getCommand()));
		} catch (IllegalArgumentException | UnsupportedOperationException ex) {
			event.getSender().sendMessage(ex.getMessage());
		}
	}
	
	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		try {
			event.setMessage(substituteCommand(event.getMessage()));
		} catch (IllegalArgumentException | UnsupportedOperationException ex) {
			event.getPlayer().sendMessage(ex.getMessage());
		}
	}
	
	private static final String SUFFIX = ">";
	private static final String MATERIAL_PREFIX = "<ci-material ";
	private static final String DAMAGE_PREFIX = "<ci-damage ";
	private static final String TAG_PREFIX = "<ci-tag ";
	private static final String INNER_PREFIX = "<ci-inner ";
	
	private String substituteCommand(String command) throws IllegalArgumentException {
		command = substitute(command, MATERIAL_PREFIX, (CustomItem item, int amount) -> {
			return item.getItemType().getMinecraftName();
		});
		command = substitute(command, DAMAGE_PREFIX, (CustomItem item, int amount) -> {
			return Short.toString(item.getItemDamage());
		});
		command = substitute(command, TAG_PREFIX, (CustomItem item, int amount) -> {
			if (CorePlugin.useNewCommands()) {
				return item.getNBTTag14();
			} else {
				return item.getNBTTag12();
			}
		});
		command = substitute(command, INNER_PREFIX, (CustomItem item, int amount) -> {
			if (CorePlugin.useNewCommands()) {
				return item.getEquipmentTag14(amount);
			} else {
				return item.getEquipmentTag12(amount);
			}
		});
		
		return command;
	}
	
	private String substitute(String string, String prefix, CustomItemSubstitutor substitutor) throws IllegalArgumentException {
		int prefixIndex = string.indexOf(prefix);
		while (prefixIndex != -1) {
			int startIndex = prefixIndex + prefix.length();
			int suffixIndex = string.indexOf(SUFFIX, startIndex);
			
			if (suffixIndex == -1) {
				throw new IllegalArgumentException("Unfinished ci tag: " + string.substring(prefixIndex));
			}
			
			String tagContent = string.substring(startIndex, suffixIndex);
			int amount;
			int indexSpace = tagContent.indexOf(" ");
			if (indexSpace != -1) {
				tagContent = tagContent.substring(0, indexSpace);
				String amountString = tagContent.substring(indexSpace + 1);
				try {
					amount = Integer.parseInt(amountString);
				} catch (NumberFormatException ex) {
					throw new IllegalArgumentException("The amount (" + amountString + ") for the custom item \"" + tagContent + "\" should be an integer");
				}
			} else {
				amount = 1;
			}
			CustomItem customItem = CustomItemsPlugin.getInstance().getSet().getItem(tagContent);
			
			if (customItem == null) {
				throw new IllegalArgumentException("There is no custom item with name \"" + tagContent + "\"");
			}
			
			String replacement = substitutor.process(customItem, amount);
			
			string = string.substring(0, prefixIndex) + replacement + string.substring(suffixIndex + 1);
			prefixIndex = string.indexOf(prefix);
		}
		
		return string;
	}
	
	private static interface CustomItemSubstitutor {
		
		String process(CustomItem item, int amount);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onItemPickup(EntityPickupItemEvent event) {
		ItemStack stack = event.getItem().getItemStack();
		if (event.getEntityType() == EntityType.PLAYER && CustomItem.isCustom(stack)) {
			CustomItem customItem = CustomItemsPlugin.getInstance().getSet().getItem(stack);
			if (customItem != null) {
				int remainingAmount = stack.getAmount();
				Player player = (Player) event.getEntity();
				Inventory inv = player.getInventory();
				ItemStack[] contents = inv.getContents();
				for (ItemStack content : contents) {
					if (customItem.is(content)) {
						int remainingSpace = customItem.getMaxStacksize() - content.getAmount();
						if (remainingSpace > 0) {
							if (remainingSpace >= remainingAmount) {
								content.setAmount(content.getAmount() + remainingAmount);
								event.getItem().remove();
								event.setCancelled(true);
								return;
							} else {
								content.setAmount(customItem.getMaxStacksize());
								remainingAmount -= remainingSpace;
							}
						}
					}
				}
				
				if (remainingAmount != stack.getAmount()) {
					stack.setAmount(remainingAmount);
					event.getItem().setItemStack(stack);
					
					// Apparently, canceling the event is necessary because it won't let me change
					// the picked up amount.
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void fixShulkerBoxes(BlockBreakEvent event) {
		Block block = event.getBlock();
		if (block.getState() instanceof ShulkerBox) {
			if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
				ShulkerBox shulker = (ShulkerBox) block.getState();
				event.setDropItems(false);
				
				ItemStack stackToDrop = new ItemStack(block.getType());
				ItemMeta meta = stackToDrop.getItemMeta();
				BlockStateMeta bms = (BlockStateMeta) meta;
				bms.setBlockState(shulker);
				stackToDrop.setItemMeta(bms);
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), stackToDrop);
			}
		}
	}
}