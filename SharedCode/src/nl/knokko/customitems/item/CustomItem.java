/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2019 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
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
 *******************************************************************************/
package nl.knokko.customitems.item;
import java.util.List;

import nl.knokko.customitems.effect.PotionEffect;

public abstract class CustomItem {
	
	public static final int UNBREAKABLE_TOOL_DURABILITY = -1;
    
    protected CustomItemType itemType;
    protected short itemDamage;
    
    protected String name;
    protected String displayName;
    protected String[] lore;
    
    protected AttributeModifier[] attributes;
    protected Enchantment[] defaultEnchantments;
    protected boolean[] itemFlags;
    
    protected List<PotionEffect> playerEffects;
    protected List<PotionEffect> targetEffects;
    
    protected String[] commands;
    public CustomItem(CustomItemType itemType, short itemDamage, String name, String displayName, 
    		String[] lore, AttributeModifier[] attributes, Enchantment[] defaultEnchantments, boolean[] itemFlags, 
    		List<PotionEffect> playerEffects, List<PotionEffect> targetEffects, String[] commands){
        this.itemType = itemType;
        this.itemDamage = itemDamage;
        if (name == null) throw new NullPointerException("name");
        this.name = name;
        this.displayName = displayName;
        this.lore = lore;
        this.attributes = attributes;
        this.defaultEnchantments = defaultEnchantments;
        this.itemFlags = itemFlags;
        this.playerEffects = playerEffects;
        this.targetEffects = targetEffects;
        this.commands = commands;
    }
    
    // For validation checks (and some other stuff), it is very important that the equals() method of custom 
    // items only return true when `other` refers to the same object as `this`.
    @Override
    public final boolean equals(Object other) {
    	return this == other;
    }
    
    public String getName(){
        return name;
    }
    
    public String getDisplayName() {
    	return displayName;
    }
    
    public String[] getLore() {
    	return lore;
    }
    
    public CustomItemType getItemType() {
    	return itemType;
    }
    
    public short getItemDamage() {
    	return itemDamage;
    }
    
    public AttributeModifier[] getAttributes() {
    	return attributes;
    }
    
    public Enchantment[] getDefaultEnchantments() {
    	return defaultEnchantments;
    }
    
    public boolean[] getItemFlags() {
    	return itemFlags;
    }
    
    public List<PotionEffect> getPlayerEffects () {
    	return playerEffects;
    }
    
    public List<PotionEffect> getTargetEffects () {
    	return targetEffects;
    }
    
    public String[] getCommands() {
    	return commands;
    }
}