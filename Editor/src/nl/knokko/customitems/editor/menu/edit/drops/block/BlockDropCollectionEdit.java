package nl.knokko.customitems.editor.menu.edit.drops.block;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.drops.BlockDrop;
import nl.knokko.customitems.editor.HelpButtons;
import nl.knokko.customitems.editor.menu.edit.CollectionEdit;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class BlockDropCollectionEdit extends CollectionEdit<BlockDrop> {
	
	private final EditMenu menu;

	public BlockDropCollectionEdit(EditMenu menu) {
		super(new BlockDropActionHandler(menu), menu.getSet().getBackingBlockDrops());
		this.menu = menu;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("New block drop", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditBlockDrop(menu.getSet(), this, null, null));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/drops/blocks.html");
	}

	private static class BlockDropActionHandler implements ActionHandler<BlockDrop> {
		
		private final EditMenu menu;
		
		private BlockDropActionHandler(EditMenu menu) {
			this.menu = menu;
		}

		@Override
		public void goBack() {
			menu.getState().getWindow().setMainComponent(menu.getDropsMenu());
		}

		@Override
		public BufferedImage getImage(BlockDrop item) {
			return ((CustomItem)item.getDrop().getItemToDrop()).getTexture().getImage();
		}

		@Override
		public String getLabel(BlockDrop item) {
			return item.toString();
		}

		@Override
		public GuiComponent createEditMenu(BlockDrop drop, GuiComponent returnMenu) {
			return new EditBlockDrop(menu.getSet(), returnMenu, drop, drop);
		}
		
		@Override
		public GuiComponent createCopyMenu(BlockDrop drop, GuiComponent returnMenu) {
			return new EditBlockDrop(menu.getSet(), returnMenu, drop, null);
		}

		@Override
		public String deleteItem(BlockDrop itemToDelete) {
			menu.getSet().removeBlockDrop(itemToDelete);
			
			// Not much to go wrong when deleting block drops
			return null;
		}
	}
}
