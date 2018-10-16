package nl.knokko.customitems.editor.set.recipe.ingredient;

import java.util.Collection;

import nl.knokko.customitems.editor.set.CustomItem;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class CustomItemIngredient implements Ingredient {
	
	private final CustomItem item;

	public CustomItemIngredient(CustomItem item) {
		this.item = item;
	}
	
	public CustomItemIngredient(BitInput input, ItemSet set) {
		String name = input.readJavaString();
		Collection<CustomItem> items = set.getItems();
		for (CustomItem item : items) {
			if(item.getName().equals(name)) {
				this.item = item;
				return;
			}
		}
		throw new IllegalArgumentException("There is no custom item with name " + name);
	}
	
	public CustomItem getItem() {
		return item;
	}

	@Override
	public void save(BitOutput output) {
		output.addJavaString(item.getName());
	}

	@Override
	public byte getID() {
		return RecipeEncoding.Ingredient.CUSTOM;
	}
}