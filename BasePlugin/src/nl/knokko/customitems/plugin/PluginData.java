package nl.knokko.customitems.plugin;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.customitems.plugin.set.item.CustomItem;
import nl.knokko.customitems.plugin.set.item.CustomWand;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;
import nl.knokko.util.bits.ByteArrayBitInput;
import nl.knokko.util.bits.ByteArrayBitOutput;

public class PluginData {
	
	private static final byte ENCODING_1 = 1;
	
	private static File getDataFile() {
		return new File(CustomItemsPlugin.getInstance().getDataFolder() + "/gamedata.bin");
	}
	
	/**
	 * Attempts to read the plugin data that was saved previously. If the data can be found, it will be
	 * loaded and a PluginData instance with the loaded data will be returned.
	 * If no previously saved data was found, a new empty PluginData instance will be returned.
	 * 
	 * This method should be called exactly once in the onEnable() of CustomItemsPlugin.
	 * @return A new PluginData or the previously saved PluginData
	 */
	static PluginData loadData() {
		File dataFile = getDataFile();
		if (dataFile.exists()) {
			try {
				BitInput input = ByteArrayBitInput.fromFile(dataFile);
				
				byte encoding = input.readByte();
				switch (encoding) {
				case ENCODING_1: return load1(input);
				default: throw new IllegalArgumentException("Unknown data encoding: " + encoding);
				}
			} catch (IOException e) {
				Bukkit.getLogger().log(Level.SEVERE, "Failed to open the data file for CustomItems", e);
				Bukkit.getLogger().severe("The current data for CustomItems won't be overwritten when you stop the server.");
				return new CarefulPluginData();
			}
		} else {
			Bukkit.getLogger().warning("Couldn't find the data file for CustomItems. Is this the first time you are using CustomItems with version at least 6.0?");
			return new PluginData();
		}
	}
	
	private static PluginData load1(BitInput input) {
		long currentTick = input.readLong();
		
		int numPlayers = input.readInt();
		Map<UUID,PlayerData> playersMap = new HashMap<>(numPlayers);
		for (int counter = 0; counter < numPlayers; counter++) {
			UUID id = new UUID(input.readLong(), input.readLong());
			PlayerData data = PlayerData.load1(input);
			playersMap.put(id, data);
		}
		
		return new PluginData(currentTick, playersMap);
	}
	
	// Persisting data
	private final Map<UUID,PlayerData> playerData;
	
	private long currentTick;
	
	// Non-persisting data
	private List<Player> shootingPlayers;

	private PluginData() {
		playerData = new HashMap<>();
		currentTick = 0;
		
		init();
	}
	
	private PluginData(long currentTick, Map<UUID,PlayerData> playerData) {
		this.playerData = playerData;
		this.currentTick = currentTick;
		
		init();
	}
	
