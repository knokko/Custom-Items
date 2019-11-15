package nl.knokko.customitems.projectile.effects;

import nl.knokko.customitems.item.ItemSetBase;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

/**
 * Represents some kind of effect/event that can occur around a projectile on impact or somewhere during its
 * flight. Some effects will be graphical only (like particle effects), but others might actually do something
 * (like explosions).
 * @author knokko
 *
 */
public abstract class ProjectileEffect {
	
	protected static final byte ENCODING_EXPLOSION_1 = 0;
	protected static final byte ENCODING_COLORED_REDSTONE_1 = 1;
	protected static final byte ENCODING_SIMPLE_PARTICLE_1 = 2;
	protected static final byte ENCODING_STRAIGHT_ACCELLERATION_1 = 3;
	protected static final byte ENCODING_RANDOM_ACCELLERATION_1 = 4;
	protected static final byte ENCODING_SUB_PROJECTILE_1 = 5;
	protected static final byte ENCODING_COMMAND_1 = 6;
	
	public static ProjectileEffect fromBits(BitInput input) {
		byte encoding = input.readByte();
		switch (encoding) {
		case ENCODING_EXPLOSION_1: return Explosion.load1(input);
		case ENCODING_COLORED_REDSTONE_1: return ColoredRedstone.load1(input);
		case ENCODING_SIMPLE_PARTICLE_1: return SimpleParticles.load1(input);
		case ENCODING_STRAIGHT_ACCELLERATION_1: return StraightAccelleration.load1(input);
		case ENCODING_RANDOM_ACCELLERATION_1: return RandomAccelleration.load1(input);
		case ENCODING_SUB_PROJECTILE_1: return SubProjectiles.load1(input);
		case ENCODING_COMMAND_1: return ExecuteCommand.load1(input);
		default: throw new IllegalArgumentException("Unknown projectile effect encoding: " + encoding);
		}
	}
	
	public void afterProjectilesAreLoaded(ItemSetBase set) {}

	public abstract void toBits(BitOutput output);
	
	/** 
	 * Checks if this projectile effect has any validation errors. If so, it returns one of those validation
	 * errors. If not, this method returns null.
	 * @return A validation error if there are any, or null if there are none
	 */
	public abstract String validate();
}
