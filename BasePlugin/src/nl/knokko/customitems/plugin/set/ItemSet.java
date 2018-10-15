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

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.encoding.SetEncoding;
import nl.knokko.customitems.item.ItemType;
import nl.knokko.customitems.plugin.recipe.CustomRecipe;
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
		ItemType itemType = ItemType.valueOf(input.readJavaString());
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