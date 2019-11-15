package nl.knokko.customitems.projectile;

import java.util.ArrayList;
import java.util.Collection;

import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.item.ItemSetBase;
import nl.knokko.customitems.projectile.effects.ProjectileEffect;
import nl.knokko.customitems.projectile.effects.ProjectileEffects;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class Projectile {
	
	private static final byte ENCODING_1 = 0;
	
	public static Projectile fromBits(BitInput input, ItemSetBase set) {
		byte encoding = input.readByte();
		switch (encoding) {
		case ENCODING_1: return load1(input, set);
		default: throw new IllegalArgumentException("Unknown projectile encoding: " + encoding);
		}
	}
	
	private static Projectile load1(BitInput input, ItemSetBase set) {
		String name = input.readString();
		float damage = input.readFloat();
		float minLaunchAngle = input.readFloat();
		float maxLaunchAngle = input.readFloat();
		float minStartSpeed = input.readFloat();
		float maxStartSpeed = input.readFloat();
		int maxLifeTime = input.readInt();
		
		DamageSource damageSource = DamageSource.valueOf(input.readString());
		ProjectileType minecraftType = ProjectileType.valueOf(input.readString());
		
		int numFlightEffects = input.readByte() & 0xFF;
		Collection<ProjectileEffects> inFlightEffects = new ArrayList<>(numFlightEffects);
		for (int counter = 0; counter < numFlightEffects; counter++)
			inFlightEffects.add(ProjectileEffects.fromBits(input));
		
		int numImpactEffects = input.readByte() & 0xFF;
		Collection<ProjectileEffect> impactEffects = new ArrayList<>(numImpactEffects);
		for (int counter = 0; counter < numImpactEffects; counter++)
			impactEffects.add(ProjectileEffect.fromBits(input));
		
		String coverName = input.readString();
		ProjectileCover cover = coverName == null ? null : set.getProjectileCoverByName(coverName);
		
		return new Projectile(name, damage, minLaunchAngle, maxLaunchAngle, minStartSpeed, maxStartSpeed, 
				maxLifeTime, damageSource, minecraftType, inFlightEffects, impactEffects, cover);
	}
	
	public String name;
	
	public float damage;
	
	/** 
	 * The minimum and maximum angle (in degrees) between the direction the shooter is facing and the
	 * direction the projectile will be launched.
	 */
	public float minLaunchAngle, maxLaunchAngle;
	public float minLaunchSpeed, maxLaunchSpeed;
	
	/** The maximum lifetime of this projectile, in ticks. */
	public int maxLifeTime;
	
	public DamageSource damageSource;
	
	public ProjectileType minecraftType;
	
	public Collection<ProjectileEffects> inFlightEffects;
	// Please note the 's' at the end of ProjectileEffectS above, that is intentional
	public Collection<ProjectileEffect> impactEffects;
	
	public ProjectileCover cover;

	public Projectile(String name, float damage, float minLaunchAngle, float maxLaunchAngle, 
			float minLaunchSpeed, float maxLaunchSpeed, int maxLifeTime,
			DamageSource damageSource, ProjectileType minecraftType, 
			Collection<ProjectileEffects> inFlightEffects, Collection<ProjectileEffect> impactEffects,
			ProjectileCover cover) {
		this.name = name;
		this.damage = damage;
		this.minLaunchAngle = minLaunchAngle;
		this.maxLaunchAngle = maxLaunchAngle;
		this.minLaunchSpeed = minLaunchSpeed;
		this.maxLaunchSpeed = maxLaunchSpeed;
		this.maxLifeTime = maxLifeTime;
		this.damageSource = damageSource;
		this.minecraftType = minecraftType;
		this.inFlightEffects = inFlightEffects;
		this.impactEffects = impactEffects;
		this.cover = cover;
	}

	public void toBits(BitOutput output) {
		output.addByte(ENCODING_1);
		output.addString(name);
		output.addFloats(damage, minLaunchAngle, maxLaunchAngle, minLaunchSpeed, maxLaunchSpeed);
		output.addInt(maxLifeTime);
		output.addString(damageSource.name());
		output.addString(minecraftType.name());
		output.addByte((byte) inFlightEffects.size());
		for (ProjectileEffects effects : inFlightEffects)
			effects.toBits(output);
		output.addByte((byte) impactEffects.size());
		for (ProjectileEffect effect : impactEffects)
			effect.toBits(output);
		output.addString(cover == null ? null : cover.name);
	}
	
	public void afterProjectilesAreLoaded(ItemSetBase set) {
		for (ProjectileEffects effects : inFlightEffects)
			for (ProjectileEffect effect : effects.effects)
				effect.afterProjectilesAreLoaded(set);
		for (ProjectileEffect effect : impactEffects)
			effect.afterProjectilesAreLoaded(set);
	}
}
