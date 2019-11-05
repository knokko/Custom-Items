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
    
    public CustomItem(CustomItemType itemType, short itemDamage, String name, String displayName, 
    		String[] lore, AttributeModifier[] attributes, Enchantment[] defaultEnchantments, boolean[] itemFlags, 
    		List<PotionEffect> playerEffects, List<PotionEffect> targetEffects){
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
    }
    
    public final String getNBTTag12() {
		return "{" + getNBTTagContent12() + "}";
	}
	
	public final String getNBTTag14() {
		return "{Damage:" + itemDamage + "," + getNBTTagContent14() + "}";
	}
	
	public final String getEquipmentTag12(int amount) {
		return "{Count:" + amount + ",Damage:" + itemDamage + ",id:" + itemType.getMinecraftName() + ",tag:" + getNBTTag12() + "}";
	}
	
	public final String getEquipmentTag14(int amount) {
		return "{Count:" + amount + ",id:" + itemType.getMinecraftName() + ",tag:" + getNBTTag14() + "}";
	}

	protected String getNBTTagContent12() {
		String content = "Unbreakable:1,display:{" + getDisplayTagContent12() + "},";
		String attributes = "AttributeModifiers:[";
		if (this.attributes.length > 0) {
			/*
			 * From the core plugin source code for the most and least id long most =
			 * modifiers.size() + 1 + slot.hashCode() * name.hashCode(); long least =
			 * modifiers.size() + 1 + slot.hashCode() + name.hashCode(); if (most == 0) most
			 * = -8; if (least == 0) least = 12;
			 */
			int index = 0;
			for (AttributeModifier attribute : this.attributes) {
				String slot = attribute.getSlot().getSlot();
				String name = attribute.getAttribute().getName();
				long most = 1 + index + slot.hashCode() * name.hashCode();
				long least = 1 + index + slot.hashCode() + name.hashCode();
				if (most == 0)
					most = -8;
				if (least == 0)
					least = 12;
				attributes += "{UUIDMost:" + most + ",UUIDLeast:" + least + ",Amount:" + attribute.getValue()
				 + ",Slot:" + slot + ",AttributeName:\"" + name + "\",Operation:" 
				 + attribute.getOperation().getOperation() + ",Name:\"" + name + "\"}";
				if (index != this.attributes.length - 1) {
					attributes += ",";
				}
				index++;
			}
		} else {
			long most = 1 + "dummyslot".hashCode() * "dummy".hashCode();
			long least = 1 + "dummyslot".hashCode() + "dummy".hashCode();
			if (most == 0)
				most = -8;
			if (least == 0)
				least = 12;
			attributes += "{UUIDMost:" + most + ",UUIDLeast:" + least
					+ ",Amount:0.0,Slot:\"dummyslot\",AttributeName:\"dummy\",Operation:0,Name:\"dummy\"}";
		}
		content += attributes + "]";
		int hideFlags = 0;
		int adder = 1;
		for (int flag = 0; flag < itemFlags.length; flag++) {
			if (itemFlags[flag]) {
				hideFlags += adder;
			}
			adder *= 2;
		}
		if (hideFlags != 0) {
			content += ",HideFlags:" + hideFlags;
		}
		
		if (defaultEnchantments.length > 0) {
			String ench = ",ench:[";
			for (int index = 0; index < defaultEnchantments.length; index++) {
				Enchantment current = defaultEnchantments[index];
				if (index != 0) {
					ench += ",";
				}
				ench += "{id:" + current.getType().getNumericID() + ",lvl:" + current.getLevel() + "}";
			}
			content += ench + "]";
		}
		return content;
	}
	
	protected String getNBTTagContent14() {
		String content = "Unbreakable:1,display:{" + getDisplayTagContent14() + "},";
		String attributes = "AttributeModifiers:[";
		if (this.attributes.length > 0) {
			/*
			 * From the core plugin source code for the most and least id long most =
			 * modifiers.size() + 1 + slot.hashCode() * name.hashCode(); long least =
			 * modifiers.size() + 1 + slot.hashCode() + name.hashCode(); if (most == 0) most
			 * = -8; if (least == 0) least = 12;
			 */
			int index = 0;
			for (AttributeModifier attribute : this.attributes) {
				String slot = attribute.getSlot().getSlot();
				String name = attribute.getAttribute().getName();
				long most = 1 + index + slot.hashCode() * name.hashCode();
				long least = 1 + index + slot.hashCode() + name.hashCode();
				if (most == 0)
					most = -8;
				if (least == 0)
					least = 12;
				attributes += "{UUIDMost:" + most + ",UUIDLeast:" + least + ",Amount:" + attribute.getValue()
				 + ",Slot:\"" + slot + "\",AttributeName:\"" + name + "\",Operation:" 
				 + attribute.getOperation().getOperation() + ",Name:\"" + name + "\"}";
				if (index != this.attributes.length - 1) {
					attributes += ",";
				}
				index++;
			}
		} else {
			throw new UnsupportedOperationException("Custom items without attribute modifiers can't be given with command blocks in minecraft 1.14");
		}
		content += attributes + "]";
		int hideFlags = 0;
		int adder = 1;
		for (int flag = 0; flag < itemFlags.length; flag++) {
			if (itemFlags[flag]) {
				hideFlags += adder;
			}
			adder *= 2;
		}
		if (hideFlags != 0) {
			content += ",HideFlags:" + hideFlags;
		}
		
		if (defaultEnchantments.length > 0) {
			String ench = ",Enchantments:[";
			for (int index = 0; index < defaultEnchantments.length; index++) {
				Enchantment current = defaultEnchantments[index];
				if (index != 0) {
					ench += ",";
				}
				ench += "{id:" + current.getType().getMinecraftName() + ",lvl:" + current.getLevel() + "}";
			}
			content += ench + "]";
		}
		return content;
	}

	protected String getDisplayTagContent12() {
		return "Name:\"" + displayName + "\",Lore:[" + getLoreContent12() + "]";
	}
	
	protected String getDisplayTagContent14() {
		return "Name:\"\\\"" + displayName + "\\\"\",Lore:[" + getLoreContent14() + "]";
	}
	
	protected String getLoreContent12() {
		String allLore = "";
		for(int index = 0; index < lore.length; index++) {
			if (index != 0) {
				allLore += ",";
			}
			allLore += "\"" + lore[index] + "\"";
		}
		return allLore;
	}
	
	protected String getLoreContent14() {
		String allLore = "";
		for(int index = 0; index < lore.length; index++) {
			if (index != 0) {
				allLore += ",";
			}
			allLore += "\"\\\"" + lore[index] + "\\\"\"";
		}
		return allLore;
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
}