package nl.knokko.customitems.damage;

public class DamageResistances {
	
	protected final short[] resistanceMap;
	
	public DamageResistances() {
		this.resistanceMap = new short[DamageSource.AMOUNT];
	}
	
	public DamageResistances(short[] resistanceMap) {
		if (resistanceMap.length > DamageSource.AMOUNT) {
			throw new IllegalArgumentException("resistanceMap is too large (" + resistanceMap.length + ")");
		}
		this.resistanceMap = new short[DamageSource.AMOUNT];
		System.arraycopy(resistanceMap, 0, this.resistanceMap, 0, DamageSource.AMOUNT);
	}
	
	@Override
	public DamageResistances clone() {
		DamageResistances clone = new DamageResistances();
		System.arraycopy(resistanceMap, 0, clone.resistanceMap, 0, DamageSource.AMOUNT);
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
}