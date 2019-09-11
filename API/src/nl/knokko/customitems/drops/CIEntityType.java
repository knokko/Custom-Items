package nl.knokko.customitems.drops;

import static nl.knokko.customitems.MCVersions.*;

public enum CIEntityType {
	
	ELDER_GUARDIAN,
	WITHER_SKELETON,
	STRAY,
	HUSK,
	ZOMBIE_VILLAGER,
	SKELETON_HORSE,
	ZOMBIE_HORSE,
	ARMOR_STAND,
	DONKEY,
	MULE,
	EVOKER,
	VEX,
	VINDICATOR,
	ILLUSIONER,
	CREEPER,
	SKELETON,
	SPIDER,
	GIANT,
	ZOMBIE,
	SLIME,
	GHAST,
	PIG_ZOMBIE,
	ENDERMAN,
	CAVE_SPIDER,
	SILVERFISH,
	BLAZE,
	MAGMA_CUBE,
	ENDER_DRAGON,
	WITHER,
	BAT,
	WITCH,
	ENDERMITE,
	GUARDIAN,
	SHULKER,
	PIG,
	SHEEP,
	COW,
	CHICKEN,
	SQUID,
	WOLF,
	MUSHROOM_COW,
	SNOWMAN,
	OCELOT,
	IRON_GOLEM,
	HORSE,
	RABBIT,
	POLAR_BEAR,
	LLAMA,
	PARROT,
	VILLAGER,
	PLAYER,
	NPC,
	TURTLE(VERSION1_13),
	PHANTOM(VERSION1_13),
	COD(VERSION1_13),
	SALMON(VERSION1_13),
	PUFFERFISH(VERSION1_13),
	TROPICAL_FISH(VERSION1_13),
	DROWNED(VERSION1_13),
	DOLPHIN(VERSION1_13),
	CAT(VERSION1_14),
	PANDA(VERSION1_14),
	PILLAGER(VERSION1_14),
	RAVAGER(VERSION1_14),
	TRADER_LLAMA(VERSION1_14),
	WANDERING_TRADER(VERSION1_14),
	FOX(VERSION1_14);
	
	private static final CIEntityType[] ALL_TYPES = values();
	
	public static final int AMOUNT = ALL_TYPES.length;
	
	public static CIEntityType getByOrdinal(int ordinal) {
		return ALL_TYPES[ordinal];
	}
	
	/**
	 * Do NOT use this on players! The NPC case needs to be handled differently!
	 */
	public static CIEntityType fromBukkitEntityType(Enum<?> entityType) {
		try {
			return valueOf(entityType.name());
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
	
	public final int version;
	
	private CIEntityType(int mcVersion) {
		version = mcVersion;
	}
	
	private CIEntityType() {
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
