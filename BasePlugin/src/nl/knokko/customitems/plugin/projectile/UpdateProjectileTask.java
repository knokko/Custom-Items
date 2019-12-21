package nl.knokko.customitems.plugin.projectile;

import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import nl.knokko.core.plugin.entity.EntityDamageHelper;
import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.core.plugin.world.RaytraceResult;
import nl.knokko.core.plugin.world.Raytracer;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.set.item.CustomItem;

public class UpdateProjectileTask implements Runnable {
	
	private final FlyingProjectile projectile;
	
	private Item coverItem;

	public UpdateProjectileTask(FlyingProjectile projectile) {
		this.projectile = projectile;
	}

	@Override
	public void run() {
		if (coverItem == null && projectile.prototype.cover != null) {
			createCoverItem(projectile.currentPosition);
		}
		
		World world = projectile.world;
		long currentTick = CustomItemsPlugin.getInstance().getData().getCurrentTick();
		
		RaytraceResult ray = Raytracer.raytrace(projectile.currentPosition.toLocation(world), 
				projectile.currentVelocity, projectile.directShooter == null 
				|| currentTick - projectile.launchTick > 20 ? null : projectile.directShooter);
		
		if (ray == null) {

			projectile.currentVelocity.setY(projectile.currentVelocity.getY() - projectile.prototype.gravity);
			
			if (coverItem != null) {
				if (coverItem.isValid()) {
					if (coverItem.getLocation().toVector().distanceSquared(projectile.currentPosition) > 1.0) {
						coverItem.teleport(projectile.currentPosition.toLocation(world));
					}
					fixItemMotion();
				} else {
					coverItem = null;
				}
			}
			
			projectile.currentPosition.add(projectile.currentVelocity);
		} else {
			
			// Move the projectile to the precise impact location before applying its effects
			projectile.currentPosition.multiply(0).add(ray.getImpactLocation().toVector());
			projectile.applyEffects(projectile.prototype.impactEffects);
			
			// If we hit an entity, damage it
			if (ray.getHitEntity() != null && projectile.prototype.damage > 0) {
				EntityDamageHelper.causeFakeProjectileDamage(ray.getHitEntity(), 
						projectile.responsibleShooter, projectile.prototype.damage, 
						projectile.currentPosition.getX(), projectile.currentPosition.getY(), 
						projectile.currentPosition.getZ(), 
						projectile.currentVelocity.getX(), projectile.currentVelocity.getY(), 
						projectile.currentVelocity.getZ());
			}
			
			projectile.destroy();
		}
		
		
	}
	
	void fixItemMotion() {
		if (coverItem != null) {
			coverItem.setVelocity(projectile.currentVelocity);
		}
	}

	void onDestroy() {
		if (coverItem != null) {
			coverItem.remove();
		}
	}
	
	private void createCoverItem(Vector position) {
		
		CIMaterial coverMaterial = CustomItem.getMaterial(projectile.prototype.cover.itemType);
		ItemStack coverStack = ItemHelper.createStack(coverMaterial.name(), 1);
		ItemMeta coverMeta = coverStack.getItemMeta();
		coverMeta.setUnbreakable(true);
		coverStack.setItemMeta(coverMeta);
		coverStack.setDurability(projectile.prototype.cover.itemDamage);
		
		coverItem = projectile.world.dropItem(position.toLocation(projectile.world), coverStack);
		coverItem.setGravity(false);
		coverItem.setInvulnerable(true);
	}
}
