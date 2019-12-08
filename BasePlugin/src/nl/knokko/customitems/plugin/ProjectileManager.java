package nl.knokko.customitems.plugin;

import static java.lang.Math.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.core.plugin.particles.ParticleHelper;
import nl.knokko.core.plugin.world.RaytraceResult;
import nl.knokko.core.plugin.world.Raytracer;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.plugin.set.item.CustomItem;
import nl.knokko.customitems.projectile.CIProjectile;
import nl.knokko.customitems.projectile.effects.ColoredRedstone;
import nl.knokko.customitems.projectile.effects.ExecuteCommand;
import nl.knokko.customitems.projectile.effects.Explosion;
import nl.knokko.customitems.projectile.effects.ProjectileEffect;
import nl.knokko.customitems.projectile.effects.RandomAccelleration;
import nl.knokko.customitems.projectile.effects.SimpleParticles;
import nl.knokko.customitems.projectile.effects.StraightAccelleration;
import nl.knokko.customitems.projectile.effects.SubProjectiles;

public class ProjectileManager implements Listener {
	
	private final Map<Projectile,int[]> taskMap;

	public ProjectileManager() {
		taskMap = new HashMap<>();
	}

	private static final Vector X = new Vector(1, 0, 0);
	private static final Vector Y = new Vector(0, 1, 0);
	
