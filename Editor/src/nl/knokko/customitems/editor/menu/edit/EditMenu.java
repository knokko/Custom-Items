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
package nl.knokko.customitems.editor.menu.edit;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.editor.HelpButtons;
import nl.knokko.customitems.editor.menu.edit.drops.DropsMenu;
import nl.knokko.customitems.editor.menu.edit.item.ItemCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.projectile.ProjectileMenu;
import nl.knokko.customitems.editor.menu.edit.recipe.RecipeCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.texture.TextureCollectionEdit;
import nl.knokko.customitems.editor.menu.main.MainMenu;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditMenu extends GuiMenu {
	
	protected final ItemSet set;
	
	protected final DynamicTextComponent errorComponent;
	
	protected final GuiComponent itemOverview;
	protected final GuiComponent textureOverview;
	protected final GuiComponent recipeOverview;
	protected final GuiComponent dropsMenu;
	protected final ProjectileMenu projectileMenu;

	public EditMenu(ItemSet set) {
		this.set = set;
		itemOverview = new ItemCollectionEdit(this);
		textureOverview = new TextureCollectionEdit(this);
		recipeOverview = new RecipeCollectionEdit(this);
		dropsMenu = new DropsMenu(this);
		projectileMenu = new ProjectileMenu(this);
		
		errorComponent = new DynamicTextComponent("", EditProps.ERROR);
	}
	
	@Override
	public void init() {
		super.init();
		errorComponent.setText("");
	}
	
	public ItemSet getSet() {
		return set;
	}
	
	public GuiComponent getItemOverview() {
		return itemOverview;
	}
	
	public GuiComponent getTextureOverview() {
		return textureOverview;
	}
	
	public GuiComponent getRecipeOverview() {
		return recipeOverview;
	}
	
	public GuiComponent getDropsMenu() {
		return dropsMenu;
	}
	
	public ProjectileMenu getProjectileMenu() {
		return projectileMenu;
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	protected void setError(String error) {
		errorComponent.setProperties(EditProps.ERROR);
		errorComponent.setText(error);
	}
	
	protected void setInfo(String info) {
		errorComponent.setProperties(EditProps.LABEL);
		errorComponent.setText(info);
	}

	@Override
	protected void addComponents() {
		addComponent(errorComponent, 0.305f, 0.9f, 0.95f, 1f);
		addComponent(new DynamicTextButton("Quit", EditProps.QUIT_BASE, EditProps.QUIT_HOVER, () -> {
			state.getWindow().setMainComponent(MainMenu.INSTANCE);
		}), 0.1f, 0.88f, 0.3f, 0.98f);
		addComponent(new DynamicTextButton("Save", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			String error = set.save();
			if (error != null)
				setError(error);
			else
				setInfo("Saved successfully");
		}), 0.1f, 0.64f, 0.25f, 0.74f);
		addComponent(new DynamicTextButton("Save and quit", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			String error = set.save();
			if(error != null)
				setError(error);
			else
				state.getWindow().setMainComponent(MainMenu.INSTANCE);
		}), 0.1f, 0.52f, 0.35f, 0.62f);
		addComponent(new DynamicTextButton("Export for 1.12", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			String error = set.save();
			if(error != null)
				setError(error);
			else {
				error = set.exportFor12(MCVersions.VERSION1_12);
				if(error != null)
					setError(error);
				else
					state.getWindow().setMainComponent(new AfterExportMenu(this));
			}
		}), 0.1f, 0.4f, 0.35f, 0.5f);
		addComponent(new DynamicTextButton("Export for 1.13", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			String error = set.save();
			if(error != null)
				setError(error);
			else {
				error = set.exportFor13Or14(MCVersions.VERSION1_13);
				if(error != null)
					setError(error);
				else
					state.getWindow().setMainComponent(new AfterExportMenu(this));
			}
		}), 0.1f, 0.27f, 0.35f, 0.37f);
		addComponent(new DynamicTextButton("Export for 1.14", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			String error = set.save();
			if(error != null)
				setError(error);
			else {
				error = set.exportFor13Or14(MCVersions.VERSION1_14);
				if(error != null)
					setError(error);
				else
					state.getWindow().setMainComponent(new AfterExportMenu(this));
			}
		}), 0.1f, 0.14f, 0.35f, 0.24f);
		addComponent(new DynamicTextButton("Export for 1.15", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			String error = set.save();
			if(error != null)
				setError(error);
			else {
				error = set.exportFor15();
				if(error != null)
					setError(error);
				else
					state.getWindow().setMainComponent(new AfterExportMenu(this));
			}
		}), 0.1f, 0.02f, 0.35f, 0.12f);
		addComponent(new DynamicTextButton("Textures", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(textureOverview);
		}), 0.6f, 0.75f, 0.8f, 0.85f);
		addComponent(new DynamicTextButton("Items", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(itemOverview);
		}), 0.6f, 0.6f, 0.8f, 0.7f);
		addComponent(new DynamicTextButton("Recipes", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(recipeOverview);
		}), 0.6f, 0.45f, 0.8f, 0.55f);
		addComponent(new DynamicTextButton("Drops", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(dropsMenu);
		}), 0.6f, 0.3f, 0.8f, 0.4f);
		addComponent(new DynamicTextButton("Projectiles", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(projectileMenu);
		}), 0.6f, 0.15f, 0.875f, 0.25f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/index.html");
	}
}