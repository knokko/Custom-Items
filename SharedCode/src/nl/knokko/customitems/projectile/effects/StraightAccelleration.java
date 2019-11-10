package nl.knokko.customitems.projectile.effects;

import nl.knokko.util.bits.BitInput;

public class StraightAccelleration extends ProjectileAccelleration {
	
	public static StraightAccelleration load1(BitInput input) {
		return new StraightAccelleration(input.readFloat(), input.readFloat());
	}
	
	public StraightAccelleration(float minAccelleration, float maxAccelleration) {
		super(minAccelleration, maxAccelleration);
	}

	@Override
	protected byte getEncoding() {
		return ENCODING_STRAIGHT_ACCELLERATION_1;
	}
}
