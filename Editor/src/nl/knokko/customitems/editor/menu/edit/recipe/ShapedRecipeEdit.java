package nl.knokko.customitems.editor.menu.edit.recipe;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.recipe.ShapedRecipe;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;

public class ShapedRecipeEdit extends GuiMenu {
	
	private static final GuiColor INGREDIENT_BACKGROUND = new SimpleGuiColor(200, 100, 0);
	
	private final EditMenu menu;
	private final ShapedRecipe previous;

	public ShapedRecipeEdit(EditMenu menu, ShapedRecipe previous) {
		this.menu = menu;
		this.previous = previous;
	}

	@Override
	protected void addComponents() {
		addComponent(new TextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_ACTIVE, () -> {
			state.getWindow().setMainComponent(menu.getRecipeOverview());
		}), 0.1f, 0.7f, 0.25f, 0.8f);
	}
	
	private class Ingredients extends GuiMenu {

		@Override
		protected void addComponents() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return INGREDIENT_BACKGROUND;
		}
	}
}