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
package nl.knokko.customitems.editor.menu.edit.texture;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.item.texture.BowTextures;
import nl.knokko.customitems.editor.set.item.texture.BowTextures.Entry;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.texture.loader.GuiTextureLoader;

public class BowTextureEdit extends GuiMenu {

	protected final EditMenu menu;
	protected final PullTextures pullTextures;
	protected final DynamicTextComponent errorComponent;
	protected final WrapperComponent<SimpleImageComponent> defaultTexture;
	protected final TextEditField nameField;

	protected final BowTextures previous;
	
	protected BufferedImage defaultImage;
	protected final List<Entry> pulls;

	public BowTextureEdit(EditMenu menu, BowTextures previous) {
		this.menu = menu;
		this.previous = previous;
		pullTextures = new PullTextures();
		errorComponent = new DynamicTextComponent("", EditProps.ERROR);
		defaultTexture = new WrapperComponent<SimpleImageComponent>(null);
		nameField = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		if (previous != null) {
			defaultImage = previous.getImage();
			List<Entry> oldPulls = previous.getPullTextures();
			pulls = new ArrayList<Entry>(oldPulls.size());
			for (Entry oldPull : oldPulls) {
				pulls.add(new Entry(oldPull.getTexture(), oldPull.getPull()));
			}
		} else {
			pulls = new ArrayList<Entry>(3);
			pulls.add(new Entry(null, 0));
			pulls.add(new Entry(null, 0.65));
			pulls.add(new Entry(null, 0.9));
		}
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(menu.getTextureOverview());
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 0.975f);
		addComponent(pullTextures, 0.65f, 0.025f, 0.95f, 0.775f);
		addComponent(new DynamicTextComponent("Base texture: ", EditProps.LABEL), 0.2f, 0.55f, 0.4f, 0.65f);
		addComponent(TextureEdit.createImageSelect((BufferedImage texture) -> {
			defaultTexture.setComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(texture)));
			defaultImage = texture;
		}, errorComponent, this), 0.425f, 0.55f, 0.525f, 0.65f);
		addComponent(new DynamicTextComponent("Name: ", EditProps.LABEL), 0.2f, 0.4f, 0.325f, 0.5f);
		addComponent(nameField, 0.35f, 0.4f, 0.6f, 0.5f);
		if (previous != null) {
			defaultTexture.setComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(previous.getImage())));
			nameField.setText(previous.getName());
			addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				if (defaultImage == null) {
					errorComponent.setText("You need to give this bow a base texture.");
				} else {
					String error = menu.getSet().changeBowTexture(previous, nameField.getText(), defaultImage, pulls, true);
					if (error != null) {
						errorComponent.setText(error);
					} else {
						state.getWindow().setMainComponent(menu.getTextureOverview());
					}
				}
			}), 0.1f, 0.1f, 0.25f, 0.2f);
		} else {
			addComponent(new DynamicTextButton("Create", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				if (defaultImage == null) {
					errorComponent.setText("You need to give this bow a base texture.");
				} else {
					Entry[] entries = new Entry[pulls.size()];
					for (int index = 0; index < entries.length; index++) {
						entries[index] = pulls.get(index).clone();
					}
					String error = menu.getSet().addBowTexture(new BowTextures(nameField.getText(), defaultImage, entries), true);
					if (error != null) {
						errorComponent.setText(error);
					} else {
						state.getWindow().setMainComponent(menu.getTextureOverview());
					}
				}
			}), 0.1f, 0.1f, 0.25f, 0.2f);
		}
		addComponent(defaultTexture, 0.54f, 0.55f, 0.64f, 0.65f);
		addComponent(new DynamicTextButton("Add pull", EditProps.BUTTON, EditProps.HOVER, () -> {
			pulls.add(new Entry(null, 0.3));
			pullTextures.refreshPullComponents();
		}), 0.3f, 0.1f, 0.45f, 0.2f);
	}

	private class PullTextures extends GuiMenu {

		protected void refreshPullComponents() {
			clearComponents();
			pulls.sort((Entry a, Entry b) -> {
				if (a.getPull() < b.getPull())
					return -1;
				if (a.getPull() > b.getPull())
					return 1;
				return 0;
			});
			int index = 0;
			for (Entry pull : pulls) {
				addComponent(new PullTexture(pull), 0f, 0.9f - index * 0.125f, 1f, 1f - index * 0.125f);
				index++;
			}
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND;
		}

		@Override
		protected void addComponents() {
			refreshPullComponents();
		}
	}
	
	private static final GuiColor BACKGROUND = new SimpleGuiColor(100, 0, 200);
	
	private class PullTexture extends GuiMenu {
		
		private final Entry entry;
		
		private PullTexture(Entry entry) {
			this.entry = entry;
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return BACKGROUND;
		}

		@Override
		protected void addComponents() {
			addComponent(new DynamicTextComponent("Pull: ", EditProps.LABEL), 0.05f, 0.5f, 0.3f, 0.9f);
			addComponent(new TextEditField(entry.getPull() + "", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE) {
				
				@Override
				public void keyPressed(char key) {
					super.keyPressed(key);
					updatePull();
				}
				
				@Override
				public void keyPressed(int key) {
					super.keyPressed(key);
					updatePull();
				}
				
				private void updatePull() {
					try {
						entry.setPull(Double.parseDouble(text));
					} catch (NumberFormatException nfe) {
						// Ignore silently because the number might be invalid during typing
					}
				}
			}, 0.3f, 0.5f, 0.6f, 0.9f);
			GuiTextureLoader loader = state.getWindow().getTextureLoader();
			addComponent(new ImageButton(loader.loadTexture("nl/knokko/gui/images/icons/delete.png"), 
					loader.loadTexture("nl/knokko/gui/images/icons/delete_hover.png"), () -> {
				pulls.remove(entry);
				pullTextures.refreshPullComponents();
			}), 0.875f, 0.5f, 0.975f, 0.9f);
			addComponent(new DynamicTextComponent("Texture: ", EditProps.LABEL), 0.05f, 0.05f, 0.5f, 0.45f);
			WrapperComponent<SimpleImageComponent> imageWrapper;
			if (entry.getTexture() == null)
				imageWrapper = new WrapperComponent<SimpleImageComponent>(null);
			else
				imageWrapper = new WrapperComponent<SimpleImageComponent>(new SimpleImageComponent(loader.loadTexture(entry.getTexture())));
			addComponent(TextureEdit.createImageSelect((BufferedImage texture) -> {
				entry.setTexture(texture);
				imageWrapper.setComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(texture)));
			}, errorComponent, BowTextureEdit.this), 0.5f, 0.05f, 0.75f, 0.45f);
			addComponent(imageWrapper, 0.75f, 0.55f, 0.85f, 0.9f);
		}
	}
}