	private void init() {
		shootingPlayers = new LinkedList<>();
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomItemsPlugin.getInstance(), () -> {
			update();
		}, 1, 1);
	}
	
	private void update() {
		currentTick++;
		
		ItemSet set = CustomItemsPlugin.getInstance().getSet();
		Iterator<Player> iterator = shootingPlayers.iterator();
		while (iterator.hasNext()) {
			Player current = iterator.next();
			PlayerData data = getPlayerData(current);
			if (data.isShooting(currentTick)) {
				CustomItem mainItem = set.getItem(current.getInventory().getItemInMainHand());
				CustomItem offItem = set.getItem(current.getInventory().getItemInOffHand());
				
				if (data.canShootNow(mainItem, currentTick)) {
					fire(current, data, mainItem, current.getInventory().getItemInMainHand());
				}
				if (data.canShootNow(offItem, currentTick)) {
					fire(current, data, offItem, current.getInventory().getItemInOffHand());
				}
			} else {
				iterator.remove();
			}
		}
	}
	
	private void fire(Player player, PlayerData data, CustomItem weapon, ItemStack weaponStack) {
		if (weapon instanceof CustomWand) {
			CustomWand wand = (CustomWand) weapon;
			data.cooldownMap.put(weapon, currentTick + wand.cooldown);
			// TODO Remove a wand charge
			
			for (int counter = 0; counter < wand.amountPerShot; counter++)
				CustomItemsPlugin.getInstance().getProjectileManager().fireProjectile(player, wand.projectile);
		}
		// TODO Add a clause for CustomGun, once it's added
	}
	
	/**
	 * Saves the data such that a call to loadData() will return a PluginData with the same data.
	 * 
	 * This method should be called in the onDisable() of CustomItemsPlugin, but could be called on 
	 * additional moments.
	 */
	public void saveData() {
		ByteArrayBitOutput output = new ByteArrayBitOutput();
		output.addByte(ENCODING_1);
		save1(output);
		try {
			OutputStream fileOutput = Files.newOutputStream(getDataFile().toPath());
			fileOutput.write(output.getBytes());
			fileOutput.flush();
			fileOutput.close();
		} catch (IOException io) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to save the CustomItems data", io);
		}
	}
	
	private void save1(BitOutput output) {
		output.addLong(currentTick);
		
		output.addInt(playerData.size());
		for (Entry<UUID,PlayerData> entry : playerData.entrySet()) {
			output.addLong(entry.getKey().getMostSignificantBits());
			output.addLong(entry.getKey().getLeastSignificantBits());
			entry.getValue().save(output);
		}
	}
	
	/**
	 * Sets the given player in the so-called shooting state for the next 10 ticks (a half second). If the
	 * player is already in the shooting state, nothing will happen. The player will leave the shooting state
	 * if this method is not called again within 10 ticks after this call.
	 * 
	 * @param player The player that wants to start shooting
	 */
	public void setShooting(Player player) {
		getPlayerData(player).lastShootTick = currentTick;
		if (!shootingPlayers.contains(player)) {
			shootingPlayers.add(player);
		}
	}
	
	private PlayerData getPlayerData(Player player) {
		PlayerData data = playerData.get(player.getUniqueId());
		if (data == null) {
			data = new PlayerData();
			playerData.put(player.getUniqueId(), data);
		}
		return data;
	}
	
	/**
	 * @return The number of ticks passed since the first use of this plug-in of at least version 6.0
	 */
	public long getCurrentTick() {
		return currentTick;
	}

	private static class PlayerData {
		
		public static PlayerData load1(BitInput input) {
			int numCooldowns = input.readInt();
			Map<CustomItem,Long> cooldownMap = new HashMap<>(numCooldowns);
			for (int counter = 0; counter < numCooldowns; counter++) {
				String itemName = input.readString();
				long availableTick = input.readLong();
				CustomItem item = CustomItemsPlugin.getInstance().getSet().getCustomItemByName(itemName);
				if (item != null) {
					cooldownMap.put(item, availableTick);
				} else {
					Bukkit.getLogger().warning("Discarded someones cooldown for custom item " + itemName + " because the item seems to have been removed.");
				}
			}
			
			return new PlayerData(cooldownMap);
		}
		
		// Persisting data
		
		/**
		 * For each CustomWand and CustomGun, this map contains the tick at which the cooldown for firing
		 * expires. If no value is contained for a wand or gun, it indicates that it is currently not on
		 * cooldown and can thus immediately fire the next projectile.
		 */
		final Map<CustomItem,Long> cooldownMap;
		
		// Non-persisting data
		
		long lastShootTick;
		
		// TODO Also keep track of the charges
		
		public PlayerData() {
			cooldownMap = new HashMap<>();
			
			init();
		}
		
		PlayerData(Map<CustomItem,Long> cooldownMap){
			this.cooldownMap = cooldownMap;
			
			init();
		}
		
		void init() {
			lastShootTick = -1;
		}
		
		public void save(BitOutput output) {
			save1(output);
		}
		
		void save1(BitOutput output) {
			output.addInt(cooldownMap.size());
			for (Entry<CustomItem,Long> entry : cooldownMap.entrySet()) {
				output.addString(entry.getKey().getName());
				output.addLong(entry.getValue());
			}
		}
		
		public boolean canShootNow(CustomItem weapon, long currentTick) {
			if (weapon instanceof CustomWand) { // TODO Add similar check for CustomGun, once it's added
				Long availableTick = cooldownMap.get(weapon);
				
				// TODO Also add a check for the charges
				if (availableTick != null) {
					if (currentTick >= availableTick) {
						cooldownMap.remove(weapon);
						return true;
					} else {
						return false;
					}
				} else {
					return true;
				}
			} else {
				return false;
			}
		}
		
		public boolean isShooting(long currentTick) {
			if (lastShootTick != -1) {
				if (currentTick <= lastShootTick + 10) {
					return true;
				} else {
					lastShootTick = -1;
					return false;
				}
			} else {
				return false;
			}
		}
	}
	
	private static class CarefulPluginData extends PluginData {
		
		@Override
		public void saveData() {
			File dataFile = getDataFile();
			if (dataFile.exists()) {
				super.saveData();
			} else {
				Bukkit.getLogger().warning("The CustomItems data wasn't saved to protect the original data");
			}
		}
	}
}
