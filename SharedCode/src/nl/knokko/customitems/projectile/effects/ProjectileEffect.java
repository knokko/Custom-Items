package nl.knokko.customitems.projectile.effects;

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
	
	public static ProjectileEffect fromBits(BitInput input) {
		byte encoding = input.readByte();
		switch (encoding) {
		case ENCODING_EXPLOSION_1: return Explosion.load1(input);
		case ENCODING_COLORED_REDSTONE_1: return ColoredRedstone.load1(input);
		case ENCODING_SIMPLE_PARTICLE_1: return SimpleParticles.load1(input);
		default: throw new IllegalArgumentException("Unknown projectile effect encoding: " + encoding);
		}
	}

	public abstract void toBits(BitOutput output);
}
