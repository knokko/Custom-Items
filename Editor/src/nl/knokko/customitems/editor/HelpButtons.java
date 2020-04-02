package nl.knokko.customitems.editor;

import java.awt.Desktop;
import java.net.MalformedURLException;
import java.net.URL;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class HelpButtons {
	
	public static void openWebpage(String urlString) {
		URL url;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			throw new Error(e);
		}
		
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	            desktop.browse(url.toURI());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}

	public static void addHelpLink(GuiMenu target, String urlEnd) {
		target.addComponent(new DynamicTextButton("?", EditProps.QUIT_BASE, EditProps.QUIT_HOVER, () -> {
			openWebpage("https://knokko.github.io/custom%20items/editor%20documentation/" + urlEnd);
		}), 0.01f, 0.95f, 0.03f, 0.99f);
	}
	
	public static void addCustomHelpLink(GuiMenu target, String fullURL) {
		target.addComponent(new DynamicTextButton("?", EditProps.QUIT_BASE, EditProps.QUIT_HOVER, () -> {
			openWebpage(fullURL);
		}), 0.01f, 0.95f, 0.03f, 0.99f);
	}
}
