package nl.knokko.customitems.editor.menu.edit;

import java.util.Collection;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.simple.SimpleColorComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static java.lang.Math.min;

import java.awt.image.BufferedImage;

public class CollectionEdit<T> extends GuiMenu {
	
	private final ActionHandler<T> handler;
	private final Collection<T> changingCollection;
	
	private final List itemList;
	private final DynamicTextComponent errorComponent;

	public CollectionEdit(ActionHandler<T> handler, Collection<T> collectionToModify) {
		this.handler = handler;
		this.changingCollection = collectionToModify;
		this.itemList = new List();
		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			handler.goBack();
		}), 0.025f, 0.7f, 0.175f, 0.8f);
		
		addComponent(itemList, 0.3f, 0f, 1f, 0.9f);
		
		// This one is a bit of a hack, but works very well
		addComponent(new SimpleColorComponent(EditProps.BACKGROUND), 0.3f, 0.9f, 1f, 1f);
		
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	@Override
	public void init() {
		if(didInit) itemList.refresh();
		super.init();
	}
	
	private class List extends GuiMenu {

		@Override
		protected void addComponents() {
			int index = 0;
			for (T item : changingCollection) {
				float minY = 0.9f - index * 0.11f;
				float maxY = 1f - index * 0.11f;
				String label = handler.getLabel(item);
				BufferedImage image = handler.getImage(item);
				float minTextX, maxTextX;
				if (image == null) {
					minTextX = 0.025f;
				} else {
					addComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(image)), 0f, minY, 0.15f, maxY);
					minTextX = 0.175f;
				}
				maxTextX = min(0.5f, minTextX + 0.03f * label.length());
				addComponent(new DynamicTextComponent(label, EditProps.LABEL), minTextX, minY, maxTextX, maxY);
				addComponent(new DynamicTextButton("Edit", EditProps.BUTTON, EditProps.HOVER, () -> {
					state.getWindow().setMainComponent(handler.createEditMenu(item, CollectionEdit.this));
				}), 0.51f, minY, 0.62f, maxY);
				addComponent(new DynamicTextButton("Copy", EditProps.BUTTON, EditProps.HOVER, () -> {
					state.getWindow().setMainComponent(handler.createCopyMenu(item, CollectionEdit.this));
				}), 0.64f, minY, 0.76f, maxY);
				addComponent(new DynamicTextButton("Delete", EditProps.QUIT_BASE, EditProps.QUIT_HOVER, () -> {
					String error = handler.deleteItem(item);
					if (error == null) {
						refresh();
					} else {
						errorComponent.setText(error);
					}
				}), 0.775f, minY, 0.975f, maxY);
				index++;
			}
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND2;
		}
		
		private void refresh() {
			clearComponents();
			addComponents();
		}
	}
	
	public static interface ActionHandler<T> {
		
		void goBack();
		
		BufferedImage getImage(T item);
		
		String getLabel(T item);
		
		GuiComponent createEditMenu(T itemToEdit, GuiComponent returnMenu);
		
		GuiComponent createCopyMenu(T itemToCopy, GuiComponent returnMenu);
		
		String deleteItem(T itemToDelete);
	}
}
