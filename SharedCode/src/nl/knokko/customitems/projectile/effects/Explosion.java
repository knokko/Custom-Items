package nl.knokko.customitems.projectile.effects;

import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class Explosion extends ProjectileEffect {
	
	public static Explosion load1(BitInput input) {
		return new Explosion(input.readFloat(), input.readBoolean(), input.readBoolean());
	}
	
	public float power;
	
	public boolean destroyBlocks;
	public boolean setFire;

	public Explosion(float power, boolean destroyBlocks, boolean setFire) {
		this.power = power;
		this.destroyBlocks = destroyBlocks;
		this.setFire = setFire;
	}

	@Override
	public void toBits(BitOutput output) {
		output.addByte(ENCODING_EXPLOSION_1);
		output.addFloat(power);
		output.addBoolean(destroyBlocks);
		output.addBoolean(setFire);
	}
}
