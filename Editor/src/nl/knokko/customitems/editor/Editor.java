/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2019 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package nl.knokko.customitems.editor;

import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.filechooser.FileSystemView;

import nl.knokko.customitems.editor.SystemTests.SystemTestResult;
import nl.knokko.customitems.editor.menu.main.MainMenu;
import nl.knokko.gui.window.AWTGuiWindow;
import nl.knokko.gui.window.GuiWindow;

public class Editor {
	
	private static final File FOLDER = new File(FileSystemView.getFileSystemView().getDefaultDirectory() + "/Custom Item Sets");
	private static final File LOGS_FOLDER = new File(FOLDER + "/logs");
	private static final File BACKUPS_FOLDER = new File(FOLDER + "/backups");
	
	private static GuiWindow window;
	
	public static GuiWindow getWindow() {
		return window;
	}
	
	/**
	 * This method is made for the Editor tester
	 * @param window The testing window instance
	 */
	public static void setWindow(GuiWindow window) {
		Editor.window = window;
	}

	public static void main(String[] args) {
		FOLDER.mkdirs();
		try {
			LOGS_FOLDER.mkdir();
			long time = System.currentTimeMillis();
			System.setOut(new Logger(new File(LOGS_FOLDER + "/out " + time + ".txt"), System.out));
			System.setErr(new Logger(new File(LOGS_FOLDER + "/err " + time + ".txt"), System.err));
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		System.out.println("test out");
		System.err.println("test error");
		window = new AWTGuiWindow();
		
		SystemTestResult systemTestResult = SystemTests.performTests();
		if (systemTestResult == SystemTestResult.SUCCESS) {
			System.out.println("All system tests succeeded");
			window.setMainComponent(MainMenu.INSTANCE);
		} else {
			System.err.println("The system tests failed: " + systemTestResult);
			window.setMainComponent(new SystemTestFailureMenu(systemTestResult, 1));
		}
		
		window.open("Custom Items Editor", true);
		window.run(30);
	}
	
	public static File getFolder() {
		return FOLDER;
	}
	
	public static File getBackupFolder() {
		return BACKUPS_FOLDER;
	}
	
	public static File getLogsFolder() {
		return LOGS_FOLDER;
	}
}