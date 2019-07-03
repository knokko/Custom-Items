package nl.knokko.customitems.damage;

import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import static nl.knokko.customitems.damage.DamageSource.AMOUNT;

public class DamageResistances {
	
	protected final short[] resistanceMap;
	
	public DamageResistances() {
		this.resistanceMap = new short[AMOUNT];
	}
	
	public DamageResistances(BitInput input) {
		this();
		for (int index = 0; index < AMOUNT; index++) {
			if (input.readBoolean()) {
				resistanceMap[index] = input.readShort();
			}
		}
	}
	
	public DamageResistances(short[] resistanceMap) {
		if (resistanceMap.length > AMOUNT) {
			throw new IllegalArgumentException("resistanceMap is too large (" + resistanceMap.length + ")");
		}
		this.resistanceMap = new short[AMOUNT];
		System.arraycopy(resistanceMap, 0, this.resistanceMap, 0, AMOUNT);
	}
	
	@Override
	public DamageResistances clone() {
		DamageResistances clone = new DamageResistances();
		System.arraycopy(resistanceMap, 0, clone.resistanceMap, 0, AMOUNT);
		return clone;
	}
	
	public short getResistance(DamageSource source) {
		return resistanceMap[source.ordinal()];
	}
	
	public void setResistance(DamageSource source, short value) {
		resistanceMap[source.ordinal()] = value;
	}
	
	public short[] getBackingArray() {
		return resistanceMap;
	}
	
	public void save(BitOutput output) {
		for (short resistance : resistanceMap) {
			if (resistance != 0) {
				output.addBoolean(true);
				output.addShort(resistance);
			} else {
				output.addBoolean(false);
			}
		}
	}
}