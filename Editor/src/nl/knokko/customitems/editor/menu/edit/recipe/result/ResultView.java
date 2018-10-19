package nl.knokko.customitems.editor.menu.edit.recipe.result;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;

public class ResultView extends GuiMenu {
	
	private final ResultComponent component;

	public ResultView(ResultComponent component) {
		this.component = component;
	}

	@Override
	protected void addComponents() {
		addComponent(new TextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(component.getMenu());
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		addComponent(new TextButton("Change", EditProps.BUTTON, EditProps.HOVER, () -> {
			//TODO open result change
		}), 0.1f, 0.3f, 0.25f, 0.4f);
		String[] info = component.getResult().getInfo();
		for (int index = 0; index < info.length; index++)
			addComponent(new TextComponent(info[index], EditProps.LABEL), 0.4f, 0.8f - index * 0.15f, 0.7f, 0.9f - index * 0.15f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}