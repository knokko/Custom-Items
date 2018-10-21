package nl.knokko.customitems.plugin.recipe.ingredient;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ComplexVanillaIngredient implements Ingredient {
	
	public ComplexVanillaIngredient(Material type, byte data, short durability) {
		this.type = type;
		this.data = data;
		this.durability = durability;
	}
	
	private final Material type;
	private final byte data;
	private final short durability;
	
	@Override
	@SuppressWarnings("deprecation")
	public boolean accept(ItemStack item) {
		// TODO Work this out later
		return item.getType() == type && item.getData().getData() == data && item.getDurability() == durability;
	}

	@Override
	public Material getType() {
		return type;
	}

	@Override
	public short getData() {
		return data;
	}
}