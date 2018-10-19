package nl.knokko.customitems.editor.menu.edit.recipe.ingredient;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;

public class IngredientView extends GuiMenu {
	
	private final IngredientComponent component;

	public IngredientView(IngredientComponent component) {
		this.component = component;
	}

	@Override
	protected void addComponents() {
		addComponent(new TextButton("Back", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(component.getMenu());
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		addComponent(new TextButton("Change", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseIngredient(component.getMenu(), (Ingredient newIngredient) -> {
				IngredientView.this.component.setIngredient(newIngredient);
			}));
		}), 0.1f, 0.3f, 0.25f, 0.4f);
		String[] info = component.getIngredient().getInfo();
		for (int index = 0; index < info.length; index++)
			addComponent(new TextComponent(info[index], EditProps.LABEL), 0.4f, 0.8f - index * 0.15f, 0.7f, 0.9f - index * 0.15f);
	}
}