package nl.knokko.customitems.projectile.effects;

import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class ColoredRedstone extends ProjectileEffect {
	
	public static ColoredRedstone load1(BitInput input) {
		return new ColoredRedstone(
				input.readByte() & 0xFF, input.readByte() & 0xFF, input.readByte() & 0xFf,
				input.readByte() & 0xFF, input.readByte() & 0xFF, input.readByte() & 0xFf,
				input.readFloat(), input.readFloat(), input.readInt());
	}
	
	/** Must be at least 0 and at most 255 */
	public int minRed, minGreen, minBlue, maxRed, maxGreen, maxBlue;
	
	public float minRadius, maxRadius;
	
	public int amount;

	public ColoredRedstone(int minRed, int minGreen, int minBlue, int maxRed, int maxGreen, int maxBlue,
			float minRadius, float maxRadius, int amount) {
		this.minRed = minRed;
		this.minGreen = minGreen;
		this.minBlue = minBlue;
		this.maxRed = maxRed;
		this.maxGreen = maxGreen;
		this.maxBlue = maxBlue;
		this.minRadius = minRadius;
		this.maxRadius = maxRadius;
		this.amount = amount;
	}

	@Override
	public void toBits(BitOutput output) {
		output.addByte(ENCODING_COLORED_REDSTONE_1);
		output.addBytes((byte) minRed, (byte) minGreen, (byte) minBlue, (byte) maxRed, (byte) maxGreen, (byte) maxBlue);
		output.addFloats(minRadius, maxRadius);
		output.addInt(amount);
	}
}
