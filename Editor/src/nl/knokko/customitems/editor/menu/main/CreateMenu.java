package nl.knokko.customitems.editor.menu.main;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import nl.knokko.customitems.editor.Editor;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.util.TextBuilder.Properties;

public class CreateMenu extends GuiMenu {
	
	public static final CreateMenu INSTANCE = new CreateMenu();
	
	public static final Properties LABEL_PROPERTIES = Properties.createLabel();
	
	private TextEditField fileName;
	private TextComponent errorComponent;
	
	@Override
	protected void addComponents() {
		fileName = new TextEditField("", Properties.createEdit(), Properties.createEdit(new Color(200, 200, 255), new Color(0, 200, 200), new Color(0, 200, 200)));
		errorComponent = new TextComponent("", Properties.createLabel(Color.RED));
		addComponent(errorComponent, 0.1f, 0.8f, 0.9f, 1);
		addComponent(new TextComponent("Filename: ", LABEL_PROPERTIES), 0.2f, 0.5f, 0.4f, 0.6f);
		addComponent(fileName, 0.45f, 0.5f, 0.75f, 0.6f);
		addComponent(new TextButton("Cancel", Properties.createButton(new Color(200, 100, 0), new Color(50, 25, 0)), Properties.createButton(new Color(250, 125, 0), new Color(60, 30, 0)), () -> {
			state.getWindow().setMainComponent(MainMenu.INSTANCE);
		}), 0.1f, 0.65f, 0.3f, 0.75f);
		addComponent(new TextButton("Create", Properties.createButton(new Color(200, 100, 0), new Color(50, 25, 0)), Properties.createButton(new Color(250, 125, 0), new Color(60, 30, 0)), () -> {
			if(!fileName.getText().isEmpty()) {
				File file = new File(Editor.getFolder() + "/" + fileName.getText() + ".cis");
				try {
					if(file.createNewFile()) {
						state.getWindow().setMainComponent(new EditMenu(new ItemSet(fileName.getText())));
					} else {
						errorComponent.setText("An item set with that name already exists.");
					}
				} catch(IOException ioex) {
					errorComponent.setText("An IO error occured during the write test: " + ioex.getMessage());
				} catch(SecurityException se) {
					errorComponent.setText("It looks like this application doesn't have the rights to create this file.");
				}
				file.delete();
			}
		}), 0.35f, 0.25f, 0.65f, 0.35f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return new SimpleGuiColor(200, 150, 0);
	}
}