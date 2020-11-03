package nl.knokko.customitems.plugin.set.item.update;

import java.util.ArrayList;
import java.util.Collection;

import static org.bukkit.enchantments.Enchantment.getByName;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.EnchantmentType;
import nl.knokko.customitems.item.ItemFlag;
import nl.knokko.core.plugin.item.attributes.ItemAttributes;
import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.customitems.plugin.set.item.BooleanRepresentation;
import nl.knokko.customitems.plugin.set.item.CustomItem;
import nl.knokko.customitems.plugin.set.item.CustomItemNBT;

public class ItemUpdater {

	private final CustomItem[] items;
	private final long setLastModified;
	
	public ItemUpdater(CustomItem[] items, long setLastModified) {
		this.items = items;
		this.setLastModified = setLastModified;
	}
	
	public ItemStack maybeUpdate(ItemStack originalStack) {
		CustomItem[] pOldItem = {null};
		CustomItem[] pNewItem = {null};
		UpdateAction[] pAction = {null};
		
		CustomItemNBT.readOnly(originalStack, nbt -> {
			if (nbt.hasOurNBT()) {
				String itemName = nbt.getName();
				
				Long lastModified = nbt.getLastModified();
				if (lastModified == null) {
					
					/*
					 * If this line is reached, the lastModified of the item stack is
					 * missing. That means the item stack wasn't created by this
					 * plug-in.
					 * 
					 * We use this case to make it easier for other plug-ins to
					 * create custom items of this plug-in: Some other plug-in creates
					 * an item stack with our nbt tag, and sets only the Name to the
					 * desired custom item name. As soon as this plug-in finds such
					 * an item stack, it will replace it by a proper item stack
					 * representation of the desired custom item.
					 */
					CustomItem currentItem = getItemByName(itemName);
					if (currentItem != null) {
						pNewItem[0] = currentItem;
						pAction[0] = UpdateAction.INITIALIZE;
					} else {
						// I'm not really sure how this case should be handled
						pAction[0] = UpdateAction.DO_NOTHING;
					}
				} else if (lastModified == setLastModified) {
					
					/*
					 * If the lastModified of the item stack is the same as the 
					 * lastModified of the item set, there is no need to take any 
					 * action because the item stack properties should already be 
					 * up-to-date.
					 */
					pAction[0] = UpdateAction.DO_NOTHING;
				} else {
					
					CustomItem currentItem = getItemByName(itemName);
					if (currentItem != null) {
						
						BooleanRepresentation oldBoolRepresentation = nbt.getBooleanRepresentation();
						BooleanRepresentation newBoolRepresentation = currentItem.getBooleanRepresentation();
						if (oldBoolRepresentation.equals(newBoolRepresentation)) {
							/*
							 * This case will happen when the item set is updated,
							 * but the current custom item stayed the same. If this
							 * happens, we don't need to upgrade the item stack,
							 * but we should update the LastModified to prevent the
							 * need for comparing the boolean representations over
							 * and over.
							 */
							pAction[0] = UpdateAction.UPDATE_LAST_MODIFIED;
						} else {
							
							/*
							 * This case will happen when a custom item stack is
							 * given to a player, but the admin changes some stats
							 * of that custom item (using the Editor). Unfortunately,
							 * some stats of the custom item stack in that player
							 * inventory will keep the old values.
							 * 
							 * To update these stats, we need to take action (this is
							 * the primary purpose of this class). This case is more
							 * complicated than it seems because we can't just
							 * completely recreate the item stack (that would for
							 * instance erase the enchantments and durability).
							 */
							CustomItem oldItem = ItemSet.loadOldItem(oldBoolRepresentation);
							if (oldItem != null) {
								pOldItem[0] = oldItem;
								pNewItem[0] = currentItem;
								pAction[0] = UpdateAction.UPGRADE;
							} else {
								/*
								 * oldItem will be null if the deserizaliation of the
								 * boolean representation failed (due to an unknown
								 * encoding). There are 2 possible causes for this
								 * situation:
								 * 
								 * 1) This item stack was created by a newer version
								 *    of this plug-in. This must mean that someone
								 *    downgraded to an older version of the plug-in.
								 * 2) The nbt of the item got corrupted somehow.
								 * 
								 * I don't think there is a good solution for this
								 * situation, but I think doing nothing is best. 
								 * (Note that the loadOldItem method already
								 * logged an error, so no need to do it twice.)
								 */
								pAction[0] = UpdateAction.DO_NOTHING;
							}
						}
					} else {
						/*
						 * This case will happen when a custom item stack is given to
						 * someone, but that custom item is later removed from the
						 * item set (the admin deleted it via the Editor and updated
						 * the server item set).
						 * 
						 * But, it is also possible that the server accidentally
						 * loaded the wrong item set. (Note: If some start-up
						 * error occurred, this method shouldn't be called in
						 * the first place.)
						 * 
						 * I think simply deleting the corresponding in-game item 
						 * stack is the best way to deal with this case.
						 */
						// TODO Reconsider this! This is lethal when the
						// wrong item set is used by mistake!
						pAction[0] = UpdateAction.DESTROY;
					}
				}
			} else {
				/*
				 * If the item stack doesn't have our nbt tag, we assume it is not a
				 * custom item and we thus shouldn't mess with it.
				 */
				pAction[0] = UpdateAction.DO_NOTHING;
			}
		});
		
		UpdateAction action = pAction[0];
		
		if (action == UpdateAction.DO_NOTHING) {
			return originalStack;
		} else if (action == UpdateAction.DESTROY) {
			return null;
		} else if (action == UpdateAction.UPDATE_LAST_MODIFIED) {
			ItemStack[] pResult = {null};
			CustomItemNBT.readWrite(originalStack, nbt -> {
				nbt.setLastModified(setLastModified);
			}, result -> pResult[0] = result);
			return pResult[0];
		} else {
			
			CustomItem newItem = pNewItem[0];
			if (action == UpdateAction.INITIALIZE) {
				return newItem.create(originalStack.getAmount());
			} else if (action == UpdateAction.UPGRADE) {
				
				CustomItem oldItem = pOldItem[0];
				return upgradeItem(originalStack, oldItem, newItem);
			} else {
				throw new Error("Unknown update action: " + action);
			}
		}
	}
	
