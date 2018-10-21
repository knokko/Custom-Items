package nl.knokko.customitems.plugin.recipe.ingredient;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DataVanillaIngredient implements Ingredient {
	
	public DataVanillaIngredient(Material type, byte data) {
		this.type = type;
		this.data = data;
	}
	
	private final Material type;
	private final byte data;
	
	@Override
	@SuppressWarnings("deprecation")
	public boolean accept(ItemStack item) {
		return item.getType() == type && item.getData().getData() == data;
	}

	@Override
	public Material getType() {
		return type;
	}

	@Override
	public short getData() {
		return Short.MAX_VALUE;
	}
}