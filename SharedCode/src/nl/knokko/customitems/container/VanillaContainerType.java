package nl.knokko.customitems.container;

import static nl.knokko.customitems.MCVersions.*;

public enum VanillaContainerType {

	CRAFTING_TABLE(VERSION1_12, VERSION1_16),
	FURNACE(VERSION1_12, VERSION1_16),
	ENCHANTING_TABLE(VERSION1_12, VERSION1_16),
	ANVIL(VERSION1_12, VERSION1_16),

	LOOM(VERSION1_14, VERSION1_16),
	BLAST_FURNACE(VERSION1_14, VERSION1_16),
	SMOKER(VERSION1_14, VERSION1_16),
	STONE_CUTTER(VERSION1_14, VERSION1_16),
	GRINDSTONE(VERSION1_14, VERSION1_16);
	
	public final int firstVersion;
	public final int lastVersion;
	
	VanillaContainerType(int firstVersion, int lastVersion) {
		this.firstVersion = firstVersion;
		this.lastVersion = lastVersion;
	}
}
