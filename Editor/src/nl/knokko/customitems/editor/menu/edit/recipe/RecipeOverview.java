package nl.knokko.customitems.editor.menu.edit.recipe;

import java.util.Collection;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.recipe.Recipe;
import nl.knokko.customitems.editor.set.recipe.ShapedRecipe;
import nl.knokko.customitems.editor.set.recipe.ShapelessRecipe;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;

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
		addComponent(new TextButton("Back", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(menu);
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		addComponent(new TextButton("Create shaped recipe", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ShapedRecipeEdit(menu, null));
		}), 0.05f, 0.3f, 0.4f, 0.4f);
		addComponent(new TextButton("Create shapeless recipe", EditProps.BUTTON, EditProps.HOVER, () -> {
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
			Collection<Recipe> recipes = menu.getSet().getRecipes();
			int index = 0;
			for (Recipe recipe : recipes) {
				addComponent(new TextComponent(recipe.getResult().getString(), EditProps.LABEL), 0f, 0.9f - index * 0.15f, 0.6f, 1f - index * 0.15f);
				addComponent(new TextButton("Edit", EditProps.BUTTON, EditProps.HOVER, () -> {
					if (recipe instanceof ShapedRecipe)
						state.getWindow().setMainComponent(new ShapedRecipeEdit(menu, (ShapedRecipe) recipe));
					else if (recipe instanceof ShapelessRecipe)
						state.getWindow().setMainComponent(new ShapelessRecipeEdit(menu, (ShapelessRecipe) recipe));
					else
						throw new IllegalStateException("Unknown recipe class: " + recipe.getClass());
				}), 0.65f, 0.9f - index * 0.15f, 0.75f, 1f - index * 0.15f);
				addComponent(new TextButton("Delete", EditProps.QUIT_BASE, EditProps.QUIT_HOVER, () -> {
					menu.getSet().removeRecipe(recipe);
				}), 0.8f, 0.9f - index * 0.15f, 0.95f, 1f - index * 0.15f);
				index++;
			}
		}
	}
}