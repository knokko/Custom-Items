package nl.knokko.customitems.plugin.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.customitems.plugin.set.item.CustomItem;
import nl.knokko.customitems.plugin.set.item.CustomWand;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

class PlayerData {

	/**
	 * The number of ticks players remain in the shooting state after right clicking with a wand or gun.
	 * Right clicking again will 'reset' the timer to this number of ticks again.
	 */
	static final int SHOOT_TIME = 10;
	
	public static PlayerData load1(BitInput input, ItemSet set, Logger logger) {
		int numWandsData = input.readInt();
		Map<CustomWand,PlayerWandData> wandsData = new HashMap<>(numWandsData);
		for (int counter = 0; counter < numWandsData; counter++) {
			String itemName = input.readString();
			CustomItem item = set.getCustomItemByName(itemName);
			if (item instanceof CustomWand) {
				CustomWand wand = (CustomWand) item;
				wandsData.put(wand, PlayerWandData.load1(input, wand));
			} else {
				PlayerWandData.discard1(input);
				logger.warning("Discarded someones cooldown for custom item " + itemName + " because the item seems to have been removed.");
			}
		}
		
		return new PlayerData(wandsData);
	}
	
	// Persisting data
	
	/**
	 * For each CustomWand, this map contains data about the current cooldown and charges.
	 * 
	 * If an entry for a given wand is missing, it indicates that the wand is currently not on
	 * cooldown and has all charges available (if the wand uses charges).
	 */
	private final Map<CustomWand,PlayerWandData> wandsData;
	
	// TODO Also handle guns
	
	// Non-persisting data
	
	private long lastShootTick;
	
	PassiveLocation containerSelectionLocation;
	
	public PlayerData() {
		wandsData = new HashMap<>();
		
		init();
	}
	
	private PlayerData(Map<CustomWand,PlayerWandData> wandsData){
		this.wandsData = wandsData;
		
		init();
	}
	
	private void init() {
		lastShootTick = -1;
	}
	
	public void save1(BitOutput output, long currentTick) {
		output.addInt(wandsData.size());
		for (Entry<CustomWand,PlayerWandData> entry : wandsData.entrySet()) {
			output.addString(entry.getKey().getName());
			entry.getValue().save1(output, entry.getKey(), currentTick);
		}
	}
	
	public void setShooting(long currentTick) {
		lastShootTick = currentTick;
	}
	
	/**
	 * Checks if the player is allowed to fire projectiles with the given weapon at this moment. 
	 * 
	 * <p>If so, this method will return true, a cooldown for the given weapon will be set and a charge 
	 * will be removed if the weapon is a wand that requires charges.</p>
	 * 
	 * <p>If not, this method will return false and nothing will be modified.</p>
	 * 
	 * @param weapon The weapon the player is trying to fire projectiles with
	 * @param currentTick The value that would be returned by 
	 * {@code CustomItemsPlugin.getData().getCurrentTick()}.
	 * @return true if the player was allowed to fire projectiles and the cooldown has been set, false
	 * if the player wasn't allowed to fire projectiles
	 */
	public boolean shootIfAllowed(CustomItem weapon, long currentTick) {
		if (weapon instanceof CustomWand) { // TODO Add similar check for CustomGun, once it's added
			CustomWand wand = (CustomWand) weapon;
			PlayerWandData data = wandsData.get(wand);
			
			if (data != null) {
				if (data.canShootNow(wand, currentTick)) {
					data.onShoot(wand, currentTick);
					return true;
				} else {
					return false;
				}
			} else {
				data = new PlayerWandData(wand);
				wandsData.put(wand, data);
				data.onShoot(wand, currentTick);
				return true;
			}
		} else {
			return false;
		}
	}
	
	public boolean isShooting(long currentTick) {
		if (lastShootTick != -1) {
			if (currentTick <= lastShootTick + SHOOT_TIME) {
				return true;
			} else {
				lastShootTick = -1;
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * @return true if this PlayerData doesn't have any active data
	 */
	public boolean clean(long currentTick) {
		
		// Clean the wands data
		Iterator<Entry<CustomWand, PlayerWandData>> iterator = wandsData.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<CustomWand, PlayerWandData> next = iterator.next();
			PlayerWandData data = next.getValue();
			
			/*
			 * If the entry is not on cooldown and not missing any charges, there is no need to keep it
			 * because the absence of an entry also indicates that it's not on cooldown and no charges
			 * are missing.
			 */
			if (!data.isOnCooldown(currentTick) && !data.isMissingCharges(next.getKey(), currentTick)) {
				iterator.remove();
			}
		}
		
		/*
		 * If the player is not shooting and doesn't have any remaining cooldowns or missing wand
		 * charges, there is no reason to keep data about this player because the absence of a player
		 * entry also indicates this.
		 */
		return !isShooting(currentTick) && wandsData.isEmpty() && containerSelectionLocation == null;
	}
}
