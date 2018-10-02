package nl.knokko.customitems.editor.menu.main;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import nl.knokko.customitems.editor.Editor;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.util.TextBuilder.Properties;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitInputStream;

public class LoadMenu extends GuiMenu {
	
	public static final LoadMenu INSTANCE = new LoadMenu();
	
	private SetList setList;

	@Override
	protected void addComponents() {
		setList = new SetList();
		addComponent(setList, 0.3f, 0f, 1f, 0.8f);
		addComponent(new TextButton("Cancel", Properties.createButton(new Color(200, 100, 0), new Color(50, 25, 0)), Properties.createButton(new Color(250, 125, 0), new Color(75, 35, 0)), () -> {
			state.getWindow().setMainComponent(MainMenu.INSTANCE);
		}), 0.05f, 0.8f, 0.25f, 0.9f);
		addComponent(new TextButton("Refresh", Properties.createButton(new Color(150, 150, 250), new Color(30, 30, 100)), Properties.createButton(new Color(180, 180, 255), new Color(40, 40, 120)), () -> {
			setList.refresh();
		}), 0.35f, 0.85f, 0.55f, 0.95f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return new SimpleGuiColor(200, 150, 0);
	}
	
	private static class SetList extends GuiMenu {
		
		private static final Properties BUTTON_PROPERTIES = Properties.createButton(new Color(0, 200, 0), new Color(0, 50, 0));
		private static final Properties HOVER_PROPERTIES = Properties.createButton(new Color(0, 250, 0), new Color(0, 70, 0));

		@Override
		protected void addComponents() {
			refresh();
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return new SimpleGuiColor(200, 150, 0);
		}
		
		private void refresh() {
			components.clear();
			File folder = Editor.getFolder();
			File[] files = folder.listFiles(new FilenameFilter(){

				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".cis");
				}
			});
			if(files != null) {
				for(int index = 0; index < files.length; index++) {
					final File file = files[index];
					addComponent(new TextButton(file.getName(), BUTTON_PROPERTIES, HOVER_PROPERTIES, () -> {
						try {
							BitInput input = new BitInputStream(new FileInputStream(file));
							ItemSet set = new ItemSet(file.getName().substring(0, file.getName().length() - 4), input);
							input.terminate();
							state.getWindow().setMainComponent(new EditMenu(set));
						} catch(IOException ioex) {
							throw new RuntimeException(ioex);
						}
					}), 0, 0.9f - index * 0.1f, 1, 1 - index * 0.1f);
				}
			}
		}
	}
}