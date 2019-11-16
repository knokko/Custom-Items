package nl.knokko.customitems.projectile;

import nl.knokko.customitems.NameHelper;

public enum ProjectileType {
	
	SNOWBALL,
	FIREBALL,
	SMALL_FIREBALL,
	DRAGON_FIREBALL,
	ARROW;
	
	@Override
	public String toString() {
		return NameHelper.getNiceEnumName(name());
	}
}
