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
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;
import nl.knokko.gui.texture.GuiTexture;

public class ShapelessRecipeEdit extends GuiMenu {
	
	private final EditMenu menu;
	private final ShapelessRecipe previous;
	
	private final Ingredients ingredients;
	private final ResultComponent result;
	private final TextComponent errorComponent;

	public ShapelessRecipeEdit(EditMenu menu, ShapelessRecipe previous) {
		this.menu = menu;
		this.previous = previous;
		ingredients = new Ingredients();
		result = new ResultComponent(previous != null ? previous.getResult() : new SimpleVanillaResult(Material.DIAMOND, (byte) 1), this, menu.getSet());
		errorComponent = new TextComponent("", EditProps.ERROR);
	}

	@Override
	protected void addComponents() {
		addComponent(new TextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
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
		addComponent(new TextComponent("Ingredients", EditProps.LABEL), 0.4f, 0.8f, 0.55f, 0.875f);
		addComponent(ingredients, 0.4f, 0.025f, 0.7f, 0.775f);
		addComponent(new TextComponent("Result", EditProps.LABEL), 0.75f, 0.725f, 0.9f, 0.8f);
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
			IngredientComponent ic = new IngredientComponent(ingredient, ShapelessRecipeEdit.this, menu.getSet());
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