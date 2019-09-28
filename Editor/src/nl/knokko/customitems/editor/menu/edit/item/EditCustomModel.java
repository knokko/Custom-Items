package nl.knokko.customitems.editor.menu.edit.item;

import java.io.File;

import nl.knokko.customitems.editor.menu.commandhelp.CommandBlockHelpOverview;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.FileChooserMenu;
import nl.knokko.gui.component.menu.FileChooserMenu.FileListener;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditCustomModel extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final FileListener receiver;
	
	private final String[] exampleContent;
	
	public EditCustomModel(String[] exampleContent, GuiComponent returnMenu, FileListener receiver) {
		this.returnMenu = returnMenu;
		this.receiver = receiver;
		this.exampleContent = exampleContent;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.8f, 0.2f, 0.9f);
		addComponent(new DynamicTextComponent("The editor will simply put the model you choose in the resourcepack", EditProps.LABEL), 0.1f, 0.7f, 0.9f, 0.8f);
		addComponent(new DynamicTextComponent("upon exporting, no attempt will be made to read the model json.", EditProps.LABEL), 0.1f, 0.6f, 0.85f, 0.7f);
		addComponent(new DynamicTextComponent("The default model for this item would be:", EditProps.LABEL), 0.1f, 0.5f, 0.6f, 0.6f);
		int index = 0;
		for (String content : exampleContent) {
			addComponent(new DynamicTextComponent(content, EditProps.LABEL), 0.2f, 0.40f - 0.05f * index, 0.2f + content.length() * 0.01f, 0.45f - 0.05f * index);
			index++;
		}
		addComponent(new DynamicTextButton("Select file...", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new FileChooserMenu(returnMenu, receiver, (File file) -> {
				return file.getName().endsWith(".json");
			}, EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, 
					EditProps.BACKGROUND, EditProps.BACKGROUND2));
		}), 0.225f, 0.8f, 0.4f, 0.9f);
		addComponent(new DynamicTextButton("Copy Default Model", EditProps.BUTTON, EditProps.HOVER, () ->  {
			String result = "";
			for (String content: exampleContent) {
				result += content + "\n";
			}
			CommandBlockHelpOverview.setClipboard(result);
		}), 0.425f, 0.8f, 0.7f, 0.9f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
