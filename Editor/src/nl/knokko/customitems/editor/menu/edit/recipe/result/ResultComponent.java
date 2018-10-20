package nl.knokko.customitems.editor.menu.edit.recipe.result;

import java.awt.Color;

import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.recipe.result.Result;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.util.TextBuilder.Properties;

public class ResultComponent extends TextButton {
	
	public static final Properties PROPS = Properties.createButton(new Color(0, 150, 0), new Color(0, 50, 0), 512, 128);
	public static final Properties HOVER_PROPS = Properties.createButton(new Color(0, 220, 0), new Color(0, 70, 0), 512, 128);
	
	private Result current;
	
	private final GuiComponent menu;

	public ResultComponent(Result original, GuiComponent menu, ItemSet set) {
		super(original.toString(), PROPS, HOVER_PROPS, null);
		clickAction = () -> {
			state.getWindow().setMainComponent(new ResultView(this, set));
		};
		current = original;
		this.menu = menu;
	}
	
	public void setResult(Result newResult) {
		current = newResult;
		setText(newResult.toString());
	}
	
	public Result getResult() {
		return current;
	}
	
	public GuiComponent getMenu() {
		return menu;
	}
}