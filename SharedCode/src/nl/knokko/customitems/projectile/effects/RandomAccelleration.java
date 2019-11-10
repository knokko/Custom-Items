package nl.knokko.customitems.projectile.effects;

import nl.knokko.util.bits.BitInput;

public class RandomAccelleration extends ProjectileAccelleration {
	
	public static RandomAccelleration load1(BitInput input) {
		return new RandomAccelleration(input.readFloat(), input.readFloat());
	}

	public RandomAccelleration(float minAccelleration, float maxAccelleration) {
		super(minAccelleration, maxAccelleration);
	}

	@Override
	protected byte getEncoding() {
		return ENCODING_RANDOM_ACCELLERATION_1;
	}
}
