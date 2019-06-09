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
import nl.knokko.customitems.editor.menu.edit.recipe.ingredient.ChooseIngredient;
import nl.knokko.customitems.editor.menu.edit.recipe.ingredient.IngredientComponent;
import nl.knokko.customitems.editor.menu.edit.recipe.result.ResultComponent;
import nl.knokko.customitems.editor.set.item.Material;
import nl.knokko.customitems.editor.set.recipe.ShapelessRecipe;
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.customitems.editor.set.recipe.result.SimpleVanillaResult;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.ConditionalTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.texture.GuiTexture;

public class ShapelessRecipeEdit extends GuiMenu {
	
	private final EditMenu menu;
	private final ShapelessRecipe previous;
	
	private final Ingredients ingredients;
	private final ResultComponent result;
	private final DynamicTextComponent errorComponent;

	public ShapelessRecipeEdit(EditMenu menu, ShapelessRecipe previous) {
		this.menu = menu;
		this.previous = previous;
		ingredients = new Ingredients();
		result = new ResultComponent(previous != null ? previous.getResult() : new SimpleVanillaResult(Material.DIAMOND, (byte) 1), this, menu.getSet());
		errorComponent = new DynamicTextComponent("", EditProps.ERROR);
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(menu.getRecipeOverview());
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		addComponent(new ConditionalTextButton("Add ingredient", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseIngredient(this, (Ingredient ingredient) -> {
				ingredients.addIngredient(ingredient);
			}, false, menu.getSet()));
		}, () -> {
			return ingredients.getComponents().size() < 18;
		}), 0.1f, 0.4f, 0.3f, 0.5f);
		addComponent(new ConditionalTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			Collection<SubComponent> components = ingredients.getComponents();
			Ingredient[] ingredients = new Ingredient[components.size() / 2];
			int index = 0;
			for (SubComponent component : components) {
				if (component.getComponent() instanceof IngredientComponent) {
					IngredientComponent ic = (IngredientComponent) component.getComponent();
					ingredients[index++] = ic.getIngredient();
				}
			}
			if (previous != null) {
				String error = menu.getSet().changeShapelessRecipe(previous, ingredients, result.getResult());
				if (error != null)
					errorComponent.setText(error);
				else
					state.getWindow().setMainComponent(menu.getRecipeOverview());
			} else {
				String error = menu.getSet().addShapelessRecipe(ingredients, result.getResult());
				if (error != null)
					errorComponent.setText(error);
				else
					state.getWindow().setMainComponent(menu.getRecipeOverview());
			}
		}, () -> {
			return ingredients.getComponents().size() > 0;
		}), 0.1f, 0.2f, 0.25f, 0.3f);
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 0.975f);
		addComponent(new DynamicTextComponent("Ingredients", EditProps.LABEL), 0.4f, 0.8f, 0.55f, 0.875f);
		addComponent(ingredients, 0.4f, 0.025f, 0.7f, 0.775f);
		addComponent(new DynamicTextComponent("Result", EditProps.LABEL), 0.75f, 0.725f, 0.9f, 0.8f);
		addComponent(result, 0.75f, 0.6f, 0.95f, 0.7f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	private class Ingredients extends GuiMenu {
		
		private GuiTexture deleteBase;
		private GuiTexture deleteHover;

		@Override
		protected void addComponents() {
			deleteBase = state.getWindow().getTextureLoader().loadTexture("nl/knokko/gui/images/icons/delete.png");
			deleteHover = state.getWindow().getTextureLoader().loadTexture("nl/knokko/gui/images/icons/delete_hover.png");
			if (previous != null) {
				Ingredient[] ingredients = previous.getIngredients();
				for (int index = 0; index < ingredients.length; index++)
					addIngredient(ingredients[index], index);
			}
		}
		
		private void addIngredient(Ingredient ingredient, int index) {
			IngredientComponent ic = new IngredientComponent(null, ingredient, ShapelessRecipeEdit.this, menu.getSet());
			addComponent(ic, 0f, 0.89f - index * 0.11f, 0.6f, 1f - index * 0.11f);
			addComponent(new ImageButton(deleteBase, deleteHover, () -> {
				Ingredients.this.removeComponent(ic);
				Ingredients.this.removeComponent(this);
			}), 0.65f, 0.89f - index * 0.11f, 0.95f, 1f - index * 0.11f);
		}
		
		private void addIngredient(Ingredient ingredient) {
			addIngredient(ingredient, getComponents().size() / 2);
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND;
		}
	}
}