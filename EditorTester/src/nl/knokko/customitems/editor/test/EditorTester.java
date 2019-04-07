package nl.knokko.customitems.editor.test;

import org.jnativehook.NativeHookException;

import nl.knokko.customitems.editor.Editor;
import nl.knokko.customitems.editor.menu.main.MainMenu;
import nl.knokko.gui.window.AWTGuiWindow;
import nl.knokko.gui.window.GuiWindow;
import nl.knokko.test.SafeGuiTester;

public class EditorTester {

	public static void main(String[] args) throws NativeHookException {
		GuiWindow window = new AWTGuiWindow();
		window.setMainComponent(MainMenu.INSTANCE);
		Editor.setWindow(window);
		SafeGuiTester.testSafely(new EditorTest1(), window);
	}
}