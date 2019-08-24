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
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.main.CreateMenu;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.NamedImage;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.menu.FileChooserMenu;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.component.text.TextEditField;

public class TextureEdit extends GuiMenu {
	
	protected final ItemSet itemSet;
	protected final GuiComponent returnMenu;
	protected final NamedImage previous;
	protected final DynamicTextComponent errorComponent;
	
	protected TextEditField name;
	protected BufferedImage image;
	protected WrapperComponent<SimpleImageComponent> wrapper;
	
	public TextureEdit(EditMenu menu, NamedImage previous) {
		this(menu.getSet(), menu.getTextureOverview(), previous);
	}

	public TextureEdit(ItemSet set, GuiComponent returnMenu, NamedImage previous) {
		this.itemSet = set;
		this.returnMenu = returnMenu;
		this.previous = previous;
		errorComponent = new DynamicTextComponent("", EditProps.ERROR);
		wrapper = new WrapperComponent<SimpleImageComponent>(null);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		addComponent(errorComponent, 0.1f, 0.9f, 0.9f, 1f);
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		if(previous != null) {
			name = new TextEditField(previous.getName(), EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			image = previous.getImage();
			wrapper.setComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(image)));
		}
		else {
			name = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		}
		addComponent(new DynamicTextComponent("Name: ", EditProps.LABEL), 0.4f, 0.6f, 0.55f, 0.7f);
		addComponent(name, 0.6f, 0.6f, 0.9f, 0.7f);
		addComponent(new DynamicTextComponent("Texture: ", EditProps.LABEL), 0.4f, 0.4f, 0.55f, 0.5f);
		addComponent(wrapper, 0.6f, 0.4f, 0.7f, 0.5f);
		addComponent(createImageSelect(), 0.75f, 0.4f, 0.9f, 0.5f);
		addComponent(new DynamicTextButton(previous != null ? "Apply" : "Create", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			if(image != null) {
				String error = CreateMenu.testFileName(name.getText() + ".png");
				if(error != null)
					errorComponent.setText(error);
				else {
					if(previous != null) {
						error = itemSet.changeTexture(previous, name.getText(), image, true);
						if(error != null)
							errorComponent.setText(error);
						else
							state.getWindow().setMainComponent(returnMenu);
					}
					else {
						error = itemSet.addTexture(new NamedImage(name.getText(), image), true);
						if(error != null)
							errorComponent.setText(error);
						else
							state.getWindow().setMainComponent(returnMenu);
					}
				}
			} else
				errorComponent.setText("You have to select an image before you can create this.");
		}), 0.4f, 0.3f, 0.6f, 0.4f);
	}
	
	public static DynamicTextButton createImageSelect(ImageListener listener, DynamicTextComponent errorComponent, GuiComponent returnMenu) {
		return new DynamicTextButton("Edit...", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			returnMenu.getState().getWindow().setMainComponent(new FileChooserMenu(returnMenu, (File file) -> {
				try {
					BufferedImage loaded = ImageIO.read(file);
					if(loaded != null) {
						int width = loaded.getWidth();
						if(width == loaded.getHeight()) {
							if(width >= 16) {
								if(width <= 512) {
									if(width == 16 || width == 32 || width == 64 || width == 128 || width == 256 || width == 512) {
										listener.listen(loaded);
									} else
										errorComponent.setText("The width and height (" + width + ") should be a power of 2");
								} else
									errorComponent.setText("The image should be at most 512 x 512 pixels.");
							} else
								errorComponent.setText("The image should be at least 16 x 16 pixels.");
						} else
							errorComponent.setText("The width (" + loaded.getWidth() + ") of this image should be equal to the height (" + loaded.getHeight() + ")");
					} else
						errorComponent.setText("This image can't be read.");
				} catch(IOException ioex) {
					errorComponent.setText("IO error: " + ioex.getMessage());
				}
			}, (File file) -> {
				return file.getName().endsWith(".png");
			}, EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, 
					EditProps.BACKGROUND, EditProps.BACKGROUND2));
		});
	}
	
	private DynamicTextButton createImageSelect() {
		return createImageSelect((BufferedImage loaded) -> {
			image = loaded;
			wrapper.setComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(image)));
		}, errorComponent, this);
	}
	
	public static interface ImageListener {
		
		void listen(BufferedImage image);
	}
}