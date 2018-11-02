package nl.knokko.customitems.editor.set.recipe.ingredient;

import java.util.Collection;

import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class CustomItemIngredient implements Ingredient {
	
	private final CustomItem item;
	private String[] info;

	public CustomItemIngredient(CustomItem item) {
		this.item = item;
		determineInfo();
	}
	
	public CustomItemIngredient(BitInput input, ItemSet set) {
		String name = input.readJavaString();
		Collection<CustomItem> items = set.getItems();
		for (CustomItem item : items) {
			if(item.getName().equals(name)) {
				this.item = item;
				determineInfo();
				return;
			}
		}
		throw new IllegalArgumentException("There is no custom item with name " + name);
	}
	
	private void determineInfo() {
		this.info = new String[] {
				"Custom Item: ",
				"Name: " + item.getName()
		};
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

	@Override
	public boolean conflictsWith(Ingredient other) {
		return  other instanceof CustomItemIngredient && ((CustomItemIngredient)other).item == item;
	}
	
	@Override
	public String toString() {
		return item.getName();
	}
	
	@Override
	public String[] getInfo() {
		return info;
	}
}