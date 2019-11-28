package nl.knokko.customitems.editor.menu.edit.item;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.editor.menu.commandhelp.CommandBlockHelpOverview;
import nl.knokko.customitems.editor.menu.edit.*;
import nl.knokko.customitems.editor.set.item.*;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ItemCollectionEdit extends CollectionEdit<CustomItem> {
	
	private final EditMenu menu;

	public ItemCollectionEdit(EditMenu menu) {
		super(new ItemActionHandler(menu), menu.getSet().getBackingItems());
		this.menu = menu;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Create item", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new CreateItem(menu));
		}), 0.025f, 0.4f, 0.225f, 0.5f);
		addComponent(new DynamicTextButton("Command block help", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new CommandBlockHelpOverview(menu.getSet(), this));
		}), 0.025f, 0.1f, 0.275f, 0.2f);
	}
	
	private static class ItemActionHandler implements ActionHandler<CustomItem> {
		
		private final EditMenu menu;
		
		private ItemActionHandler(EditMenu menu) {
			this.menu = menu;
		}

		@Override
		public void goBack() {
			menu.getState().getWindow().setMainComponent(menu);
		}

		@Override
		public GuiComponent createEditMenu(CustomItem item, GuiComponent returnMenu) {
			if (item instanceof CustomBow)
				return new EditItemBow(menu, (CustomBow) item);
			else if (item instanceof CustomArmor)
				return new EditItemArmor(menu, (CustomArmor) item, item.getItemType().getMainCategory());
			else if (item instanceof CustomShears) 
				return new EditItemShears(menu, (CustomShears) item);
			else if (item instanceof CustomHoe)
				return new EditItemHoe(menu, (CustomHoe) item);
			else if (item instanceof CustomShield)
				return new EditItemShield(menu, (CustomShield) item);
			else if (item instanceof CustomTrident)
				return new EditItemTrident(menu, (CustomTrident) item);
			else if (item instanceof CustomTool)
				return new EditItemTool(menu, (CustomTool) item, item.getItemType().getMainCategory());
			else if (item instanceof CustomWand)
				return new EditItemWand(menu, (CustomWand) item);
			else if (item instanceof SimpleCustomItem)
				return new EditItemSimple(menu, (SimpleCustomItem) item);
			else
				throw new IllegalArgumentException("Unsupported custom item class: " + item.getClass());
		}

		@Override
		public String deleteItem(CustomItem itemToDelete) {
			return menu.getSet().removeItem(itemToDelete);
		}

		@Override
		public String getLabel(CustomItem item) {
			return item.getName();
		}

		@Override
		public BufferedImage getImage(CustomItem item) {
			return item.getTexture().getImage();
		}
	}
}
