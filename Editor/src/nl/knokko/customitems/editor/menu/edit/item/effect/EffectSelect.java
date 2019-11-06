package nl.knokko.customitems.editor.menu.edit.item.effect;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.effect.EffectType;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class EffectSelect extends GuiMenu {
	
	private final Receiver receiver;
	private final GuiComponent returnMenu;

	public EffectSelect(Receiver receiver, GuiComponent returnMenu) {
		this.receiver = receiver;
		this.returnMenu = returnMenu;
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.1f, 0.1f, 0.2f, 0.2f);
		float x = 0.3f;
		float y = 0.9f;
		EffectType[] effects = EffectType.values();
		for (EffectType effect : effects) {
			addComponent(new DynamicTextButton(effect.toString(), EditProps.SELECT_BASE, EditProps.SELECT_HOVER, () -> {
				receiver.onSelect(effect);
				state.getWindow().setMainComponent(returnMenu);
			}), x, y - 0.05f, x + 0.125f, y);
			y -= 0.06f;
			if (y < 0.15f) {
				y = 0.9f;
				x += 0.15f;
			}
		}
	}
	
	public static interface Receiver {
		void onSelect(EffectType enchantment);
	}

}
