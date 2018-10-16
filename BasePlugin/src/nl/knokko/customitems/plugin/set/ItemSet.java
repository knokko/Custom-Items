package nl.knokko.customitems.plugin.set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.objects.ObjectSet;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.encoding.SetEncoding;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.plugin.recipe.CustomRecipe;
import nl.knokko.customitems.plugin.recipe.ShapedCustomRecipe;
import nl.knokko.customitems.plugin.recipe.ingredient.CustomIngredient;
import nl.knokko.customitems.plugin.recipe.ingredient.DataVanillaIngredient;
import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;
import nl.knokko.customitems.plugin.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.plugin.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.plugin.set.item.CustomItem;
import nl.knokko.util.bits.BitInput;

public class ItemSet {
	
	private static final CustomRecipe[] EMPTY = {};
	
	private final Map<Material, CustomRecipe[]> recipeMap;
	
	private final Map<Material, Short2ObjectMap<CustomItem>> customItemMap;

	private Collection<CustomItem> items;
	
	public ItemSet() {
		recipeMap = new EnumMap<Material, CustomRecipe[]>(Material.class);
		customItemMap = new EnumMap<Material, Short2ObjectMap<CustomItem>>(Material.class);
		items = new ArrayList<CustomItem>(0);
	}
	
	public ItemSet(BitInput input) {
		recipeMap = new EnumMap<Material, CustomRecipe[]>(Material.class);
		customItemMap = new EnumMap<Material, Short2ObjectMap<CustomItem>>(Material.class);
		byte encoding = input.readByte();
		if(encoding == SetEncoding.ENCODING_1)
			load1(input);
		else
			throw new IllegalArgumentException("Unknown encoding: " + encoding);
	}
	
	private void load1(BitInput input) {
		// Items
		int itemSize = input.readInt();
		items = new ArrayList<CustomItem>(itemSize);
		for(int counter = 0; counter < itemSize; counter++)
			register(loadItem(input));
		
		// Recipes
		int recipeAmount = input.readInt();
		for(int counter = 0; counter < recipeAmount; counter++) {
			CustomRecipe recipe = loadRecipe(input);
			recipe.register();
			register(recipe);
		}
	}
	
	private CustomItem loadItem(BitInput input) {
		byte encoding = input.readByte();
        if(encoding == ItemEncoding.ENCODING_SIMPLE_1)
            return loadSimpleItem1(input);
        throw new IllegalArgumentException("Unknown encoding: " + encoding);
	}
	
	private CustomItem loadSimpleItem1(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
        short damage = input.readShort();
        String name = input.readJavaString();
        String displayName = input.readJavaString();
        String[] lore = new String[input.readByte() & 0xFF];
        for(int index = 0; index < lore.length; index++)
            lore[index] = input.readJavaString();
        return new CustomItem(itemType, damage, name, displayName, lore);
	}
	
	private void register(CustomItem item) {
		items.add(item);
		Short2ObjectMap<CustomItem> map = customItemMap.get(item.getMaterial());
		if(map == null) {
			map = new org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.shorts.Short2ObjectAVLTreeMap<>();
			customItemMap.put(item.getMaterial(), map);
		}
		map.put(item.getItemDamage(), item);
	}
	
	private CustomRecipe loadRecipe(BitInput input) {
		byte encoding = input.readByte();
		if(encoding == RecipeEncoding.SHAPED_RECIPE)
			return loadShapedRecipe(input);
		if(encoding == RecipeEncoding.SHAPELESS_RECIPE)
			return loadShapelessRecipe(input);
		throw new IllegalArgumentException("Unknown recipe encoding: " + encoding);
	}
	
	private CustomRecipe loadShapelessRecipe(BitInput input) {
		throw new UnsupportedOperationException("Shapeless recipes are not yet supported");
	}
	
	private CustomRecipe loadShapedRecipe(BitInput input) {
		String id = input.readJavaString();
		ItemStack result = loadResult(input);
		Ingredient[] ingredients = new Ingredient[input.readInt()];
		for (int index = 0; index < ingredients.length; index++)
			ingredients[index] = loadIngredient(input);
		return new ShapedCustomRecipe(id, result, ingredients);
	}
	
