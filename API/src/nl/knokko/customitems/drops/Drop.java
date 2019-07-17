package nl.knokko.customitems.drops;

import java.util.Random;

import nl.knokko.customitems.item.CustomItem;
import nl.knokko.customitems.item.ItemSetBase;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class Drop {
	
	public static Drop load1(BitInput input, ItemSetBase set) {
		String dropName = input.readString();
		CustomItem drop = set.getCustomItemByName(dropName);
		if (drop == null) {
			throw new IllegalArgumentException("There is no custom item with name '" + dropName + "'");
		}
		return new Drop(drop, input.readInt(), input.readInt(), input.readInt(), input.readBoolean());
	}
	
	private final CustomItem drop;
	
	private final int minDropAmount;
	private final int maxDropAmount;
	
	private final int dropChance;
	
	private final boolean cancelNormalDrop;
	
	public Drop(CustomItem drop, int minDropAmount, int maxDropAmount, int dropChance, boolean cancelNormalDrop) {
		this.drop = drop;
		this.minDropAmount = minDropAmount;
		this.maxDropAmount = maxDropAmount;
		this.dropChance = dropChance;
		this.cancelNormalDrop = cancelNormalDrop;
	}
	
	public void save1(BitOutput output) {
		output.addString(drop.getName());
		output.addInt(minDropAmount);
		output.addInt(maxDropAmount);
		output.addInt(dropChance);
		output.addBoolean(cancelNormalDrop);
	}
	
	public CustomItem getItemToDrop() {
		return drop;
	}
	
	public int getMinDropAmount() {
		return minDropAmount;
	}
	
	public int getMaxDropAmount() {
		return maxDropAmount;
	}
	
	public int getDropChance() {
		return dropChance;
	}
	
	public boolean cancelNormalDrop() {
		return cancelNormalDrop;
	}
	
	public int chooseDroppedAmount(Random random) {
		return minDropAmount + random.nextInt(maxDropAmount + 1 - minDropAmount);
	}
	
	public boolean chooseToDrop(Random random) {
		return dropChance > random.nextInt(100);
	}
}
