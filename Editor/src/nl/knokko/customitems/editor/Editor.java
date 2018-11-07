package nl.knokko.customitems.editor;

import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.filechooser.FileSystemView;

import nl.knokko.customitems.editor.menu.main.MainMenu;
import nl.knokko.gui.window.AWTGuiWindow;
import nl.knokko.gui.window.GuiWindow;

public class Editor {
	
	private static final File FOLDER = new File(FileSystemView.getFileSystemView().getDefaultDirectory() + "/Custom Item Sets");
	private static final File LOGS_FOLDER = new File(FOLDER + "/logs");
	private static final File BACKUPS_FOLDER = new File(FOLDER + "/backups");

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
		GuiWindow window = new AWTGuiWindow();
		window.setMainComponent(MainMenu.INSTANCE);
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