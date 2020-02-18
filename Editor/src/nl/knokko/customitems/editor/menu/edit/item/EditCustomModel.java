package nl.knokko.customitems.editor.menu.edit.item;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Scanner;

import nl.knokko.customitems.editor.menu.commandhelp.CommandBlockHelpOverview;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.item.EditCustomModel.ByteArrayFileConverter;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.FileChooserMenu;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.ConditionalTextButton;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditCustomModel extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final ByteArrayListener receiver;
	
	private final String[] exampleContent;
	private final byte[] currentContent;
	private TextEditField parent = new TextEditField("handheld", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
	
	public EditCustomModel(String[] exampleContent, GuiComponent returnMenu, ByteArrayListener receiver, byte[] currentContent) {
		this.returnMenu = returnMenu;
		this.receiver = receiver;
		this.exampleContent = exampleContent;
		this.currentContent = currentContent;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.8f, 0.175f, 0.9f);
		addComponent(new ConditionalTextButton("Change to default model with given parent", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			String output = "";
			for (String content: exampleContent) {
				output += content + "\n";
			}
			String result = output.replaceFirst("handheld", parent.getText());
			byte[] array = result.getBytes();
			receiver.readArray(array);
			state.getWindow().setMainComponent(returnMenu);
		}, () -> {
			return exampleContent != null && !parent.getText().equals("handheld");
		}), 0.65f, 0.025f, 0.995f, 0.125f);
		addComponent(new DynamicTextComponent("The editor will simply put the model you choose in the resourcepack", EditProps.LABEL), 0.1f, 0.7f, 0.9f, 0.8f);
		addComponent(new DynamicTextComponent("upon exporting, no attempt will be made to read the model json.", EditProps.LABEL), 0.1f, 0.6f, 0.85f, 0.7f);
		
		if (exampleContent != null && currentContent == null) {
			addComponent(new DynamicTextComponent("The default model for this item would be:", EditProps.LABEL), 0.1f, 0.5f, 0.6f, 0.6f);
			int index = 0;
			for (String content : exampleContent) {
				addComponent(new DynamicTextComponent(content, EditProps.LABEL), 0.025f, 0.40f - 0.05f * index, 0.025f + content.length() * 0.01f, 0.45f - 0.05f * index);
				index++;
			}
		} else if (currentContent != null) {
			try {
				String asString = StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(currentContent)).toString();
				Scanner lineByLine = new Scanner(asString);
				addComponent(new DynamicTextComponent("The current custom model for this item is:", EditProps.LABEL), 0.1f, 0.5f, 0.6f, 0.6f);
				
				int index = 0;
				while (lineByLine.hasNextLine()) {
					String content = lineByLine.nextLine();
					addComponent(new DynamicTextComponent(content, EditProps.LABEL), 0.025f, 0.40f - 0.05f * index, 0.025f + content.length() * 0.01f, 0.45f - 0.05f * index);
					index++;
				}
				
				lineByLine.close();
				
			} catch (CharacterCodingException e) {
				addComponent(new DynamicTextComponent("The current custom model for this item seems to be invalid", EditProps.LABEL), 0.1f, 0.5f, 0.6f, 0.6f);
			}
			
		}
		addComponent(new DynamicTextButton("Select file...", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new FileChooserMenu(returnMenu, (File file) -> {
				receiver.readArray(new ByteArrayFileListener().convertFile(file));
				return returnMenu;
			}, (File file) -> {
				return file.getName().endsWith(".json");
			}, EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, 
					EditProps.BACKGROUND, EditProps.BACKGROUND2));
		}), 0.2f, 0.8f, 0.375f, 0.9f);
		if (exampleContent != null) {
			addComponent(new DynamicTextButton("Copy Default Model", EditProps.BUTTON, EditProps.HOVER, () ->  {
				String result = "";
				for (String content: exampleContent) {
					result += content + "\n";
				}
				CommandBlockHelpOverview.setClipboard(result);
			}), 0.4f, 0.8f, 0.675f, 0.9f);
			addComponent(new DynamicTextComponent("Default Parent:", EditProps.LABEL), 0.8f, 0.325f, 0.975f, 0.425f);
			addComponent(parent, 0.8f, 0.2f, 0.975f, 0.3f);
		}
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	public static interface ByteArrayFileConverter{
		byte[] convertFile(File file);
	}

	public static interface ByteArrayListener {
		void readArray(byte[] array);
	}
}

class ByteArrayFileListener implements ByteArrayFileConverter{
	public byte[] convertFile (File file) {
		try {
			if (file.length() > 500000000) {
				return new byte[0];
			}
			byte[] result = new byte[(int) file.length()];
			InputStream in = Files.newInputStream(file.toPath());
			DataInputStream dataIn = new DataInputStream(in);
			dataIn.readFully(result);
			in.close();
			return result;
		} catch (IOException ioex) {
			return new byte[0];
		}
	}
}
