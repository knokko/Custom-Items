package nl.knokko.customitems.drops;

import nl.knokko.customitems.encoding.DropEncoding;
import nl.knokko.customitems.item.ItemSetBase;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class EntityDrop {
	
	public static EntityDrop load(BitInput input, ItemSetBase set) {
		byte encoding = input.readByte();
		if (encoding == DropEncoding.Entity.ENCODING1)
			return load1(input, set);
		else
			throw new IllegalArgumentException("Unknown mob drop encoding: " + encoding);
	}
	
	private static EntityDrop load1(BitInput input, ItemSetBase set) {
		return new EntityDrop(CIEntityType.getByOrdinal(input.readInt()), input.readString(), Drop.load1(input, set));
	}
	
	private CIEntityType entity;
	private String requiredName;
	
	private Drop drop;
	
	public EntityDrop(CIEntityType entity, String requiredName, Drop drop) {
		this.entity = entity;
		this.requiredName = requiredName;
		this.drop = drop;
	}
	
	@Override
	public String toString() {
		return drop + " for " + entity + (requiredName == null ? "" : " named " + requiredName);
	}
	
	public void save(BitOutput output) {
		save1(output);
	}
	
	protected void save1(BitOutput output) {
		output.addByte(DropEncoding.Entity.ENCODING1);
		output.addInt(entity.ordinal());
		output.addString(requiredName);
		drop.save1(output);
	}
	
	public CIEntityType getEntityType() {
		return entity;
	}
	
	public String getRequiredName() {
		return requiredName;
	}
	
	public Drop getDrop() {
		return drop;
	}
	
	public void setEntityType(CIEntityType newType) {
		entity = newType;
	}
	
	public void setRequiredName(String newReqName) {
		requiredName = newReqName;
	}
	
	public void setDrop(Drop newDrop) {
		drop = newDrop;
	}
}
