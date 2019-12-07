package nl.knokko.customitems.plugin.set;

import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.plugin.set.item.CustomItem;
import nl.knokko.customitems.projectile.ProjectileCover;
import nl.knokko.util.bits.BitInput;

class PluginProjectileCover extends ProjectileCover implements ItemDamageClaim {
	
	private final CIMaterial material;

	public PluginProjectileCover(BitInput input) {
		super(input);
		this.material = CustomItem.getMaterial(itemType);
	}

	@Override
	public CIMaterial getMaterial() {
		return material;
	}

	@Override
	public short getItemDamage() {
		return itemDamage;
	}
}
