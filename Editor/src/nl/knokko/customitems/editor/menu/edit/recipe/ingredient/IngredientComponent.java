package nl.knokko.customitems.editor.menu.edit.recipe.ingredient;

import java.awt.Color;

import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.NoIngredient;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.util.TextBuilder.Properties;

public class IngredientComponent extends TextButton {
	
	public static final Properties PROPS = Properties.createLabel(Color.BLACK, new Color(255, 150, 0), 512, 128);
	public static final Properties HOVER_PROPS = Properties.createLabel(Color.BLUE, new Color(255, 200, 0), 512, 128);
	
	private Ingredient current;
	private final GuiComponent menu;

	public IngredientComponent(Ingredient original, GuiComponent menu, ItemSet set) {
		super(original.toString(), PROPS, HOVER_PROPS, null);
		this.clickAction = () -> {
			state.getWindow().setMainComponent(new IngredientView(this, set));
		};
		current = original;
		this.menu = menu;
	}
	
	public IngredientComponent(GuiComponent menu, ItemSet set) {
		this(new NoIngredient(), menu, set);
	}
	
	public void setIngredient(Ingredient ingredient) {
		current = ingredient;
		setText(current.toString());
	}
	
	public Ingredient getIngredient() {
		return current;
	}
	
	public GuiComponent getMenu() {
		return menu;
	}
}