package nl.knokko.customitems.editor.set;

import nl.knokko.customitems.item.CustomItemType;

public interface ItemDamageClaim {
	
	short getItemDamage();
	
	CustomItemType getItemType();
	
	String getResourcePath();
}
