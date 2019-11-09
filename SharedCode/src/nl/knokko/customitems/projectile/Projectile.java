package nl.knokko.customitems.projectile;

import java.util.ArrayList;
import java.util.Collection;

import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.projectile.effects.ProjectileEffect;
import nl.knokko.customitems.projectile.effects.ProjectileEffects;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class Projectile {
	
	private static final byte ENCODING_1 = 0;
	
	public static Projectile fromBits(BitInput input) {
		byte encoding = input.readByte();
		switch (encoding) {
		case ENCODING_1: return load1(input);
		default: throw new IllegalArgumentException("Unknown projectile encoding: " + encoding);
		}
	}
	
	private static Projectile load1(BitInput input) {
		float damage = input.readFloat();
		DamageSource damageSource = DamageSource.valueOf(input.readString());
		ProjectileType minecraftType = ProjectileType.valueOf(input.readString());
		
		int numFlightEffects = input.readByte() & 0xFF;
		Collection<ProjectileEffects> inFlightEffects = new ArrayList<>(numFlightEffects);
		for (int counter = 0; counter < numFlightEffects; counter++) {
			inFlightEffects.add(ProjectileEffects.fromBits(input));
		}
		
		int numImpactEffects = input.readByte() & 0xFF;
		Collection<ProjectileEffect> impactEffects = new ArrayList<>(numImpactEffects);
		for (int counter = 0; counter < numImpactEffects; counter++) {
			impactEffects.add(ProjectileEffect.fromBits(input));
		}
		
		return new Projectile(damage, damageSource, minecraftType, inFlightEffects, impactEffects);
	}
	
	public float damage;
	public DamageSource damageSource;
	
	public ProjectileType minecraftType;
	
	public Collection<ProjectileEffects> inFlightEffects;
	// Please note the 's' at the end of ProjectileEffectS above, that is intentional
	public Collection<ProjectileEffect> impactEffects;

	public Projectile(float damage, DamageSource damageSource, ProjectileType minecraftType,
			Collection<ProjectileEffects> inFlightEffects, Collection<ProjectileEffect> impactEffects) {
		this.damage = damage;
		this.damageSource = damageSource;
		this.minecraftType = minecraftType;
		this.inFlightEffects = inFlightEffects;
		this.impactEffects = impactEffects;
	}

	public void toBits(BitOutput output) {
		output.addByte(ENCODING_1);
		output.addFloat(damage);
		output.addString(damageSource.name());
		output.addString(minecraftType.name());
		output.addByte((byte) inFlightEffects.size());
		for (ProjectileEffects effects : inFlightEffects) {
			effects.toBits(output);
		}
		output.addByte((byte) impactEffects.size());
		for (ProjectileEffect effect : impactEffects) {
			effect.toBits(output);
		}
	}
}
