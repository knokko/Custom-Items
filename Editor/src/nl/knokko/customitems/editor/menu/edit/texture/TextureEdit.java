package nl.knokko.customitems.editor.menu.edit.texture;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.set.NamedImage;
import nl.knokko.gui.component.menu.GuiMenu;

public class TextureEdit extends GuiMenu {
	
	protected final EditMenu menu;
	protected final NamedImage previous;

	public TextureEdit(EditMenu menu, NamedImage previous) {
		this.menu = menu;
		this.previous = previous;
	}

	@Override
	protected void addComponents() {
		// TODO Auto-generated method stub

	}
}