	@SuppressWarnings("deprecation")
	private ItemStack loadResult(BitInput input) {
		byte amount = input.readByte();
		byte encoding = input.readByte();
		if(encoding == RecipeEncoding.Result.VANILLA_SIMPLE)
			return new ItemStack(Material.valueOf(input.readJavaString()), amount);
		if(encoding == RecipeEncoding.Result.VANILLA_DATA) {
			ItemStack stack = new ItemStack(Material.valueOf(input.readJavaString()), amount);
			MaterialData data = stack.getData();
			data.setData(input.readByte());
			stack.setData(data);
			return stack;
		}
		if(encoding == RecipeEncoding.Result.VANILLA_ADVANCED_1)
			throw new UnsupportedOperationException("Advanced vanilla results are not yet supported");
		if(encoding == RecipeEncoding.Result.CUSTOM)
			return getItem(input.readJavaString()).create(amount);
		throw new IllegalArgumentException("Unknown result encoding: " + encoding);
	}
	
	private Ingredient loadIngredient(BitInput input) {
		byte encoding = input.readByte();
		if(encoding == RecipeEncoding.Ingredient.NONE)
			return new NoIngredient();
		if(encoding == RecipeEncoding.Ingredient.VANILLA_SIMPLE)
			return new SimpleVanillaIngredient(Material.valueOf(input.readJavaString()));
		if(encoding == RecipeEncoding.Ingredient.VANILLA_DATA)
			return new DataVanillaIngredient(Material.valueOf(input.readJavaString()), input.readByte());
		if(encoding == RecipeEncoding.Ingredient.VANILLA_ADVANCED_1)
			throw new UnsupportedOperationException("Advanced vanilla ingredients are not yet supported.");
		if(encoding == RecipeEncoding.Ingredient.CUSTOM)
			return new CustomIngredient(getItem(input.readJavaString()));
		throw new IllegalArgumentException("Unknown ingredient encoding: " + encoding);
	}
	
	private void register(CustomRecipe recipe) {
		CustomRecipe[] recipes = recipeMap.get(recipe.getResult().getType());
		if(recipes == null)
			recipes = new CustomRecipe[1];
		else
			recipes = Arrays.copyOf(recipes, recipes.length + 1);
		recipes[recipes.length - 1] = recipe;
		recipeMap.put(recipe.getResult().getType(), recipes);
	}
	
	public CustomItem getItem(String name) {
		Set<Entry<Material, Short2ObjectMap<CustomItem>>> entrySet = customItemMap.entrySet();
		for(Entry<Material, Short2ObjectMap<CustomItem>> entry : entrySet) {
			ObjectSet<Entry<Short, CustomItem>> set = entry.getValue().entrySet();
			for(Entry<Short, CustomItem> innerEntry : set) {
				if(innerEntry.getValue().getName().equals(name)) {
					return innerEntry.getValue();
				}
			}
		}
		return null;
	}
	
	public CustomItem getItem(ItemStack item) {
		if(item != null && item.hasItemMeta() && item.getItemMeta().isUnbreakable()) {
			Short2ObjectMap<CustomItem> map = customItemMap.get(item.getType());
			if(map != null) {
				return map.get(item.getDurability());
			}
		}
		return null;
	}
	
	public CustomRecipe[] getRecipes(ItemStack result) {
		CustomRecipe[] recipes = recipeMap.get(result.getType());
		if(recipes == null) return EMPTY;
		boolean[] correctResults = new boolean[recipes.length];
		int length = 0;
		for(int index = 0; index < recipes.length; index++) {
			if(recipes[index].getResult().equals(result)) {
				correctResults[index] = true;
				length++;
			}
		}
		CustomRecipe[] correctRecipes = new CustomRecipe[length];
		int correctIndex = 0;
		for(int index = 0; index < correctResults.length; index++)
			if(correctResults[index])
				correctRecipes[correctIndex++] = recipes[index];
		return correctRecipes;
	}
}