/*
 * The MIT License
 *
 * Copyright 2018 knokko.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package nl.knokko.customitems.plugin.recipe;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface CustomRecipe {
    
	/**
	 * Register this recipe to Bukkit
	 */
    void register();
    
    /**
     * @return The result of this recipe
     */
    ItemStack getResult();
    
    /**
     * Checks if the materials of the specified ingredients are correct for this recipe.
     * If this is the case, the shouldAccept method will be called and the crafting of the result
     * will be cancelled if the recipe is not entirely correct.
     * This is to prevent players from crafting items with the wrong ingredients.
     * @param ingredients The types of the ingredients, from left to right and up to down
     * @return True if the types of the ingredients are correct for this recipe
     */
    boolean areMaterialsCorrect(Material[] ingredients);
    
    /**
     * Checks if the specified ingredients are sufficient to craft the result of this recipe.
     * @param ingredients The crafting ingredients the player uses, from left to right and up to down
     * @return True if the ingredients are sufficient to craft the result, false otherwise
     */
    boolean shouldAccept(ItemStack[] ingredients);
}