	private ItemStack upgradeItem(ItemStack oldStack, CustomItem oldItem, CustomItem newItem) {
		
		// We start with the attribute modifiers
		ItemAttributes.Single[] newStackAttributes = determineUpgradedAttributes(
				oldStack, oldItem, newItem
		);
		
		ItemStack withoutAttributes = ItemAttributes.clearAttributes(oldStack);
		ItemStack newStack = ItemAttributes.setAttributes(withoutAttributes, newStackAttributes);

		ItemStack[] pNewStack = {null};
		Long[] pNewDurability = {null};
		CustomItemNBT.readWrite(newStack, nbt -> {
			Long currentDurability = nbt.getDurability();
			if (currentDurability != null) {
				if (newItem.getMaxDurabilityNew() != null) {
					/*
					 * There was durability, and there still is. We will do the
					 * following: if the new maximum durability became bigger,
					 * we increase the current durability by the difference between
					 * the old and new max durability.
					 * 
					 * If the new maximum durability is smaller than the old
					 * maximum durability, the current durability will be set to
					 * the new maximum durability if (and only if) the current
					 * durability is bigger.
					 * 
					 * These decisions are not necessarily perfect, but decisions
					 * have to be made.
					 */
					if (oldItem.getMaxDurabilityNew() != null) {
						if (newItem.getMaxDurabilityNew() >= oldItem.getMaxDurabilityNew()) {
							pNewDurability[0] = currentDurability 
									+ newItem.getMaxDurabilityNew() 
									- oldItem.getMaxDurabilityNew();
						} else {
							pNewDurability[0] = Math.min(
									currentDurability, 
									newItem.getMaxDurabilityNew()
							);
						}
					} else {
						// How is this even possible?
						// Anyway, this seems like the most logical response
						pNewDurability[0] = Math.min(
								currentDurability, 
								newItem.getMaxDurabilityNew()
						);
					}
				} else {
					// There was durability, but no more
					pNewDurability[0] = null;
				}
			} else {
				if (newItem.getMaxDurabilityNew() != null) {
					// There was no durability, but now there is.
					// Let's just start with full durability
					pNewDurability[0] = newItem.getMaxDurabilityNew();
				} else {
					// There was no durability, and there shouldn't be durability
					pNewDurability[0] = null;
				}
			}
			
			if (pNewDurability[0] != null) {
				nbt.setDurability(pNewDurability[0]);
			} else {
				nbt.removeDurability();
			}
		}, afterNbt -> pNewStack[0] = afterNbt);
		newStack = pNewStack[0];
		
		upgradeEnchantments(newStack, oldItem, newItem);
		
		ItemHelper.setMaterial(newStack, newItem.getMaterial().name());
		
		ItemMeta meta = newStack.getItemMeta();
		
		upgradeDisplayName(meta, oldItem, newItem);
		upgradeLore(meta, oldItem, newItem, pNewDurability[0]);
		upgradeItemFlags(meta, oldItem, newItem);
		
		meta.setUnbreakable(true);
		newStack.setItemMeta(meta);
		
		newStack.setDurability(newItem.getItemDamage());
		
		return newStack;
	}
	
