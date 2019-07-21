package nl.knokko.customitems.editor.menu.edit.drops.block;

import nl.knokko.customitems.drops.BlockType;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class SelectBlockType extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final Receiver receiver;
	
	public SelectBlockType(GuiComponent returnMenu, Receiver receiver) {
		this.returnMenu = returnMenu;
		this.receiver = receiver;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.8f, 0.2f, 0.9f);
		
		BlockType[] blocks = BlockType.values();
		for (int index = 0; index < blocks.length; index++) {
			BlockType block = blocks[index];
			addComponent(new DynamicTextButton(block.toString(), EditProps.SELECT_BASE, EditProps.SELECT_HOVER, () -> {
				receiver.onSelect(block);
				state.getWindow().setMainComponent(returnMenu);
			}), 0.4f, 0.9f - index * 0.1f, 1f, 1f - index * 0.1f);
		}
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	public static interface Receiver {
		
		void onSelect(BlockType newBlock);
	}
}
