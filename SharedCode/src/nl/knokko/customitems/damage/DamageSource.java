package nl.knokko.customitems.damage;

import static nl.knokko.customitems.MCVersions.*;

import nl.knokko.customitems.NameHelper;

public enum DamageSource {
	
	CONTACT,
	ENTITY_ATTACK,
	ENTITY_SWEEP_ATTACK,
	PROJECTILE,
	SUFFOCATION,
	FALL,
	FIRE,
	FIRE_TICK,
	MELTING,
	LAVA,
	DROWNING,
	BLOCK_EXPLOSION,
	ENTITY_EXPLOSION,
	VOID,
	LIGHTNING,
	SUICIDE,
	STARVATION,
	POISON,
	MAGIC,
	WITHER,
	FALLING_BLOCK,
	THORNS,
	DRAGON_BREATH,
	CUSTOM,
	FLY_INTO_WALL,
	HOT_FLOOR,
	CRAMMING,
	DRYOUT(VERSION1_13);
	
	public static final int AMOUNT_14 = values().length;
	
	// This is dirty, but correct as only 1 new damage source was added in mc 1.13 and no new in mc 1.14
	public static final int AMOUNT_12 = AMOUNT_14 - 1;
	
	public final int version;
	
	private DamageSource(int mcVersion) {
		version = mcVersion;
	}
	
	private DamageSource() {
		this(VERSION1_12);
	}
	
	@Override
	public String toString() {
		return NameHelper.getNiceEnumName(name(), version);
	}
}