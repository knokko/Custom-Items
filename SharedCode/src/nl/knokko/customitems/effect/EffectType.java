package nl.knokko.customitems.effect;

import static nl.knokko.customitems.MCVersions.*;

import nl.knokko.customitems.NameHelper;

public enum EffectType {
	
	SPEED,
	SLOW,
	FAST_DIGGING,
	SLOW_DIGGING,
	INCREASE_DAMAGE,
	HEAL,
	HARM,
	JUMP,
	CONFUSION,
	REGENERATION,
	DAMAGE_RESISTANCE,
	FIRE_RESISTANCE,
	WATER_BREATHING,
	INVISIBILITY,
	BLINDNESS,
	NIGHT_VISION,
	HUNGER,
	WEAKNESS,
	POISON,
	WITHER,
	HEALTH_BOOST,
	ABSORPTION,
	SATURATION,
	GLOWING,
	LEVITATION,
	LUCK,
	UNLUCK,
	CONDUIT_POWER(VERSION1_13),
	DOLPHINS_GRACE(VERSION1_13),
	SLOW_FALLING(VERSION1_13),
	BAD_OMEN(VERSION1_14),
	HERO_OF_THE_VILLAGE(VERSION1_14);
	  
	public final int version;
	
	EffectType() {
		this(VERSION1_12);
	}
	
	EffectType(int version) {
		this.version = version;
	}
	
	@Override
	public String toString() {
		return NameHelper.getNiceEnumName(name(), version);
	}
}
