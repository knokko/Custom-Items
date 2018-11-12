/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2018 knokko
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
package nl.knokko.customitems.editor.menu.edit;

import java.awt.Color;

import nl.knokko.customitems.editor.menu.edit.item.ItemOverview;
import nl.knokko.customitems.editor.menu.edit.recipe.RecipeOverview;
import nl.knokko.customitems.editor.menu.edit.texture.TextureOverview;
import nl.knokko.customitems.editor.menu.main.MainMenu;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;
import nl.knokko.gui.util.TextBuilder.Properties;

public class EditMenu extends GuiMenu {
	
	protected final ItemSet set;
	
	protected final TextComponent errorComponent;
	
	protected final ItemOverview itemOverview;
	protected final TextureOverview textureOverview;
	protected final RecipeOverview recipeOverview;

	public EditMenu(ItemSet set) {
		this.set = set;
		itemOverview = new ItemOverview(this);
		textureOverview = new TextureOverview(this);
		recipeOverview = new RecipeOverview(this);
		errorComponent = new TextComponent("", EditProps.ERROR);
	}
	
	public ItemSet getSet() {
		return set;
	}
	
	public ItemOverview getItemOverview() {
		return itemOverview;
	}
	
	public TextureOverview getTextureOverview() {
		return textureOverview;
	}
	
	public RecipeOverview getRecipeOverview() {
		return recipeOverview;
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
		addComponent(new TextButton("Quit", Properties.createButton(new Color(200, 0, 0), new Color(50, 0, 0)), Properties.createButton(new Color(250, 0, 0), new Color(65, 0, 0)), () -> {
			state.getWindow().setMainComponent(MainMenu.INSTANCE);
		}), 0.1f, 0.7f, 0.3f, 0.8f);
		addComponent(new TextButton("Save", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			String error = set.save();
			errorComponent.setText(error != null ? error : "");
		}), 0.1f, 0.4f, 0.25f, 0.5f);
		addComponent(new TextButton("Save and quit", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			String error = set.save();
			if(error != null)
				errorComponent.setText(error);
			else
				state.getWindow().setMainComponent(MainMenu.INSTANCE);
		}), 0.1f, 0.25f, 0.35f, 0.35f);
		addComponent(new TextButton("Export", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			String error = set.save();
			if(error != null)
				errorComponent.setText(error);
			else {
				error = set.export();
				if(error != null)
					errorComponent.setText(error);
				else
					state.getWindow().setMainComponent(MainMenu.INSTANCE);
			}
		}), 0.1f, 0.05f, 0.25f, 0.15f);
		addComponent(new TextButton("Textures", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(textureOverview);
		}), 0.6f, 0.75f, 0.8f, 0.85f);
		addComponent(new TextButton("Items", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(itemOverview);
		}), 0.6f, 0.6f, 0.8f, 0.7f);
		addComponent(new TextButton("Recipes", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(recipeOverview);
		}), 0.6f, 0.45f, 0.8f, 0.55f);
	}
}