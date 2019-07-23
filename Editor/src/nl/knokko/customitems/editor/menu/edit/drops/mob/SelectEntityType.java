package nl.knokko.customitems.editor.menu.edit.drops.mob;

import nl.knokko.customitems.drops.CIEntityType;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class SelectEntityType extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final Receiver receiver;
	
	public SelectEntityType(GuiComponent returnMenu, Receiver receiver) {
		this.returnMenu = returnMenu;
		this.receiver = receiver;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.8f, 0.2f, 0.9f);
		
		CIEntityType[] entityTypes = CIEntityType.values();
		for (int index = 0; index < entityTypes.length; index++) {
			CIEntityType currentType = entityTypes[index];
			addComponent(new DynamicTextButton(currentType.toString(), EditProps.SELECT_BASE, EditProps.SELECT_HOVER, () -> {
				receiver.onSelect(currentType);
				state.getWindow().setMainComponent(returnMenu);
			}), 0.5f, 0.9f - index * 0.1f, 0.8f, 1f - index * 0.1f);
		}
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	public static interface Receiver {
		
		void onSelect(CIEntityType newType);
	}
}
