package nl.knokko.customitems.editor.test;

import java.io.File;

import nl.knokko.customitems.editor.Editor;
import nl.knokko.gui.testing.GuiTestHelper;
import nl.knokko.gui.testing.GuiTestProgram;

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
		test.setDelayTime(200);
		
		// If the file already exists, delete it!
		File maybe = new File(Editor.getFolder() + "/" + name + ".cisb");
		maybe.delete();
		
		test.click("New item set");
		test.clickNearest("", test.getComponentWithText("Create"), 0);
		test.type(name);
		test.click("Create");
	}
}