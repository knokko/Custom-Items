package nl.knokko.customitems.editor.set.projectile;

import java.io.IOException;
import java.util.zip.ZipOutputStream;

import nl.knokko.customitems.editor.set.ItemDamageClaim;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public abstract class ProjectileCover implements ItemDamageClaim {
	
	static final byte ID_SPHERE = 0;
	static final byte ID_CUSTOM = 1;
	
	public static ProjectileCover fromBits(BitInput input, ItemSet set) {
		byte id = input.readByte();
		
		if (id == ID_SPHERE) {
			return new SphereProjectileCover(input, set);
		} else if (id == ID_CUSTOM) {
			return new CustomProjectileCover(input);
		} else {
			throw new IllegalArgumentException("Unknown projectile cover id " + id);
		}
	}
	
	public CustomItemType itemType;
    public short itemDamage;
    
    public String name;
    
    ProjectileCover(CustomItemType type, short itemDamage, String name){
    	this.itemType = type;
    	this.itemDamage = itemDamage;
    	this.name = name;
    }
    
    ProjectileCover(BitInput input){
    	String itemTypeName = input.readString();
    	this.itemType = CustomItemType.valueOf(itemTypeName);
		this.itemDamage = input.readShort();
		
		this.name = input.readString();
    }
    
    @Override
    public CustomItemType getItemType() {
    	return itemType;
    }
    
    @Override
    public short getItemDamage() {
    	return itemDamage;
    }
    
    @Override
    public String getResourcePath() {
    	return "customprojectiles/" + name;
    }
	
	public final void toBits(BitOutput output) {
		output.addByte(getID());
		
		output.addString(itemType.name());
		output.addShort(itemDamage);
		
		output.addString(name);
		
		saveData(output);
	}
	
	protected abstract byte getID();
	
	protected abstract void saveData(BitOutput output);
	
	public abstract void writeModel(ZipOutputStream output) throws IOException;
}
