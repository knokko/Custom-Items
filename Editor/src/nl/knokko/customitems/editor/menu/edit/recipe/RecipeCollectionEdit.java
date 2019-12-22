package nl.knokko.customitems.editor.menu.edit.recipe;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.editor.menu.edit.CollectionEdit;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.recipe.Recipe;
import nl.knokko.customitems.editor.set.recipe.ShapedRecipe;
import nl.knokko.customitems.editor.set.recipe.ShapelessRecipe;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class RecipeCollectionEdit extends CollectionEdit<Recipe> {
	
	private final EditMenu menu;

	public RecipeCollectionEdit(EditMenu menu) {
		super(new RecipeActionHandler(menu), menu.getSet().getBackingRecipes());
		this.menu = menu;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Create shaped recipe", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ShapedRecipeEdit(menu, null));
		}), 0.025f, 0.25f, 0.27f, 0.35f);
		addComponent(new DynamicTextButton("Create shapeless recipe", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ShapelessRecipeEdit(menu, null));
		}), 0.025f, 0.1f, 0.29f, 0.2f);
	}
	
	private static class RecipeActionHandler implements ActionHandler<Recipe> {
		
		private final EditMenu menu;
		
		private RecipeActionHandler(EditMenu menu) {
			this.menu = menu;
		}

		@Override
		public void goBack() {
			menu.getState().getWindow().setMainComponent(menu);
		}

		@Override
		public BufferedImage getImage(Recipe item) {
			// It is allowed to return null
			return null;
		}

		@Override
		public String getLabel(Recipe item) {
			return item.getResult().getString();
		}

		@Override
		public GuiComponent createEditMenu(Recipe recipe, GuiComponent returnMenu) {
			if (recipe instanceof ShapedRecipe)
				return new ShapedRecipeEdit(menu, (ShapedRecipe) recipe);
			else if (recipe instanceof ShapelessRecipe)
				return new ShapelessRecipeEdit(menu, (ShapelessRecipe) recipe);
			else
				throw new IllegalStateException("Unknown recipe class: " + recipe.getClass());
		}

		@Override
		public String deleteItem(Recipe itemToDelete) {
			menu.getSet().removeRecipe(itemToDelete);
			
			// Well... there isn't really something that can go wrong when deleting recipes
			return null;
		}
	}
}