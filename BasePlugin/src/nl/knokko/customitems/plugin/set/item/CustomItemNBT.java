package nl.knokko.customitems.plugin.set.item;

import java.util.function.Consumer;

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;

import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class CustomItemNBT {
	
	private static final String KEY = "CustomItemsAndTexturesTag";
	
	private static final String NAME = "CustomItemName";
	private static final String EXPORT_TIME = "ExportTime";
	private static final String DURABILITY = "Durability";
	private static final String AUTO_UPDATE = "AutoUpdate";
	private static final String BOOL_REPRESENTATION = "BooleanRepresentation";
	
	/**
	 * This method grants the opportunity to both read from and write to the custom
	 * item nbt of the given Bukkit ItemStack.
	 * 
	 * This method requires that the original item stack is replaced with a new item 
	 * stack because the original item stack will NOT be modified. The third 
	 * parameter is to remind users of that.
	 * 
	 * Both lambda expression parameters will be called before this method returns.
	 * 
	 * @param original The original Bukkit ItemStack 'to modify'
	 * @param useNBT A lambda expression taking the CustomItemNBT as parameter.
	 * Reading from and writing to the custom item nbt should be done in this 
	 * lambda expression.
	 * @param getNewStack A lambda expression taking the new item stack as parameter.
	 * This lambda should be used to replace the old item stack with the new item
	 * modified item stack.
	 */
	public static void readWrite(org.bukkit.inventory.ItemStack original, 
			Consumer<CustomItemNBT> useNBT, 
			Consumer<org.bukkit.inventory.ItemStack> getNewStack) {
		
		CustomItemNBT nbt = new CustomItemNBT(original, true);
		useNBT.accept(nbt);
		getNewStack.accept(nbt.getBukkitStack());
	}
	
	/**
	 * This method grants the opportunity to read the custom item nbt of the given
	 * Bukkit ItemStack.
	 * 
	 * @param bukkitStack The item stack whose custom item nbt is to be read
	 * @param useNBT A lambda expression taking the custom item nbt of the given item
	 * stack as parameter. It will be called before this method returns.
	 */
	public static void readOnly(org.bukkit.inventory.ItemStack bukkitStack, 
			Consumer<CustomItemNBT> useNBT) {
		useNBT.accept(new CustomItemNBT(bukkitStack, false));
	}
	
	private final ItemStack nmsStack;
	private final NBTTagCompound nbt;
	
	private boolean allowWrite;
	
	private CustomItemNBT(org.bukkit.inventory.ItemStack bukkitStack, boolean allowWrite) {
		this.nmsStack = CraftItemStack.asNMSCopy(bukkitStack);
		this.nbt = nmsStack.getTag();
		this.allowWrite = allowWrite;
	}
	
	private org.bukkit.inventory.ItemStack getBukkitStack() {
		nmsStack.setTag(nbt);
		return CraftItemStack.asBukkitCopy(nmsStack);
	}
	
	private NBTTagCompound getOurTag() {
		return nbt.getCompound(KEY);
	}

	/**
	 * Checks if this item has the custom nbt tag for this plug-in. This method will
	 * return true if and only if the item has the tag.
	 */
	public boolean hasOurNBT() {
		return getOurTag() != null;
	}
	
	private void assertOurNBT() {
		if (!hasOurNBT())
			throw new UnsupportedOperationException("This item stack doesn't have our nbt tag");
	}
	
	private NBTTagCompound getOrCreateOurNBT() {
		if (hasOurNBT())
			return getOurTag();
		else {
			assertWrite();
			NBTTagCompound ourNBT = new NBTTagCompound();
			nbt.set(KEY, ourNBT);
			return ourNBT;
		}
	}
	
	private void assertWrite() {
		if (!allowWrite)
			throw new UnsupportedOperationException("This CustomItemNBT is read-only");
	}
	
	/**
	 * @return The name of the custom item represented by this item
	 * @throws UnsupportedOperationException If this item doesn't have custom item nbt
	 */
	public String getName() throws UnsupportedOperationException {
		assertOurNBT();
		
		return getOurTag().getString(NAME);
	}
	
	/**
	 * Gets the time (from System.currentTimeMillis) at which the custom item
	 * represented by this item was exported. If this is not equal to the export time
	 * of the current item set, this indicates that this item stack was created
	 * from an older version of the server item set.
	 * 
	 * @throws UnsupportedOperationException If this item stack doesn't have custom
	 * item nbt
	 */
	public long getExportTime() throws UnsupportedOperationException {
		assertOurNBT();
		
		return getOurTag().getLong(EXPORT_TIME);
	}
	
	/**
	 * Gets the remaining custom durability of this item stack. If no custom
	 * durability was stored in the custom item nbt, this method returns null.
	 * 
	 * @throws UnsupportedOperationException If this item doesn't have custom item nbt
	 */
	public Long getDurability() throws UnsupportedOperationException {
		assertOurNBT();
		
		NBTTagCompound ourTag = getOurTag();
		if (!ourTag.hasKey(DURABILITY))
			return null;
		
		return getOurTag().getLong(DURABILITY);
	}
	
	/**
	 * Changes the remaining custom durability of this item to newDurability.
	 * @param newDurability The new custom durability
	 * 
	 * @throws UnsupportedOperationException If this custom nbt is read-only or of
	 * this item doesn't have custom item nbt
	 */
	public void setDurability(long newDurability) throws UnsupportedOperationException {
		assertWrite();
		assertOurNBT();
		getOurTag().setLong(DURABILITY, newDurability);
	}
	
	/**
	 * Removes the custom durability of this item, making it practically unbreakable.
	 * This method should only be used when its corresponding custom item turned into
	 * a custom item without durability or if it became unbreakable.
	 * 
	 * @throws UnsupportedOperationException If this custom item nbt is read-only or
	 * if this item doesn't have custom item nbt
	 */
	public void removeDurability() throws UnsupportedOperationException {
		assertWrite();
		assertOurNBT();
		getOurTag().remove(DURABILITY);
	}
	
	/**
	 * Changes the entire custom item nbt or initializes it (if the item didn't have
	 * a custom nbt yet).
	 * 
	 * @param name The name of the custom item that is to be represented by this item
	 * @param exportTime The time (from System.currentTimeMillis) at which the item
	 * set of the custom item was exported
	 * @param maxDurability The maximum durability of the custom item, or null if it's
	 * an unbreakable tool or not a tool at all
	 * @param boolRepresentation The boolean representation of the custom item
	 * 
	 * @throws UnsupportedOperationException If this custom item nbt is read-only
	 */
	public void set(String name, long exportTime, Long maxDurability, 
			BooleanRepresentation boolRepresentation) throws UnsupportedOperationException {
		assertWrite();
		NBTTagCompound nbt = getOrCreateOurNBT();
		nbt.setString(NAME, name);
		nbt.setLong(EXPORT_TIME, exportTime);
		nbt.setLong(DURABILITY, maxDurability);
		nbt.setBoolean(AUTO_UPDATE, true);
		nbt.setByteArray(BOOL_REPRESENTATION, boolRepresentation.getAsBytes());
	}
}
