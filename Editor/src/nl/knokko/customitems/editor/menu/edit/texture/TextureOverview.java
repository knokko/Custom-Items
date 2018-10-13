package nl.knokko.customitems.editor.menu.edit.texture;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;

public class TextureOverview extends GuiMenu {
	
	protected final EditMenu menu;
	protected final TextureList textureList;

	public TextureOverview(EditMenu menu) {
		this.menu = menu;
		textureList = new TextureList();
	}
	
	@Override
	public void init() {
		if(didInit) textureList.refresh();
		super.init();
	}

	@Override
	protected void addComponents() {
		addComponent(textureList, 0.3f, 0f, 1f, 1f);
		addComponent(new TextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_ACTIVE, () -> {
			state.getWindow().setMainComponent(menu);
		}), 0.05f, 0.7f, 0.2f, 0.8f);
		addComponent(new TextButton("Create texture", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new TextureEdit(menu, null));
		}), 0.05f, 0.4f, 0.25f, 0.5f);
	}
	
	private class TextureList extends GuiMenu {

		@Override
		protected void addComponents() {
			refresh();
		}
		
		private void refresh() {
			
		}
	}
}