	/**
	 * Lets the given player launch a custom projectile. 
	 * 
	 * <p>This method will NOT check whether the player is
	 * holding the right weapon or check whether the player is allowed to fire that projectile now (that
	 * should have been done before calling this method).</p>
	 * 
	 * <p>This method will make sure that all (special) effects of the projectile will be applied and it will
	 * make sure that the projectile will be cleaned up when it despawns or the server stops.</p>
	 * @param player
	 * @param projectile
	 */
	public void fireProjectile(Player player, CIProjectile projectile) {
		fireProjectile(player, player.getEyeLocation(), player.getLocation().getDirection(), projectile, 
				projectile.maxLifeTime, 0.0);
		
		CustomItemsPlugin plugin = CustomItemsPlugin.getInstance();
		Vector look = player.getLocation().getDirection();
		double baseAngle = 0.0;
		World world = player.getWorld();
		int lifetime = projectile.maxLifeTime;
		
		// For the next computations, I need a unit vector that is not (almost) parallel to `look`
		Vector notParallel;
		
		// If the absolute value of the x-component of `look` is in this range, it's not almost parallel to X
		if (look.getX() > -0.8 && look.getX() < 0.8) {
			notParallel = X;
		} else {
			
			// In this block, the absolute value of the x-component of `look` must be at least 0.8
			// Since also `look` is a unit vector, the absolute value of the y-component can't be close to 1
			notParallel = Y;
		}
		
		// A unit vector perpendicular to `look`
		Vector perpendicular1 = look.getCrossProduct(notParallel).normalize();
		
		// A unit vector perpendicular to both `look` and `perpendicular1`
		Vector perpendicular2 = look.getCrossProduct(perpendicular1);
		
		double randomAngle = random() * 2.0 * PI;
		Vector randomPerpendicular = perpendicular1.clone().multiply(sin(randomAngle)).add(perpendicular2.clone().multiply(cos(randomAngle)));
		
		double launchAngle = toRadians(baseAngle + projectile.minLaunchAngle + random() * (projectile.maxLaunchAngle - projectile.minLaunchAngle));
		Vector launchDirection = look.clone().multiply(cos(launchAngle)).add(randomPerpendicular.clone().multiply(sin(launchAngle)));
		
		double launchSpeed = projectile.minLaunchSpeed + random() * (projectile.maxLaunchSpeed - projectile.minLaunchSpeed);
		Vector velocity = launchDirection.multiply(launchSpeed);
		
		Vector supposedPosition = player.getEyeLocation().toVector();
		Vector startPosition = supposedPosition.clone();
		
		int[] taskIDs;
		
		Item coverItem;
		
		if (projectile.cover != null) {
			CIMaterial coverMaterial = CustomItem.getMaterial(projectile.cover.itemType);
			ItemStack coverStack = ItemHelper.createStack(coverMaterial.name(), 1);
			ItemMeta coverMeta = coverStack.getItemMeta();
			coverMeta.setUnbreakable(true);
			coverStack.setItemMeta(coverMeta);
			coverStack.setDurability(projectile.cover.itemDamage);
			
			coverItem = player.getWorld().dropItem(player.getEyeLocation(), coverStack);
			coverItem.setGravity(false);
			coverItem.setInvulnerable(true);
			taskIDs = new int[]{ -1, -1, Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
				
				double distance = supposedPosition.distance(coverItem.getLocation().toVector());
				if (distance > 0.1) {
					coverItem.teleport(supposedPosition.toLocation(world));
				}
				
				coverItem.setVelocity(velocity);
				
			}, 0, 1) };
		} else {
			taskIDs = new int[] {-1, -1};
			coverItem = null;
		}
		
		taskIDs[1] = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			ParticleHelper.spawnColoredParticle(supposedPosition.toLocation(world), 1, 0.1, 0.1);
			
			long startTime = System.nanoTime();
			
			RaytraceResult rayTraceResult = Raytracer.raytrace(supposedPosition.toLocation(world), velocity, 
					coverItem, supposedPosition.distanceSquared(startPosition) < 2.0 ? player : null);
			
			if (rayTraceResult != null) {
				if (coverItem != null) {
					coverItem.remove();
				}
				for (int taskID : taskIDs) {
					
					// Remove the if statement when the lifetime task has been added
					if (taskID != -1) {
						Bukkit.getScheduler().cancelTask(taskID);
					}
				}
				if (rayTraceResult.getHitEntity() != null) {
					Bukkit.broadcastMessage("Intersected with " + rayTraceResult.getHitEntity());
				} else {
					Bukkit.broadcastMessage("Intersected with " + rayTraceResult.getImpactLocation().getBlock());
				}
			}
			
			long endTime = System.nanoTime();
			
			// NMS end
			
			System.out.println("Raytracing took " + (endTime - startTime) / 1000 + " us");
			
			/*
			double distance = supposedPosition.distance(rayTracer.getLocation().toVector());
			System.out.println("distance to bat is " + distance);
			if (distance > 0.1) {
				if (supposedPosition.distance(startPosition) > 100.2) {
					Bukkit.broadcastMessage("intersection: supposed distance is " + supposedPosition.distance(startPosition));
					rayTracer.remove();
					if (coverItem != null) {
						coverItem.remove();
					}
					Bukkit.getScheduler().cancelTask(taskIDs[1]);
					if (taskIDs.length > 2) {
						Bukkit.getScheduler().cancelTask(taskIDs[2]);
					}
				} else {
					rayTracer.teleport(supposedPosition.toLocation(world));
				}
			}
			*/
			
			supposedPosition.add(velocity);
			//rayTracer.setVelocity(velocity);
			//Bukkit.broadcastMessage("rayTracer position is " + rayTracer.getLocation().toVector());
			velocity.multiply(0.95 + 0.1 * Math.random());
		}, 0, 1);
	}
	
	private void fireProjectile(Player responsibleShooter, Location launchPosition, Vector look, 
			CIProjectile projectile, int lifetime, double baseAngle) {
		
		/*
		
		EntityType projectileType = EntityType.valueOf(projectile.minecraftType.name());
		
		// All enum values of ProjectileType are of EntityType's who's corresponding classes are projectiles
		@SuppressWarnings("unchecked")
		Class<? extends Projectile> bukkitProjectileClass = (Class<? extends Projectile>) projectileType.getEntityClass();
		
		// For the next computations, I need a unit vector that is not (almost) parallel to `look`
		Vector notParallel;
		
		// If the absolute value of the x-component of `look` is in this range, it's not almost parallel to X
		if (look.getX() > -0.8 && look.getX() < 0.8) {
			notParallel = X;
		} else {
			
			// In this block, the absolute value of the x-component of `look` must be at least 0.8
			// Since also `look` is a unit vector, the absolute value of the y-component can't be close to 1
			notParallel = Y;
		}
		
		// A unit vector perpendicular to `look`
		Vector perpendicular1 = look.getCrossProduct(notParallel).normalize();
		
		// A unit vector perpendicular to both `look` and `perpendicular1`
		Vector perpendicular2 = look.getCrossProduct(perpendicular1);
		
		double randomAngle = random() * 2.0 * PI;
		Vector randomPerpendicular = perpendicular1.clone().multiply(sin(randomAngle)).add(perpendicular2.clone().multiply(cos(randomAngle)));
		
		double launchAngle = toRadians(baseAngle + projectile.minLaunchAngle + random() * (projectile.maxLaunchAngle - projectile.minLaunchAngle));
		Vector launchDirection = look.clone().multiply(cos(launchAngle)).add(randomPerpendicular.clone().multiply(sin(launchAngle)));
		
		double launchSpeed = projectile.minLaunchSpeed + random() * (projectile.maxLaunchSpeed - projectile.minLaunchSpeed);
		Vector launchVelocity = launchDirection.multiply(launchSpeed);
		
		Projectile bukkitProjectile = responsibleShooter.launchProjectile(bukkitProjectileClass, launchVelocity);
		if (projectile.cover != null) {
			
			CIMaterial coverMaterial = CustomItem.getMaterial(projectile.cover.itemType);
			ItemStack coverStack = ItemHelper.createStack(coverMaterial.name(), 1);
			ItemMeta coverMeta = coverStack.getItemMeta();
			coverMeta.setUnbreakable(true);
			coverStack.setItemMeta(coverMeta);
			coverStack.setDurability(projectile.cover.itemDamage);
			
			Item coverItem = bukkitProjectile.getWorld().dropItem(bukkitProjectile.getLocation(), coverStack);
			bukkitProjectile.addPassenger(coverItem);
		}
		
		if (bukkitProjectile instanceof LargeFireball) {
			((LargeFireball) bukkitProjectile).setYield(0f);
		}
		
		bukkitProjectile.setMetadata("CustomProjectileName", new ProjectileMetadata(projectile));
		bukkitProjectile.setMetadata("CustomProjectileLaunchTime", new ProjectileLaunchTimeMeta(
				CustomItemsPlugin.getInstance().getData().getCurrentTick()));
		int[] taskIDs = new int[1 + projectile.inFlightEffects.size()];
		CustomItemsPlugin plugin = CustomItemsPlugin.getInstance();
		taskIDs[0] = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, 
				new RemoveTask(bukkitProjectile), lifetime);
		int taskIndex = 1;
		for (ProjectileEffects effects : projectile.inFlightEffects) {
			taskIDs[taskIndex++] = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, 
					new ApplyEffectsTask(bukkitProjectile, projectile, effects.effects), 
					effects.delay, effects.period);
		}
		
		// Apparently, calling teleport immediately doesn't work so well
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			//bukkitProjectile.teleport(launchPosition);
			
			
		});
		
		taskMap.put(bukkitProjectile, taskIDs);
		
		*/
	}
	
	/**
	 * Destroys all currently flying custom projectiles. This method should be called in the onDisable() of
	 * CustomItemsPlugin, but may be called from additional places as well.
	 */
	public void destroyCustomProjectiles() {
		
		for (Entry<Projectile,int[]> entry : taskMap.entrySet()) {
			entry.getKey().remove();
			for (int taskID : entry.getValue()) {
				Bukkit.getScheduler().cancelTask(taskID);
			}
		}
		
		taskMap.clear();
	}
	
	private boolean isProjectileCover(ItemStack item) {
		return CustomItemsPlugin.getInstance().getSet().getCover(item) != null;
	}
	
	@EventHandler
	public void preventProjectileCoverPickup(EntityPickupItemEvent event) {
		ItemStack item = event.getItem().getItemStack();
		if (isProjectileCover(item)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void preventProjectileCoverPickup(InventoryPickupItemEvent event) {
		ItemStack item = event.getItem().getItemStack();
		if (isProjectileCover(item)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void handleCustomProjectileImpactEffects(ProjectileHitEvent event) {
		List<MetadataValue> metas = event.getEntity().getMetadata("CustomProjectileName");
		for (MetadataValue meta : metas) {
			if (meta.getOwningPlugin() == CustomItemsPlugin.getInstance()) {
				String projectileName = meta.asString();
				
				// Apply the impact effects
				CIProjectile projectile = CustomItemsPlugin.getInstance().getSet().getProjectileByName(projectileName);
				applyEffects(event.getEntity(), projectile, projectile.impactEffects);
				
				// Remove all passengers (the projectile covers)
				List<Entity> passengers = event.getEntity().getPassengers();
				for (Entity passenger : passengers) {
					if (passenger instanceof Item) {
						passenger.remove();
					}
				}
				
				// Cancel all tasks for the projectile
				int[] taskIDs = taskMap.remove(event.getEntity());
				if (taskIDs != null) {
					for (int taskID : taskIDs) {
						Bukkit.getScheduler().cancelTask(taskID);
					}
				} else {
					Bukkit.getLogger().warning("The projectile tasks for " + projectile.name + " were already cancelled");
				}
			}
		}
	}
	
	@EventHandler
	public void handleCustomProjectileImpactDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Projectile) {
			List<MetadataValue> metas = event.getDamager().getMetadata("CustomProjectileName");
			for (MetadataValue meta : metas) {
				if (meta.getOwningPlugin() == CustomItemsPlugin.getInstance()) {
					String projectileName = meta.asString();
					CIProjectile projectile = CustomItemsPlugin.getInstance().getSet().getProjectileByName(projectileName);
					if (projectile != null) {
						event.setDamage(projectile.damage);
						
						// TODO Set the right damage CAUSE
					}
				}
			}
		}
	}
	
	private class RemoveTask implements Runnable {
		
		final Projectile projectile;
		
		RemoveTask(Projectile projectile){
			this.projectile = projectile;
		}

		@Override
		public void run() {
			projectile.remove();
			int[] taskIDs = taskMap.remove(projectile);
			
			// Avoid null pointer exceptions in case the projectile was removed earlier
			if (taskIDs != null) {
				for (int taskID : taskIDs) {
					Bukkit.getScheduler().cancelTask(taskID);
				}
			}
		}
	}
	
	// Somewhat dirty, but at least this is on a single place
	private void applyEffects(Projectile projectile, CIProjectile customProjectile, 
			Collection<ProjectileEffect> effects) {
		for (ProjectileEffect effect : effects) {
			
			if (effect instanceof ColoredRedstone) {
				
				ColoredRedstone cr = (ColoredRedstone) effect;
				Location center = projectile.getLocation();
				Location next = center.clone();
				Random random = new Random();
				
				for (int counter = 0; counter < cr.amount; counter++) {
					
					double currentRadius = cr.minRadius + random.nextDouble() * (cr.maxRadius - cr.minRadius);
					
					// Set next
					addRandomDirection(random, next, currentRadius);
					
					// Determine the colors
					int currentRed = cr.minRed + random.nextInt(cr.maxRed - cr.minRed + 1);
					int currentGreen = cr.minGreen + random.nextInt(cr.maxGreen - cr.minGreen + 1);
					int currentBlue = cr.minBlue + random.nextInt(cr.maxBlue - cr.minBlue + 1);
					
					// Spawn the actual particle
					ParticleHelper.spawnColoredParticle(next, 
							currentRed / 255.0, currentGreen / 255.0, currentBlue / 255.0);
					
					// Reset next
					next.setX(center.getX());
					next.setY(center.getY());
					next.setZ(center.getZ());
				}
			} else if (effect instanceof ExecuteCommand) {
				
				ExecuteCommand command = (ExecuteCommand) effect;
				CommandSender sender = null;
				switch (command.executor) {
				case CONSOLE: sender = Bukkit.getConsoleSender(); break;
				case PROJECTILE: sender = projectile; break;
				case SHOOTER:
					ProjectileSource shooter = projectile.getShooter();
					if (shooter instanceof CommandSender)
						sender = (CommandSender) shooter;
					else
						Bukkit.getLogger().warning("Custom projectile was shot by a non-command sender");
					break;
				}
				
				if (sender != null) {
					Bukkit.dispatchCommand(sender, command.command);
				}
			} else if (effect instanceof Explosion) {
				
				Explosion explosion = (Explosion) effect;
				Location loc = projectile.getLocation();
				projectile.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), explosion.power, 
						explosion.setFire, explosion.destroyBlocks);
			} else if (effect instanceof RandomAccelleration) {
				
				RandomAccelleration ra = (RandomAccelleration) effect;
				double accelleration = ra.minAccelleration + random() * (ra.maxAccelleration - ra.minAccelleration);
				Location direction = new Location(projectile.getWorld(), 0, 0, 0);
				addRandomDirection(new Random(), direction, accelleration);
				
				Vector oldMotion = projectile.getVelocity();
				oldMotion.add(direction.toVector());
				projectile.setVelocity(oldMotion);
			} else if (effect instanceof SimpleParticles) {
				
				SimpleParticles sp = (SimpleParticles) effect;
				Random random = new Random();
				Location loc = projectile.getLocation();
				Location next = loc.clone();
				Particle particle = Particle.valueOf(sp.particle.name());
				
				for (int counter = 0; counter < sp.amount; counter++) {
					
					// Determine the current distance
					double distance = sp.minRadius + random.nextDouble() * (sp.maxRadius - sp.minRadius);
					
					// Set next
					addRandomDirection(random, next, distance);
					
					// Spawn the actual particle
					projectile.getWorld().spawnParticle(particle, next.getX(), next.getY(), next.getZ(), 1);
					
					// Reset next
					next.setX(loc.getX());
					next.setY(loc.getY());
					next.setZ(loc.getZ());
				}
			} else if (effect instanceof StraightAccelleration) {
				
				StraightAccelleration sa = (StraightAccelleration) effect;
				
				// Obtain the current velocity and the direction
				Vector velocity = projectile.getVelocity();
				Vector direction = projectile.getVelocity().normalize();
				
				// Determine the acceleration and add it to the velocity
				double accelleration = sa.minAccelleration + random() * (sa.maxAccelleration - sa.minAccelleration);
				velocity.add(direction.multiply(accelleration));
				
				// Update the velocity of the projectile
				projectile.setVelocity(velocity);
			} else if (effect instanceof SubProjectiles) {
				
				SubProjectiles sub = (SubProjectiles) effect;
				
				ProjectileSource shooter = projectile.getShooter();
				if (shooter instanceof Player) {
					
					int amount = sub.minAmount + new Random().nextInt(sub.maxAmount - sub.minAmount + 1);
					CustomItemsPlugin plugin = CustomItemsPlugin.getInstance();
					long launchTick = 0;
					List<MetadataValue> metas = projectile.getMetadata("CustomProjectileLaunchTime");
					for (MetadataValue meta : metas) {
						if (meta.getOwningPlugin() == plugin) {
							launchTick = meta.asLong();
						}
					}
					long passedTicks = plugin.getData().getCurrentTick() - launchTick;
					long remaining = customProjectile.maxLifeTime - passedTicks;
					
					if (remaining > 0) {
						for (int counter = 0; counter < amount; counter++) {
							fireProjectile((Player) shooter, projectile.getLocation(), projectile.getVelocity().normalize(), 
									sub.child, sub.useParentLifeTime ? (int) remaining : sub.child.maxLifeTime, sub.angleToParent);
						}
					} else if (remaining < 0) {
						Bukkit.getLogger().warning("Custom projectile " + customProjectile.name + " outlived its lifetime");
					}
				} else {
					Bukkit.getLogger().warning("A custom projectile was launched, but not by a player");
				}
			}
		}
	}
	
	private static void addRandomDirection(Random random, Location toModify, double distance) {
		
		double pitch = 0.5 * PI - random.nextDouble() * PI;
		double yaw = 2.0 * PI * random.nextDouble();
		
		toModify.add(distance * sin(yaw) * cos(pitch), distance * sin(pitch), distance * cos(yaw) * cos(pitch));
	}
	
	private class ApplyEffectsTask implements Runnable {
		
		final Projectile projectile;
		final CIProjectile customProjectile;
		final Collection<ProjectileEffect> effects;
		
		ApplyEffectsTask(Projectile projectile, CIProjectile customProjectile,
				Collection<ProjectileEffect> effects){
			this.projectile = projectile;
			this.customProjectile = customProjectile;
			this.effects = effects;
		}

		@Override
		public void run() {
			applyEffects(projectile, customProjectile, effects);
		}
	}
}
