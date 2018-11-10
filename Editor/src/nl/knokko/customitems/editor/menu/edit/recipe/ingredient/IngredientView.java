/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2018 knokko
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
package nl.knokko.customitems.editor.menu.edit.recipe.ingredient;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;

public class IngredientView extends GuiMenu {
	
	private final IngredientComponent component;
	private final ItemSet set;

	public IngredientView(IngredientComponent component, ItemSet set) {
		this.component = component;
		this.set = set;
	}

	@Override
	protected void addComponents() {
		addComponent(new TextButton("Back", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(component.getMenu());
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		addComponent(new TextButton("Change", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseIngredient(component.getMenu(), (Ingredient newIngredient) -> {
				IngredientView.this.component.setIngredient(newIngredient);
			}, true, set));
		}), 0.1f, 0.3f, 0.25f, 0.4f);
		String[] info = component.getIngredient().getInfo();
		for (int index = 0; index < info.length; index++)
			addComponent(new TextComponent(info[index], EditProps.LABEL), 0.4f, 0.8f - index * 0.15f, 0.7f, 0.9f - index * 0.15f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}