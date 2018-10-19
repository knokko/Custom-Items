package nl.knokko.customitems.editor.menu.edit.recipe.result;

import java.awt.Color;

import nl.knokko.customitems.editor.set.recipe.result.Result;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.util.TextBuilder.Properties;

public class ResultComponent extends TextButton {
	
	public static final Properties PROPS = Properties.createLabel(Color.BLACK, new Color(0, 0, 150));
	public static final Properties HOVER_PROPS = Properties.createLabel(Color.BLACK, new Color(0,  0, 220), 512, 128);
	
	private Result current;

	public ResultComponent(Result original) {
		super(original.toString(), PROPS, HOVER_PROPS, null);
		clickAction = () -> {
			state.getWindow().setMainComponent(new ResultView(this));
		};
		current = original;
	}
	
	public void setResult(Result newResult) {
		current = newResult;
	}
	
	public Result getResult() {
		return current;
	}
}