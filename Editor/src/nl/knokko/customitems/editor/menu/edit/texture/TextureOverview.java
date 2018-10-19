package nl.knokko.customitems.editor.menu.edit.texture;

import java.util.Collection;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.NamedImage;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;

public class TextureOverview extends GuiMenu {
	
	protected final EditMenu menu;
	protected final TextureList textureList;
	protected final TextComponent errorComponent;

	public TextureOverview(EditMenu menu) {
		this.menu = menu;
		textureList = new TextureList();
		errorComponent = new TextComponent("", EditProps.ERROR);
	}
	
	@Override
	public void init() {
		if(didInit) textureList.refresh();
		super.init();
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
		addComponent(textureList, 0.3f, 0f, 1f, 0.9f);
		addComponent(new TextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
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
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND;
		}
		
		private void refresh() {
			clearComponents();
			Collection<NamedImage> textures = menu.getSet().getTextures();
			int index = 0;
			for(NamedImage texture : textures) {
				addComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(texture.getImage())), 0f, 0.9f - index * 0.15f, 0.2f, 1f - index * 0.15f);
				addComponent(new TextComponent(texture.getName(), EditProps.LABEL), 0.25f, 0.9f - index * 0.15f, 0.55f, 1f - index * 0.15f);
				addComponent(new TextButton("Edit", EditProps.BUTTON, EditProps.HOVER, () -> {
					state.getWindow().setMainComponent(new TextureEdit(menu, texture));
				}), 0.6f, 0.9f - index * 0.15f, 0.7f, 1f - index * 0.15f);
				addComponent(new TextButton("Delete", EditProps.QUIT_BASE, EditProps.QUIT_HOVER, () -> {
					String error = menu.getSet().removeTexture(texture);
					if(error != null)
						errorComponent.setText(error);
					else
						refresh();
				}), 0.75f, 0.9f - index * 0.15f, 0.95f, 1f - index * 0.15f);
				index++;
			}
		}
	}
}