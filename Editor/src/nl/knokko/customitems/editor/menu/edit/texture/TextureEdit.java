package nl.knokko.customitems.editor.menu.edit.texture;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.NamedImage;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.menu.FileChooserMenu;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.simple.SimpleImageComponent;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.util.TextBuilder.Properties;

public class TextureEdit extends GuiMenu {
	
	protected final EditMenu menu;
	protected final NamedImage previous;
	protected final TextComponent errorComponent;
	
	protected TextEditField name;
	protected BufferedImage image;
	protected WrapperComponent<SimpleImageComponent> wrapper;

	public TextureEdit(EditMenu menu, NamedImage previous) {
		this.menu = menu;
		this.previous = previous;
		errorComponent = new TextComponent("", Properties.createLabel(Color.RED));
		wrapper = new WrapperComponent<SimpleImageComponent>(null);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return SimpleGuiColor.GREEN;
	}

	@Override
	protected void addComponents() {
		addComponent(errorComponent, 0.1f, 0.9f, 0.9f, 1f);
		addComponent(new TextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_ACTIVE, () -> {
			state.getWindow().setMainComponent(menu.getTextureOverview());
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		if(previous != null) {
			name = new TextEditField(previous.getName(), EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			image = previous.getImage();
			wrapper.setComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(image)));
		}
		else {
			name = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		}
		addComponent(new TextComponent("Name: ", EditProps.LABEL), 0.4f, 0.6f, 0.55f, 0.7f);
		addComponent(name, 0.6f, 0.6f, 0.9f, 0.7f);
		addComponent(new TextComponent("Texture: ", EditProps.LABEL), 0.4f, 0.4f, 0.55f, 0.5f);
		addComponent(wrapper, 0.6f, 0.4f, 0.7f, 0.5f);
		addComponent(createImageSelect(), 0.75f, 0.4f, 0.9f, 0.5f);
	}
	
	private TextButton createImageSelect() {
		return new TextButton("Edit...", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new FileChooserMenu(TextureEdit.this, (File file) -> {
				try {
					BufferedImage loaded = ImageIO.read(file);
					if(loaded != null) {
						image = loaded;
						wrapper.setComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(image)));
					}
					else {
						errorComponent.setText("This image can't be read.");
					}
				} catch(IOException ioex) {
					errorComponent.setText("IO error: " + ioex.getMessage());
				}
			}, (File file) -> {
				return file.getName().endsWith(".png");
			}));
		});
	}
}