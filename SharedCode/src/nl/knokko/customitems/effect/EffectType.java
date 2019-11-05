package nl.knokko.customitems.effect;

import static nl.knokko.customitems.MCVersions.*;

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
	BAD_OMEN(VERSION1_14),
	HERO_OF_THE_VILLAGE(VERSION1_14),
	SLOW_FALLING(VERSION1_13);
	  
	public final int version;
	
	EffectType() {
		this(VERSION1_12);
	}
	
	EffectType(int version) {
		this.version = version;
	}
	
	@Override
	public String toString() {
		switch(version) {
		case VERSION1_12: return name().toLowerCase().replace('_', ' ');
		case VERSION1_13: return name().toLowerCase().replace('_', ' ') + " (1.13+)";
		case VERSION1_14: return name().toLowerCase().replace('_', ' ') + " (1.14+)";
		default: throw new Error("Unknown mc version: " + version);
		}
	}
}