	private void upgradeDisplayName(ItemMeta toUpgrade, CustomItem oldItem, CustomItem newItem) {
		/*
		 * If the item allows anvil actions, it is possible that the player renamed
		 * the item in an anvil. It would be bad to 'reset' the name each time the
		 * item set is updated, so I will instead replace all occurrences of the
		 * display name of the old custom item with the display name of the new
		 * custom item. (Even this might not always be ideal, but I don't think
		 * there is some 'perfect' behavior for this.)
		 */
		if (newItem.allowAnvilActions()) {
			if (toUpgrade.hasDisplayName()) {
				String currentDisplayName = toUpgrade.getDisplayName();
				String newDisplayName = currentDisplayName.replace(oldItem.getDisplayName(), newItem.getDisplayName());
				toUpgrade.setDisplayName(newDisplayName);
			}
			// else... well... the player decided to remove the custom name entirely
			// so lets keep it that way
		} else {
			toUpgrade.setDisplayName(newItem.getDisplayName());
		}
	}
	
	private void upgradeLore(ItemMeta toUpgrade, CustomItem oldItem, CustomItem newItem, Long newDurability) {
		/*
		 * I will do no attempt to 'upgrade' the lore rather than replacing it,
		 * because tools will overwrite lore each time they take durability
		 * anyway.
		 */
		toUpgrade.setLore(newItem.createLore(newDurability));
	}
	
	private void upgradeItemFlags(ItemMeta toUpgrade, CustomItem oldItem, CustomItem newItem) {
		/*
		 * We will only update the item flags that changed for optimal preservation
		 * of the custom values of the item stack being upgraded.
		 */
		boolean[] oldFlags = oldItem.getItemFlags();
		boolean[] newFlags = newItem.getItemFlags();
		ItemFlag[] allFlags = ItemFlag.values();
		for (int flagIndex = 0; flagIndex < allFlags.length; flagIndex++) {
			boolean oldHasFlag = flagIndex < oldFlags.length && oldFlags[flagIndex];
			boolean newHasFlag = flagIndex < newFlags.length && newFlags[flagIndex];
			if (oldHasFlag != newHasFlag) {
				ItemFlag currentFlag = allFlags[flagIndex];
				if (newHasFlag) {
					toUpgrade.addItemFlags(
							org.bukkit.inventory.ItemFlag.valueOf(currentFlag.name())
					);
				} else {
					toUpgrade.removeItemFlags(
							org.bukkit.inventory.ItemFlag.valueOf(currentFlag.name())
					);
				}
			}
		}
	}
	
	private ItemAttributes.Single[] determineUpgradedAttributes(
			ItemStack oldStack, CustomItem oldItem, CustomItem newItem
	) {
		
		/*
		 * If the oldStack had attribute modifiers that didn't come from oldItem,
		 * we should keep them. (Except if that attribute modifier is already part
		 * of the attribute modifiers of newItem.) This makes integration with other 
		 * plug-ins that assign attribute modifiers a bit nicer.
		 */
		ItemAttributes.Single[] oldStackAttributes = ItemAttributes.getAttributes(oldStack);
		Collection<ItemAttributes.Single> newStackAttributes = new ArrayList<>(
				oldStackAttributes.length - oldItem.getAttributeModifiers().length 
				+ newItem.getAttributeModifiers().length
		);
		
		oldStackLoop:
		for (ItemAttributes.Single oldStackAttribute : oldStackAttributes) {
			for (ItemAttributes.Single oldItemAttribute : oldItem.getAttributeModifiers()) {
				if (oldStackAttribute.equals(oldItemAttribute)) {
					continue oldStackLoop;
				}
			}
			for (ItemAttributes.Single newItemAttribute : newItem.getAttributeModifiers()) {
				if (oldStackAttribute.equals(newItemAttribute)) {
					continue oldStackLoop;
				}
			}
			
			newStackAttributes.add(oldStackAttribute);
		}
		
		// Obviously, we should also add the attribute modifiers of newItem
		for (ItemAttributes.Single newItemAttribute : newItem.getAttributeModifiers()) {
			newStackAttributes.add(newItemAttribute);
		}
		
		return newStackAttributes.toArray(new ItemAttributes.Single[newStackAttributes.size()]);
	}
	
