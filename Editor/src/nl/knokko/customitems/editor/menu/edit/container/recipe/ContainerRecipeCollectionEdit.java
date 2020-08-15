package nl.knokko.customitems.editor.menu.edit.container.recipe;

import java.awt.image.BufferedImage;
import java.util.Collection;

import nl.knokko.customitems.container.slot.CustomSlot;
import nl.knokko.customitems.editor.menu.edit.CollectionEdit;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.container.EditContainer;
import nl.knokko.customitems.editor.set.recipe.result.CustomItemResult;
import nl.knokko.customitems.recipe.ContainerRecipe;
import nl.knokko.customitems.recipe.ContainerRecipe.OutputEntry;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ContainerRecipeCollectionEdit extends CollectionEdit<ContainerRecipe> {
	
	private final Iterable<CustomSlot> slots;
	private final Collection<ContainerRecipe> recipes;

	public ContainerRecipeCollectionEdit(
			Iterable<CustomSlot> slots, Collection<ContainerRecipe> recipes, EditContainer editMenu
	) {
		super(
				new ContainerRecipeActionHandler(slots, recipes, editMenu), 
				recipes
		);
		this.slots = slots;
		this.recipes = recipes;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Add recipe", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditContainerRecipe(slots, recipes, this, null, null));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	private static class ContainerRecipeActionHandler implements ActionHandler<ContainerRecipe> {

		private final Iterable<CustomSlot> slots;
		private final Collection<ContainerRecipe> recipes;
		private final EditContainer editMenu;
		
		ContainerRecipeActionHandler(Iterable<CustomSlot> slots, 
				Collection<ContainerRecipe> recipes, EditContainer editMenu) {
			this.slots = slots;
			this.recipes = recipes;
			this.editMenu = editMenu;
		}
		
		@Override
		public void goBack() {
			editMenu.getState().getWindow().setMainComponent(editMenu);
		}

		@Override
		public BufferedImage getImage(ContainerRecipe item) {
			
			// If we find an output with a custom item, take it!
			for (OutputEntry output : item.getOutputs()) {
				if (output.getResult() instanceof CustomItemResult) {
					CustomItemResult customResult = (CustomItemResult) output.getResult();
					return customResult.getItem().getTexture().getImage();
				}
			}
			
			// Otherwise, we don't have an icon ;(
			return null;
		}

		@Override
		public String getLabel(ContainerRecipe item) {
			StringBuilder result = new StringBuilder();
			result.append('(');
			for (OutputEntry output : item.getOutputs()) {
				result.append(output.getResult());
				result.append(',');
			}
			result.append(')');
			return null;
		}
		
		private GuiComponent thisMenu() {
			return editMenu.getState().getWindow().getMainComponent();
		}

		@Override
		public GuiComponent createEditMenu(ContainerRecipe itemToEdit, GuiComponent returnMenu) {
			return new EditContainerRecipe(slots, recipes, thisMenu(), itemToEdit, itemToEdit);
		}

		@Override
		public GuiComponent createCopyMenu(ContainerRecipe itemToCopy, GuiComponent returnMenu) {
			return new EditContainerRecipe(slots, recipes, thisMenu(), itemToCopy, null);
		}

		@Override
		public String deleteItem(ContainerRecipe itemToDelete) {
			return recipes.remove(itemToDelete) 
					? null : "This recipe wasn't in the list of container recipes";
		}
	}
}
