package nl.knokko.customitems.editor.menu.edit.recipe.ingredient;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.select.item.SelectCustomItem;
import nl.knokko.customitems.editor.menu.edit.select.item.SelectDataVanillaItem;
import nl.knokko.customitems.editor.menu.edit.select.item.SelectSimpleVanillaItem;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.item.Material;
import nl.knokko.customitems.editor.set.recipe.ingredient.CustomItemIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.DataVanillaIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;

public class ChooseIngredient extends GuiMenu {
	
	private final IngredientListener listener;
	private final GuiComponent returnMenu;
	private final ItemSet set;
	private final boolean allowEmpty;

	public ChooseIngredient(GuiComponent returnMenu, IngredientListener listener, boolean allowEmpty, ItemSet set) {
		this.listener = listener;
		this.returnMenu = returnMenu;
		this.allowEmpty = allowEmpty;
		this.set = set;
	}

	@Override
	protected void addComponents() {
		addComponent(new TextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.2f, 0.3f, 0.35f, 0.4f);
		addComponent(new TextButton("Custom Item", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectCustomItem(returnMenu, (CustomItem item) -> {
				listener.set(new CustomItemIngredient(item));
				//the SelectCustomItem will go the the returnGui automatically
			}, set));
		}), 0.6f, 0.7f, 0.8f, 0.8f);
		addComponent(new TextButton("Simple vanilla item", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectSimpleVanillaItem(returnMenu, (Material material) -> {
				listener.set(new SimpleVanillaIngredient(material));
				//the SelectSimpleVanillaItem will go to the returnGui automatically
			}));
		}), 0.6f, 0.55f, 0.8f, 0.65f);
		addComponent(new TextButton("Vanilla item with datavalue", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectDataVanillaItem(returnMenu, (Material material, byte data) -> {
				listener.set(new DataVanillaIngredient(material, data));
			}));
		}), 0.6f, 0.4f, 0.8f, 0.5f);
		if (allowEmpty) {
			addComponent(new TextButton("Empty", EditProps.BUTTON, EditProps.HOVER, () -> {
				listener.set(new NoIngredient());
			}), 0.6f, 0.25f, 0.8f, 0.35f);
		}
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}