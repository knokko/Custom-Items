package nl.knokko.customitems.plugin.data;

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
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.container.slot.CustomSlot;
import nl.knokko.customitems.container.slot.FuelCustomSlot;
import nl.knokko.customitems.container.slot.InputCustomSlot;
import nl.knokko.customitems.container.slot.OutputCustomSlot;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.container.ContainerInfo;
import nl.knokko.customitems.plugin.container.ContainerInstance;
import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.customitems.plugin.set.item.CustomItem;
import nl.knokko.customitems.plugin.set.item.CustomWand;
import nl.knokko.customitems.plugin.util.ItemUtils;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;
import nl.knokko.util.bits.ByteArrayBitInput;
import nl.knokko.util.bits.ByteArrayBitOutput;

public class PluginData {
	
	private static final byte ENCODING_1 = 1;
	private static final byte ENCODING_2 = 2;
	
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
	public static PluginData loadData() {
		File dataFile = getDataFile();
		if (dataFile.exists()) {
			try {
				BitInput input = ByteArrayBitInput.fromFile(dataFile);
				
				byte encoding = input.readByte();
				switch (encoding) {
				case ENCODING_1: return load1(input);
				case ENCODING_2: return load2(input);
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
	
	private static Map<UUID, PlayerData> loadPlayerData1(BitInput input, ItemSet set) {
		int numPlayers = input.readInt();
		Map<UUID,PlayerData> playersMap = new HashMap<>(numPlayers);
		for (int counter = 0; counter < numPlayers; counter++) {
			UUID id = new UUID(input.readLong(), input.readLong());
			PlayerData data = PlayerData.load1(input, set, Bukkit.getLogger());
			playersMap.put(id, data);
		}
		
		return playersMap;
	}
	
	private static PluginData load1(BitInput input) {
		long currentTick = input.readLong();
		CustomItemsPlugin plugin = CustomItemsPlugin.getInstance();
		
		Map<UUID, PlayerData> playersMap = loadPlayerData1(input, plugin.getSet());
		
		// There were no persistent containers in this version
		return new PluginData(currentTick, playersMap, new HashMap<>());
	}
	
	private static PluginData load2(BitInput input) {
		long currentTick = input.readLong();
		CustomItemsPlugin plugin = CustomItemsPlugin.getInstance();
		
		Map<UUID, PlayerData> playersMap = loadPlayerData1(input, plugin.getSet());
		
		int numPersistentContainers = input.readInt();
		Map<ContainerLocation, ContainerInstance> persistentContainers = new HashMap<>(numPersistentContainers);
		
		for (int counter = 0; counter < numPersistentContainers; counter++) {
			
			UUID worldId = new UUID(input.readLong(), input.readLong());
			int x = input.readInt();
			int y = input.readInt();
			int z = input.readInt();
			String typeName = input.readString();
			
			ContainerInfo typeInfo = plugin.getSet().getContainerInfo(typeName);
			
			if (typeInfo != null) {
				ContainerInstance instance = ContainerInstance.load1(input, typeInfo);
				ContainerLocation location = new ContainerLocation(worldId, typeInfo.getContainer(), x, y, z);
				persistentContainers.put(location, instance);
			} else {
				ContainerInstance.discard1(input);
			}
		}
		
		return new PluginData(currentTick, playersMap, persistentContainers);
	}
	
	// Persisting data
	private final Map<UUID,PlayerData> playerData;
	private final Map<ContainerLocation,ContainerInstance> persistentContainers;
	
	private long currentTick;
	
	// Non-persisting data
	private List<Player> shootingPlayers;

	private PluginData() {
		playerData = new HashMap<>();
		persistentContainers = new HashMap<>();
		currentTick = 0;
		
		init();
	}
	
	private PluginData(long currentTick, Map<UUID,PlayerData> playerData,
			Map<ContainerLocation, ContainerInstance> persistentContainers) {
		this.playerData = playerData;
		this.persistentContainers = persistentContainers;
		this.currentTick = currentTick;
		
		init();
	}
	
	private void init() {
		shootingPlayers = new LinkedList<>();
		
		CustomItemsPlugin plugin = CustomItemsPlugin.getInstance();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			update();
		}, 1, 1);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			clean();
		}, 200, 40);
	}
	
	private void update() {
		currentTick++;
		
		updateShooting();
	}
	
	private void updateShooting() {
		ItemSet set = CustomItemsPlugin.getInstance().getSet();
		Iterator<Player> iterator = shootingPlayers.iterator();
		while (iterator.hasNext()) {
			Player current = iterator.next();
			PlayerData data = getPlayerData(current);
			if (data.isShooting(currentTick)) {
				CustomItem mainItem = set.getItem(current.getInventory().getItemInMainHand());
				CustomItem offItem = set.getItem(current.getInventory().getItemInOffHand());
				
				if (data.shootIfAllowed(mainItem, currentTick)) {
					fire(current, data, mainItem, current.getInventory().getItemInMainHand());
				}
				if (data.shootIfAllowed(offItem, currentTick)) {
					fire(current, data, offItem, current.getInventory().getItemInOffHand());
				}
			} else {
				iterator.remove();
			}
		}
	}
	
	private void clean() {
		Iterator<Entry<UUID, PlayerData>> it = playerData.entrySet().iterator();
		while (it.hasNext()) {
			if (it.next().getValue().clean(currentTick)) {
				it.remove();
			}
		}
		cleanEmptyContainers();
	}
	