	private void upgradeEnchantments(ItemStack toUpgrade, CustomItem oldItem, CustomItem newItem) {
		
		/*
		 * If the new item doesn't allow anvil actions, it should not be possible to
		 * add enchantments to it, so the item to upgrade should get only the default
		 * enchantments of the new item.
		 */
		if (!newItem.allowAnvilActions()) {
			toUpgrade.getEnchantments().keySet().forEach(enchantment -> toUpgrade.removeEnchantment(enchantment));
			for (Enchantment enchantment : newItem.getDefaultEnchantments()) {
				toUpgrade.addUnsafeEnchantment(
						getByName(enchantment.getType().name()), 
						enchantment.getLevel()
				);
			}
		} else {
			
			class ChangedEnchantment {
				
				final EnchantmentType type;
				final int oldLevel, newLevel;
				
				ChangedEnchantment(EnchantmentType type, int oldLevel, int newLevel) {
					this.type = type;
					this.oldLevel = oldLevel;
					this.newLevel = newLevel;
				}
			}
			
			Collection<Enchantment> removedEnchantments = new ArrayList<>();
			Collection<Enchantment> addedEnchantments = new ArrayList<>();
			Collection<ChangedEnchantment> changedEnchantments = new ArrayList<>();
			
			// Find out which enchantments are removed and which are upgraded
			outerLoop:
			for (Enchantment oldEnchantment : oldItem.getDefaultEnchantments()) {
				for (Enchantment newEnchantment : newItem.getDefaultEnchantments()) {
					if (oldEnchantment.getType() == newEnchantment.getType()) {
						if (oldEnchantment.getLevel() != newEnchantment.getLevel()) {
							changedEnchantments.add(new ChangedEnchantment(
									oldEnchantment.getType(), 
									oldEnchantment.getLevel(),
									newEnchantment.getLevel()
							));
						}
						continue outerLoop;
					}
				}
				
				removedEnchantments.add(oldEnchantment);
			}
			
			// Find out which enchantments are added
			outerLoop:
			for (Enchantment newEnchantment : newItem.getDefaultEnchantments()) {
				for (Enchantment oldEnchantment : oldItem.getDefaultEnchantments()) {
					if (newEnchantment.getType() == oldEnchantment.getType()) {
						continue outerLoop;
					}
				}
				
				addedEnchantments.add(newEnchantment);
			}
			
			for (Enchantment removed : removedEnchantments) {
				int currentLevel = toUpgrade.getEnchantmentLevel(
						getByName(removed.getType().name())
				);
				
				/*
				 * This case is a bit nasty because it is possible that the item to
				 * upgrade has the removed enchantment at a higher level than the
				 * level of the default enchantment of the old custom item. I'm not
				 * really sure what the desired behavior should be, but I will go
				 * with the following decision:
				 * 
				 * The enchantment level of the item to upgrade will be decreased by
				 * the level of the default enchantment of the old custom item.
				 */
				int newLevel = currentLevel - removed.getLevel();
				if (newLevel > 0) {
					toUpgrade.addEnchantment(getByName(removed.getType().name()), newLevel);
				} else {
					toUpgrade.removeEnchantment(getByName(removed.getType().name()));
				}
			}
			
			for (Enchantment added : addedEnchantments) {
				int currentLevel = toUpgrade.getEnchantmentLevel(
						getByName(added.getType().name())
				);
				
				/*
				 * It is possible that the item to upgrade already has the
				 * enchantment of the new default enchantment. Again, I'm not
				 * entirely sure what the desired behavior would be, but I will go
				 * with the following decision:
				 * 
				 * The enchantment level of the item to upgrade will be set to the
				 * maximum of the current level and the level of the new default
				 * enchantment.
				 */
				if (added.getLevel() > currentLevel) {
					toUpgrade.addEnchantment(
							getByName(added.getType().name()), 
							added.getLevel()
					);
				}
			}
			
			for (ChangedEnchantment changed : changedEnchantments) {
				int currentLevel = toUpgrade.getEnchantmentLevel(
						getByName(changed.type.name())
				);
				
				if (changed.newLevel > changed.oldLevel) {
					
					// Make the same decision as for adding a new enchantment
					if (changed.newLevel > currentLevel) {
						toUpgrade.addEnchantment(
								getByName(changed.type.name()), 
								changed.newLevel
						);
					}
				} else {
					// Decrease the current enchantment level
					int newLevel = currentLevel + changed.newLevel - changed.oldLevel;
					if (newLevel > 0) {
						toUpgrade.addEnchantment(getByName(changed.type.name()), newLevel);
					} else {
						toUpgrade.removeEnchantment(getByName(changed.type.name()));
					}
				}
			}
		}
	}
	
	private CustomItem getItemByName(String name) {
		for (CustomItem item : items) {
			if (item.getName().equals(name)) {
				return item;
			}
		}
		
		return null;
	}
	
	private static enum UpdateAction {
		
		DO_NOTHING,
		UPDATE_LAST_MODIFIED,
		INITIALIZE,
		UPGRADE,
		DESTROY
	}
}
