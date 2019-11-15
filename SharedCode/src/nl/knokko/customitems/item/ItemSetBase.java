package nl.knokko.customitems.item;

import nl.knokko.customitems.projectile.Projectile;
import nl.knokko.customitems.projectile.ProjectileCover;

public interface ItemSetBase {
	
	CustomItem getCustomItemByName(String name);
	
	Projectile getProjectileByName(String name);
	
	ProjectileCover getProjectileCoverByName(String name);
}