	private void fire(Player player, PlayerData data, CustomItem weapon, ItemStack weaponStack) {
		if (weapon instanceof CustomWand) {
			CustomWand wand = (CustomWand) weapon;
			
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1f, 1f);
			
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
		output.addByte(ENCODING_2);
		save2(output);
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
			entry.getValue().save1(output, currentTick);
		}
	}
	
	private void cleanEmptyContainers() {
		// Clean up any empty custom containers
		Iterator<Entry<ContainerLocation, ContainerInstance>> entryIterator = persistentContainers.entrySet().iterator();
		entryLoop:
		while (entryIterator.hasNext()) {
			
			Entry<ContainerLocation, ContainerInstance> entry = entryIterator.next();
			ContainerInstance instance = entry.getValue();
			
			// Check if its still burning or still has some crafting progress
			if (instance.getCurrentCraftingProgress() != 0 || instance.isAnySlotBurning()) {
				continue;
			}
			
			// Check if any of its input/output/fuel slots is non-empty
			for (int x = 0; x < 9; x++) {
				for (int y = 0; y < instance.getType().getHeight(); y++) {
					
					CustomSlot slot = instance.getType().getSlot(x, y);
					if (slot instanceof InputCustomSlot) {
						if (!ItemUtils.isEmpty(instance.getInput(((InputCustomSlot) slot).getName()))) {
							continue entryLoop;
						}
					} else if (slot instanceof OutputCustomSlot) {
						if (!ItemUtils.isEmpty(instance.getOutput(((OutputCustomSlot) slot).getName()))) {
							continue entryLoop;
						}
					} else if (slot instanceof FuelCustomSlot) {
						if (!ItemUtils.isEmpty(instance.getFuel(((FuelCustomSlot) slot).getName()))) {
							continue entryLoop;
						}
					}
				}
			}
		}
	}
	
	private void save2(BitOutput output) {
		save1(output);
		
		cleanEmptyContainers();
		output.addInt(persistentContainers.size());
		for (Entry<ContainerLocation, ContainerInstance> entry : persistentContainers.entrySet()) {
			
			// Save container location
			ContainerLocation loc = entry.getKey();
			output.addLong(loc.worldId.getMostSignificantBits());
			output.addLong(loc.worldId.getLeastSignificantBits());
			output.addInts(loc.x, loc.y, loc.z);
			output.addString(loc.type.getName());
			
			// Save container state
			ContainerInstance state = entry.getValue();
			state.save1(output);
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
		getPlayerData(player).setShooting(currentTick);
		if (!shootingPlayers.contains(player)) {
			shootingPlayers.add(player);
		}
	}
	
	private ContainerInfo infoFor(CustomContainer container) {
		return CustomItemsPlugin.getInstance().getSet().getContainerInfo(container);
	}
	
	public ContainerInstance startTempSession(Player player, CustomContainer container) {
		PlayerData pd = getPlayerData(player);
		if (pd.containerSession != null) {
			throw new IllegalStateException("The player " + player.getName() + " is already in a container session");
		}
		
		ContainerInstance session = new ContainerInstance(infoFor(container));
		pd.containerSession = session;
		return session;
	}
	
	public ContainerInstance startPersistentSession(Player player, Location containerLocation,
			CustomContainer container) {
		
		PlayerData pd = getPlayerData(player);
		if (pd.containerSession != null) {
			throw new IllegalStateException("The player " + player.getName() + " is already in a container session");
		}
		
		ContainerInstance session = getPersistentContainerData(container, containerLocation);
		pd.containerSession = session;
		
		return session;
	}
	
	public ContainerInstance getContainerSession(Player player) {
		return getPlayerData(player).containerSession;
	}
	
	public void closeContainerSession(Player player) {
		PlayerData pd = getPlayerData(player);
		if (pd.containerSession == null) {
			throw new IllegalStateException("The player " + player.getName() + " doesn't have an open container session");
		}
		pd.containerSession = null;
	}
	
	private PlayerData getPlayerData(Player player) {
		PlayerData data = playerData.get(player.getUniqueId());
		if (data == null) {
			data = new PlayerData();
			playerData.put(player.getUniqueId(), data);
		}
		return data;
	}
	
	private ContainerInstance getPersistentContainerData(CustomContainer container, 
			Location loc) {
		
		ContainerLocation containerLocation = new ContainerLocation(loc, container);
		ContainerInstance instance = persistentContainers.get(containerLocation);
		if (instance == null) {
			instance = new ContainerInstance(infoFor(container));
			persistentContainers.put(containerLocation, instance);
		}
		
		return instance;
	}
	
	/**
	 * @return The number of ticks passed since the first use of this plug-in of at least version 6.0
	 */
	public long getCurrentTick() {
		return currentTick;
	}
	
	private static class ContainerLocation {
		
		final UUID worldId;
		final int x, y, z;
		
		final CustomContainer type;
		
		ContainerLocation(UUID worldId, CustomContainer type, int x, int y, int z){
			this.worldId = worldId;
			this.type = type;
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		ContainerLocation(Location location, CustomContainer type) {
			this.worldId = location.getWorld().getUID();
			this.x = location.getBlockX();
			this.y = location.getBlockY();
			this.z = location.getBlockZ();
			this.type = type;
		}
		
		@Override
		public int hashCode() {
			return worldId.hashCode() + 7 * x - 17 * y + 43 * z - 71 * type.getName().hashCode();
		}
		
		@Override
		public boolean equals(Object other) {
			if (other instanceof ContainerLocation) {
				ContainerLocation loc = (ContainerLocation) other;
				return loc.worldId.equals(worldId) && 
						loc.type.getName().equals(type.getName()) &&
						loc.x == x && loc.y == y && loc.z == z;
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
