package nl.knokko.customitems.editor.set.projectile;

import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.projectile.cover.ProjectileCover;
import nl.knokko.customitems.projectile.Projectile;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class EditorProjectile {
	
	private static final byte ENCODING_1 = 0;
	
	public static EditorProjectile fromBits(BitInput input, ItemSet set) {
		byte encoding = input.readByte();
		switch (encoding) {
		case ENCODING_1: return load1(input, set);
		default: throw new IllegalArgumentException("Unknown editor projectile encoding: " + encoding);
		}
	}
	
	private static EditorProjectile load1(BitInput input, ItemSet set) {
		Projectile projectile = Projectile.fromBits(input);
		String coverName = input.readString();
		ProjectileCover cover = set.getProjectileCoverByName(coverName);
		if (cover == null) {
			throw new IllegalArgumentException("There is no projectile cover with name " + coverName);
		}
		return new EditorProjectile(projectile, cover);
	}
	
	public Projectile projectile;
	
	/** If cover is null, it indicates that the projectile doesn't have a cover. */
	public ProjectileCover cover;

	public EditorProjectile(Projectile projectile, ProjectileCover cover) {
		this.projectile = projectile;
		this.cover = cover;
	}

	public void toBits(BitOutput output) {
		output.addByte(ENCODING_1);
		projectile.toBits(output);
		output.addString(cover.name);
	}
}
