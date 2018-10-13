package nl.knokko.customitems.editor.menu.edit.item;

import java.util.Collection;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.NamedImage;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.text.TextButton;

public class SelectTexture extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final ReturnAction returnAction;
	private final ItemSet set;

	public SelectTexture(ItemSet set, GuiComponent returnMenu, ReturnAction returnAction) {
		this.returnMenu = returnMenu;
		this.returnAction = returnAction;
		this.set = set;
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		addComponent(new TextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_ACTIVE, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.1f, 0.6f, 0.3f, 0.7f);
		Collection<NamedImage> textures = set.getTextures();
		int index = 0;
		for(NamedImage texture : textures) {
			addComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(texture.getImage())), 0.4f, 0.9f - index * 0.15f, 0.5f, 1f - index * 0.15f);
			addComponent(new TextButton(texture.getName(), EditProps.SELECT_BASE, EditProps.SELECT_HOVER, () -> {
				returnAction.onSelect(texture);
				state.getWindow().setMainComponent(returnMenu);
			}), 0.55f, 0.9f - index * 0.15f, 0.85f, 1f - index * 0.15f);
			index++;
		}
	}
	
	public static interface ReturnAction {
		
		void onSelect(NamedImage texture);
	}
}