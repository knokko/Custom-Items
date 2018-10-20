package nl.knokko.customitems.editor.set.recipe.result;

import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public abstract class Result {
	
	private final byte amount;
	
	private String[] info;
	
	public Result(byte amount) {
		this.amount = amount;
	}
	
	public Result(BitInput input) {
		amount = (byte) (1 + input.readNumber((byte) 6, false));
	}
	
	protected abstract void saveOwn(BitOutput output);
	
	public abstract byte getID();
	
	protected void initInfo() {
		String[] extraInfo = createInfo();
		info = new String[extraInfo.length + 1];
		System.arraycopy(extraInfo, 0, info, 0, extraInfo.length);
		info[extraInfo.length] = "Amount: " + amount;
	}
	
	protected abstract String[] createInfo();
	
	public abstract String getString();
	
	/**
	 * @param amount The new amount
	 * @return A copy of this result, but with different amount
	 */
	public abstract Result amountClone(byte amount);
	
	@Override
	public String toString() {
		return getString() + " x " + amount;
	}
	
	public String[] getInfo() {
		return info;
	}
	
	public byte getAmount() {
		return amount;
	}
	
	public void save(BitOutput output) {
		output.addByte(getID());
		output.addNumber(amount - 1, (byte) 6, false);
		saveOwn(output);
	}
}