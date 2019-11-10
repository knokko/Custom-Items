package nl.knokko.customitems.editor.menu.edit;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class EnumSelect<T extends Enum<?>> extends GuiMenu {
	
	public static <T extends Enum<?>> GuiComponent createSelectButton(Class<T> enumClass, Receiver<T> receiver, T current) {
		String text = current == null ? "None" : current.toString();
		return new DynamicTextButton(text, EditProps.BUTTON, EditProps.HOVER, null) {
			
			@Override
			public void click(float x, float y, int button) {
				state.getWindow().setMainComponent(new EnumSelect<T>(enumClass, (T newType) -> {
					setText(newType.toString());
					receiver.onSelect(newType);
				}, state.getWindow().getMainComponent()));
			}
		};
	}
	
	private final Class<T> enumClass;
	private final Receiver<T> receiver;
	private final GuiComponent returnMenu;
	
	private EnumSelect(Class<T> enumClass, Receiver<T> receiver, GuiComponent returnMenu) {
		this.enumClass = enumClass;
		this.receiver = receiver;
		this.returnMenu = returnMenu;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.2f, 0.8f);
		
		T[] all = enumClass.getEnumConstants();
		float x = 0.25f;
		float y = 0.8f;
		for (T currentType : all) {
			addComponent(new DynamicTextButton(currentType.toString(), EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
				receiver.onSelect(currentType);
				state.getWindow().setMainComponent(returnMenu);
			}), x, y - 0.1f, x + 0.2f, y);
			y -= 0.15f;
			if (y < 0.1f) {
				x += 0.25f;
				y = 0.8f;
			}
		}
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	public static interface Receiver<T> {
		
		void onSelect(T selected);
	}
}
