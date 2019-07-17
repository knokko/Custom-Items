package nl.knokko.customitems.editor.menu.edit.drops.block;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class BlockDropsOverview extends GuiMenu {
	
	private final ItemSet set;
	private final GuiComponent returnMenu;
	
	private final DropsList list;
	
	public BlockDropsOverview(ItemSet set, GuiComponent returnMenu) {
		this.set = set;
		this.returnMenu = returnMenu;
		
		this.list = new DropsList(set);
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.8f, 0.2f, 0.9f);
		addComponent(list, 0.3f, 0.025f, 1f, 0.9f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	private static class DropsList extends GuiMenu {
		
		private final ItemSet set;
		
		private DropsList(ItemSet set) {
			this.set = set;
		}

		@Override
		protected void addComponents() {
			refresh();
		}
		
		private void refresh() {
			clearComponents();
			// TODO Now add the drops...
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND2;
		}
	}

}
