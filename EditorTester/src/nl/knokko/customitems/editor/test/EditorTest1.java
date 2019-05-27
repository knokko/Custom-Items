package nl.knokko.customitems.editor.test;

import nl.knokko.customitems.editor.test.editmenu.ExploreEditMenuTest;
import nl.knokko.customitems.editor.test.editmenu.item.CreateSimpleItemTest;
import nl.knokko.customitems.editor.test.editmenu.texture.CreateBowTextureTest1;
import nl.knokko.customitems.editor.test.editmenu.texture.CreateSimpleTextureTest1;
import nl.knokko.customitems.editor.test.mainmenu.CreateItemSetTest;
import nl.knokko.customitems.editor.test.mainmenu.ExploreMainMenuTest;
import nl.knokko.gui.testing.GuiTestHelper;
import nl.knokko.gui.testing.GuiTestProgram;

/**
 * This is the first combined test program. It tests the basic functionality of the menus and then creates
 * the first part of the test item set. It should be run from the main menu.
 * @author knokko
 *
 */
public class EditorTest1 implements GuiTestProgram {

	@Override
	public void test(GuiTestHelper test) {
		test.setDelayTime(20);
		ExploreMainMenuTest.test(test);
		CreateItemSetTest.test(test, "automatic test");
		ExploreEditMenuTest.test(test);
		CreateBowTextureTest1.test(test, "test_bow_first");
		CreateSimpleTextureTest1.test(test, "simple_test");
		CreateSimpleItemTest.create(test, "simple_test_item", "simple_test_one", "The very first line of lore", "The second line of lore");
	}
}