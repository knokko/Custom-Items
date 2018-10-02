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
package nl.knokko.customitems.plugin.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Bukkit;

import com.google.common.collect.Lists;

import nl.knokko.customitems.item.ItemType;

public class CustomItem extends nl.knokko.customitems.item.CustomItem {
	
	private final Material material;
    
    public CustomItem(ItemType itemType, short itemDamage, String name, String displayName, String[] lore){
        super(itemType, itemDamage, name, displayName, lore);
        material = Material.getMaterial(itemType.name());
    }
    
    public ItemStack create(int amount){
        ItemStack item = new ItemStack(material, amount);
        item.setDurability(itemDamage);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);
        meta.setDisplayName(displayName);
        meta.setLore(Lists.newArrayList(lore));
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        return item;
    }
    
    public boolean is(ItemStack item){
        return item.getItemMeta().isUnbreakable() && item.getType() == material && item.getDurability() == itemDamage;
    }
    
    public Material getMaterial() {
    	return material;
    }
}