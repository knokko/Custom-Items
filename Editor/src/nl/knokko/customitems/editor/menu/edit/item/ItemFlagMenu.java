package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.item.ItemFlag;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;

public class ItemFlagMenu extends GuiMenu {
	
	private final EditItemBase itemEdit;
	private final CheckboxComponent[] checkBoxes;
	
	public ItemFlagMenu(EditItemBase itemEdit, boolean[] oldFlags) {
		this.itemEdit = itemEdit;
		this.checkBoxes = new CheckboxComponent[oldFlags.length];
		for (int index = 0; index < oldFlags.length; index++) {
			checkBoxes[index] = new CheckboxComponent(oldFlags[index]);
		}
	}

	@Override
	protected void addComponents() {
		ItemFlag[] allFlags = ItemFlag.values();
		for (int index = 0; index < allFlags.length; index++) {
			addComponent(checkBoxes[index], 0.4f, 0.725f - 0.1f * index, 0.425f, 0.75f - 0.1f * index);
			addComponent(new TextComponent(allFlags[index].toString(), EditProps.LABEL), 0.45f, 0.725f - 0.1f * index, 0.65f, 0.8f - 0.1f * index);
		}
		addComponent(new TextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(itemEdit);
		}), 0.1f, 0.9f, 0.25f, 0.97f);
		addComponent(new TextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			boolean[] newFlags = new boolean[checkBoxes.length];
			for (int index = 0; index < newFlags.length; index++) {
				newFlags[index] = checkBoxes[index].isChecked();
			}
			itemEdit.setItemFlags(newFlags);
			state.getWindow().setMainComponent(itemEdit);
		}), 0.1f, 0.13f, 0.25f, 0.2f);
		
		if (itemEdit instanceof EditItemSimple) {
			addComponent(new TextComponent("Notice: it is recommended for simple custom items to keep the 'hide unbreakable' checked", EditProps.LABEL), 0.05f, 0.025f, 0.95f, 0.1f);
		}
		if (itemEdit instanceof EditItemTool && !((EditItemTool) itemEdit).durability.getText().equals("-1")) {
			addComponent(new TextComponent("Notice: it is recommended for breakable custom tools to keep the 'hide unbreakable' checked", EditProps.LABEL), 0.05f, 0.025f, 0.95f, 0.1f);
		}
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}