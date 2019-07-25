package nl.knokko.customitems.drops;

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
	NPC;
	
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
	
	@Override
	public String toString() {
		return name().replace('_', ' ').toLowerCase();
	}
}
