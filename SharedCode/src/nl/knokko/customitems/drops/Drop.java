package nl.knokko.customitems.drops;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.customitems.recipe.OutputTable.Entry;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ExceptionSupplier;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class Drop {
	
	public static Drop load1(
			BitInput input, 
			BiFunction<String, Byte, Object> createCustomItemResult
	) {
		return new Drop(
				input.readString(), input.readInt(), input.readInt(), input.readInt(), 
				input.readBoolean(), createCustomItemResult
		);
	}
	
	public static Drop load2(
			BitInput input, 
			ExceptionSupplier<Object, UnknownEncodingException> loadResult
	) throws UnknownEncodingException {
		return new Drop(OutputTable.load1(input, loadResult), input.readBoolean());
	}
	
	private final OutputTable dropTable;
	
	private final boolean cancelNormalDrop;
	
	private Drop(
			String itemName, int minDropAmount, int maxDropAmount, int dropChance, 
			boolean cancelNormalDrop, 
			BiFunction<String, Byte, Object> createCustomItemResult) {
		
		int numAmounts = maxDropAmount - minDropAmount + 1;
		int chancePerAmount = dropChance / numAmounts;
		
		// Resolve rounding errors as best as we can
		int lastChance = chancePerAmount + dropChance - numAmounts * chancePerAmount;
		
		this.dropTable = new OutputTable();
		for (byte amount = (byte) minDropAmount; amount < maxDropAmount; amount++) {
			dropTable.getEntries().add(new Entry(createCustomItemResult.apply(itemName, amount), chancePerAmount));
		}
		dropTable.getEntries().add(new Entry(createCustomItemResult.apply(itemName, (byte) maxDropAmount), lastChance));
		
		this.cancelNormalDrop = cancelNormalDrop;
	}
	
	public Drop(OutputTable dropTable, boolean cancelNormalDrop) {
		this.dropTable = dropTable;
		this.cancelNormalDrop = cancelNormalDrop;
	}
	
	@Override
	public String toString() {
		return dropTable.toString();
	}
	
	/*
	public void save1(BitOutput output) {
		output.addString(drop.getName());
		output.addInt(minDropAmount);
		output.addInt(maxDropAmount);
		output.addInt(dropChance);
		output.addBoolean(cancelNormalDrop);
	}*/
	
	public void save2(BitOutput output, Consumer<Object> saveResult) {
		dropTable.save1(output, saveResult);
		output.addBoolean(cancelNormalDrop);
	}
	
	public OutputTable getDropTable() {
		return dropTable;
	}
	
	public boolean cancelNormalDrop() {
		return cancelNormalDrop;
	}
}
