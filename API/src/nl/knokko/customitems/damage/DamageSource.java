package nl.knokko.customitems.damage;

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
	CRAMMING;
	
	public static final int AMOUNT = values().length;
	
	@Override
	public String toString() {
		return name().toLowerCase().replace('_', ' ');
	}
}