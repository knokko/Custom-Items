package nl.knokko.customitems.plugin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.item.AttributeModifier.Slot;
import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.customitems.plugin.set.item.CustomItem;

public class EquipmentEffectsManager {

	public static void start() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomItemsPlugin.getInstance(), () -> {
			ItemSet set = CustomItemsPlugin.getInstance().getSet();
			for (World world : Bukkit.getWorlds()) {
				for (LivingEntity living : world.getLivingEntities()) {
					EntityEquipment equipment = living.getEquipment();
					
					CustomItem mainHand = set.getItem(equipment.getItemInMainHand());
					CustomItem offHand = set.getItem(equipment.getItemInOffHand());
					
					CustomItem helmet = set.getItem(equipment.getHelmet());
					CustomItem chestplate = set.getItem(equipment.getChestplate());
					CustomItem leggings = set.getItem(equipment.getLeggings());
					CustomItem boots = set.getItem(equipment.getBoots());
					
					giveEffects(living, Slot.MAINHAND, mainHand);
					giveEffects(living, Slot.OFFHAND, offHand);
					
					giveEffects(living, Slot.HEAD, helmet);
					giveEffects(living, Slot.CHEST, chestplate);
					giveEffects(living, Slot.LEGS, leggings);
					giveEffects(living, Slot.FEET, boots);
				}
			}
		}, 50, 30);
	}

	private static void giveEffects(LivingEntity living, Slot slot, CustomItem item) {
		if (item != null) {
			for (EquippedPotionEffect effect : item.getEquippedEffects()) {
				if (effect.getRequiredSlot() == slot) {
					living.addPotionEffect(new PotionEffect(
							PotionEffectType.getByName(
									effect.getPotionEffect().getEffect().name()
							), 50, effect.getPotionEffect().getLevel() - 1
					));
				}
			}
		}
	}
}
