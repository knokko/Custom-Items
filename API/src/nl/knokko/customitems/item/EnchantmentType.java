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

public enum EnchantmentType {
	
	PROTECTION_ENVIRONMENTAL(0),
	PROTECTION_FIRE(1),
	PROTECTION_FALL(2),
	PROTECTION_EXPLOSIONS(3),
	PROTECTION_PROJECTILE(4),
	OXYGEN(5),
	WATER_WORKER(6),
	MENDING(70),
	THORNS(7),
	VANISHING_CURSE(71),
	DEPTH_STRIDER(8),
	FROST_WALKER(9),
	BINDING_CURSE(10),
	DAMAGE_ALL(16),
	DAMAGE_UNDEAD(17),
	DAMAGE_ARTHROPODS(18),
	KNOCKBACK(19),
	FIRE_ASPECT(20),
	LOOT_BONUS_MOBS(21),
	SWEEPING_EDGE(22),
	DIG_SPEED(32),
	SILK_TOUCH(33),
	DURABILITY(34),
	LOOT_BONUS_BLOCKS(35),
	ARROW_DAMAGE(48),
	ARROW_KNOCKBACK(49),
	ARROW_FIRE(50),
	ARROW_INFINITE(51),
	LUCK(61),
	LURE(62);
	
	private final String niceName;
	
	private final int numericID;
	
	private EnchantmentType(int numericID) {
		niceName = name().toLowerCase().replace('_', ' ');
		this.numericID = numericID;
	}
	
	public String getName() {
		return niceName;
	}
	
	public int getNumericID() {
		return numericID;
	}
}
