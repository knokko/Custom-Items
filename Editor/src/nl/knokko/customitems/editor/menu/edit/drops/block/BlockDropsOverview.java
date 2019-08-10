package nl.knokko.customitems.editor.menu.edit.drops.block;

import java.util.Collection;

import nl.knokko.customitems.drops.BlockDrop;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class BlockDropsOverview extends GuiMenu {
	
	private final ItemSet set;
	private final GuiComponent returnMenu;
	
	private final DropsList list;
	
	public BlockDropsOverview(ItemSet set, GuiComponent returnMenu) {
		this.set = set;
		this.returnMenu = returnMenu;
		
		this.list = new DropsList();
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.8f, 0.2f, 0.9f);
		addComponent(new DynamicTextButton("New block drop", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditBlockDrop(set, this, null));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		addComponent(list, 0.3f, 0f, 1f, 1f);
	}
	
	@Override
	public void init() {
		if(didInit) list.refresh();
		super.init();
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	private class DropsList extends GuiMenu {

		@Override
		protected void addComponents() {
			refresh();
		}
		
		private void refresh() {
			clearComponents();
			Collection<BlockDrop> blockDrops = set.getBackingBlockDrops();
			int index = 0;
			for (BlockDrop drop : blockDrops) {
				addComponent(new BlockDropEntry(drop), 0f, 0.9f - 0.125f * index, 1f, 1f - 0.125f * index);
				index++;
			}
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND2;
		}
		
		private class BlockDropEntry extends GuiMenu {
			
			private final BlockDrop drop;
			
			private BlockDropEntry(BlockDrop drop) {
				this.drop = drop;
			}

			@Override
			protected void addComponents() {
				addComponent(new DynamicTextComponent(drop.toString(), EditProps.LABEL), 0.025f, 0.05f, 0.4f, 0.95f);
				addComponent(new DynamicTextButton("Edit", EditProps.BUTTON, EditProps.HOVER, () -> {
					state.getWindow().setMainComponent(new EditBlockDrop(set, BlockDropsOverview.this, drop));
				}), 0.5f, 0.05f, 0.7f, 0.95f);
				addComponent(new DynamicTextButton("Delete", EditProps.QUIT_BASE, EditProps.QUIT_HOVER, () -> {
					set.removeBlockDrop(drop);
					list.refresh();
				}), 0.75f, 0.05f, 0.95f, 0.95f);
			}
			
			@Override
			public GuiColor getBackgroundColor() {
				return EditProps.BACKGROUND2;
			}
		}
	}
}
