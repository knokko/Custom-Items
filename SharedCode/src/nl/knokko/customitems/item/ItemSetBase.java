package nl.knokko.customitems.item;

import nl.knokko.customitems.projectile.CIProjectile;
import nl.knokko.customitems.projectile.ProjectileCover;

public interface ItemSetBase {
	
	CustomItem getCustomItemByName(String name);
	
	CIProjectile getProjectileByName(String name);
	
	ProjectileCover getProjectileCoverByName(String name);
}
