package nl.knokko.customitems.damage;

import static nl.knokko.customitems.MCVersions.*;

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
	
	public static final int AMOUNT = values().length;
	
	public final int version;
	
	private DamageSource(int mcVersion) {
		version = mcVersion;
	}
	
	private DamageSource() {
		this(VERSION1_12);
	}
	
	@Override
	public String toString() {
		String name = name().replace('_', ' ').toLowerCase();
		switch (version){
		case VERSION1_12: return name;
		case VERSION1_13: return name + " (1.13+)";
		case VERSION1_14: return name + " (1.14+)";
		default: throw new Error("Unknown minecraft version: " + version);
		}
	}
}