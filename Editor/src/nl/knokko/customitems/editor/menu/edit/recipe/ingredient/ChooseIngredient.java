package nl.knokko.customitems.editor.menu.edit.recipe.ingredient;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;

public class ChooseIngredient extends GuiMenu {
	
	private final IngredientListener listener;
	private final GuiComponent returnMenu;

	public ChooseIngredient(GuiComponent returnMenu, IngredientListener listener) {
		this.listener = listener;
		this.returnMenu = returnMenu;
	}

	@Override
	protected void addComponents() {
		addComponent(new TextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_ACTIVE, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.2f, 0.3f, 0.35f, 0.4f);
	}
}