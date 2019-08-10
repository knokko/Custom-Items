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
package nl.knokko.customitems.editor.menu.edit.recipe;

import java.util.Collection;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.recipe.Recipe;
import nl.knokko.customitems.editor.set.recipe.ShapedRecipe;
import nl.knokko.customitems.editor.set.recipe.ShapelessRecipe;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class RecipeOverview extends GuiMenu {
	
	private final EditMenu menu;
	
	private final RecipeList list;

	public RecipeOverview(EditMenu menu) {
		this.menu = menu;
		list = new RecipeList();
	}
	
	@Override
	public void init() {
		if (didInit) list.refresh();
		super.init();
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Back", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(menu);
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		addComponent(new DynamicTextButton("Create shaped recipe", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ShapedRecipeEdit(menu, null));
		}), 0.05f, 0.3f, 0.4f, 0.4f);
		addComponent(new DynamicTextButton("Create shapeless recipe", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ShapelessRecipeEdit(menu, null));
		}), 0.05f, 0.1f, 0.4f, 0.2f);
		addComponent(list, 0.4f, 0.05f, 1f, 0.95f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	private class RecipeList extends GuiMenu {
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND;
		}

		@Override
		protected void addComponents() {
			refresh();
		}
		
		private void refresh() {
			clearComponents();
			Collection<Recipe> recipes = menu.getSet().getBackingRecipes();
			int index = 0;
			for (Recipe recipe : recipes) {
				addComponent(new DynamicTextComponent(recipe.getResult().getString(), EditProps.LABEL), 0f, 0.9f - index * 0.15f, 0.6f, 1f - index * 0.15f);
				addComponent(new DynamicTextButton("Edit", EditProps.BUTTON, EditProps.HOVER, () -> {
					if (recipe instanceof ShapedRecipe)
						state.getWindow().setMainComponent(new ShapedRecipeEdit(menu, (ShapedRecipe) recipe));
					else if (recipe instanceof ShapelessRecipe)
						state.getWindow().setMainComponent(new ShapelessRecipeEdit(menu, (ShapelessRecipe) recipe));
					else
						throw new IllegalStateException("Unknown recipe class: " + recipe.getClass());
				}), 0.65f, 0.9f - index * 0.15f, 0.75f, 1f - index * 0.15f);
				addComponent(new DynamicTextButton("Delete", EditProps.QUIT_BASE, EditProps.QUIT_HOVER, () -> {
					menu.getSet().removeRecipe(recipe);
					refresh();
				}), 0.8f, 0.9f - index * 0.15f, 0.95f, 1f - index * 0.15f);
				index++;
			}
		}
	}
}