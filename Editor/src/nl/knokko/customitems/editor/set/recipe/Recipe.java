package nl.knokko.customitems.editor.set.recipe;

import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.recipe.ingredient.*;
import nl.knokko.customitems.editor.set.recipe.result.*;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public abstract class Recipe {
	
	protected static Ingredient loadIngredient(BitInput input, ItemSet set) {
		byte encoding = input.readByte();
		if (encoding == RecipeEncoding.Ingredient.NONE)
			return new NoIngredient();
		if (encoding == RecipeEncoding.Ingredient.VANILLA_SIMPLE)
			return new SimpleVanillaIngredient(input);
		if (encoding == RecipeEncoding.Ingredient.VANILLA_DATA)
			return new DataVanillaIngredient(input);
		if (encoding == RecipeEncoding.Ingredient.VANILLA_ADVANCED_1)
			throw new UnsupportedOperationException("Advanced vanilla results are not yet supported");
		if (encoding == RecipeEncoding.Ingredient.CUSTOM)
			return new CustomItemIngredient(input, set);
		throw new IllegalArgumentException("Unknown ingredient encoding: " + encoding);
	}
	
	private static Result loadResult(BitInput input, ItemSet set) {
		byte encoding = input.readByte();
		if (encoding == RecipeEncoding.Result.VANILLA_SIMPLE)
			return new SimpleVanillaResult(input);
		if (encoding == RecipeEncoding.Result.VANILLA_DATA)
			return new DataVanillaResult(input);
		if (encoding == RecipeEncoding.Result.VANILLA_ADVANCED_1)
			throw new UnsupportedOperationException("Advanced vanilla results are not yet supported");
		if (encoding == RecipeEncoding.Result.CUSTOM)
			return new CustomItemResult(input, set);
		throw new IllegalArgumentException("Unknown result encoding: " + encoding);
	}
	
	protected Result result;
	protected String id;
	
	public Recipe() {
		id = "id...";
		result = null;
	}
	
	public Recipe(BitInput input, ItemSet set) {
		id = input.readJavaString();
		result = loadResult(input, set);
	}
	
	public final void save(BitOutput output) {
		output.addJavaString(id);
		output.addByte(result.getID());
		result.save(output);
		saveOwn(output);
	}
	
	protected abstract void saveOwn(BitOutput output);
	
	public Result getResult() {
		return result;
	}
	
	public void setResult(Result newResult) {
		result = newResult;
	}
	
	public String getID() {
		return id;
	}
	
	public void setID(String newID) {
		id = newID;
	}
}