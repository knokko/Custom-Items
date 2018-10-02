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

import java.util.logging.Level;

import org.bukkit.Bukkit;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.item.ItemType;
import nl.knokko.util.bits.BitInput;

public class CustomItemLoader {
    
    public static void load(BitInput input) {
    	int amount = input.readInt();
    	for(int counter = 0; counter < amount; counter++) {
    		CustomItem next = loadSingle(input);
    		CustomItems.register(next);
    	}
    	Bukkit.getLogger().log(Level.INFO, "Loaded " + amount + " custom items.");
    }
    
    private static CustomItem loadSingle(BitInput input){
    	byte encoding = input.readByte();
        if(encoding == ItemEncoding.ENCODING_SIMPLE_1){
            return loadSimpleItem1(input);
        }
        throw new IllegalArgumentException("Unknown encoding: " + encoding);
    }
    
    private static CustomItem loadSimpleItem1(BitInput input){
        ItemType itemType = ItemType.valueOf(input.readJavaString());
        short damage = input.readShort();
        String name = input.readJavaString();
        String displayName = input.readJavaString();
        String[] lore = new String[input.readByte() & 0xFF];
        for(int index = 0; index < lore.length; index++){
            lore[index] = input.readJavaString();
        }
        return new CustomItem(itemType, damage, name, displayName, lore);
    }
}