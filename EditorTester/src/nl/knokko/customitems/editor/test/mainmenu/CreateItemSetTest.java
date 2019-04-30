package nl.knokko.customitems.editor.test.mainmenu;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import nl.knokko.customitems.editor.Editor;
import nl.knokko.gui.testing.GuiTestHelper;
import nl.knokko.gui.testing.GuiTestProgram;
import nl.knokko.gui.testing.TestException;

/**
 * A small test to create a new item set. If there is already an item set with the given name, it will be
 * deleted first. It should be run from the main menu.
 * @author knokko
 *
 */
public class CreateItemSetTest implements GuiTestProgram {
	
	private final String name;
	
	public CreateItemSetTest(String name) {
		this.name = name;
	}

	@Override
	public void test(GuiTestHelper test) {
		
		// If the file already exists, delete it!
		File maybe = new File(Editor.getFolder() + "/" + name + ".cisb");
		maybe.delete();
		
		// Also test that it forbids to create an item set with the same name as an existing item set
		try {
			new File(Editor.getFolder() + "/" + name + "full.cisb").createNewFile();
		} catch (IOException e) {
			throw new TestException("Failed to create a dumb test file");
		}
		
		test.click("New item set");
		test.clickNearest("", test.getComponentWithText("Create"), 0, 2);
		
		test.type(name + "full");
		test.click("Create");
		
		test.click(name + "full");
		test.pressAndRelease(KeyEvent.VK_BACK_SPACE, 4);
		
		test.assertComponentWithText(name);
		test.click("Create");
		test.assertComponentWithText("Save and quit");
	}
}