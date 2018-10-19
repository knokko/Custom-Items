package nl.knokko.customitems.editor.set.recipe.ingredient;

import nl.knokko.util.bits.BitOutput;

public interface Ingredient {
	
	void save(BitOutput output);
	
	byte getID();
	
	boolean conflictsWith(Ingredient other);
}