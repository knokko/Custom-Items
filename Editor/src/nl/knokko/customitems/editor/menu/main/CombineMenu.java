package nl.knokko.customitems.editor.menu.main;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

import nl.knokko.customitems.editor.Editor;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.item.NamedImage;
import nl.knokko.customitems.editor.set.recipe.Recipe;
import nl.knokko.customitems.editor.set.recipe.ShapedRecipe;
import nl.knokko.customitems.editor.set.recipe.ShapelessRecipe;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.util.bits.ByteArrayBitInput;

public class CombineMenu extends GuiMenu {
	
	private static CombineMenu instance;
	
	public static CombineMenu getInstance() {
		if (instance == null) {
			instance = new CombineMenu();
		}
		return instance;
	}
	
	private CombineMenu() {
		
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(MainMenu.INSTANCE);
		}), 0.025f, 0.8f, 0.2f, 0.9f);
		addComponent(new DynamicTextButton("Continue", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectSets());
		}), 0.025f, 0.1f, 0.2f, 0.2f);
		
		addComponent(new DynamicTextComponent("You can combine 2 item sets into a single item set that will have the content of both item sets.", EditProps.LABEL), 0f, 0.7f, 0.95f, 0.8f);
		addComponent(new DynamicTextComponent("You will need to select a primary item set and a secundary item set.", EditProps.LABEL), 0f, 0.6f, 0.65f, 0.7f);
		addComponent(new DynamicTextComponent("If you have used any of the item sets on your server before, you must use that set as primary item set.", EditProps.ERROR), 0f, 0.5f, 0.98f, 0.6f);
		addComponent(new DynamicTextComponent("If you haven't used any of the item sets on your server yet, it doesn't matter which one is the primary.", EditProps.LABEL), 0f, 0.4f, 0.98f, 0.5f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	private static class SelectSets extends GuiMenu {
		
		@Override
		protected void addComponents() {
			addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
				state.getWindow().setMainComponent(CombineMenu.getInstance());
			}), 0.025f, 0.8f, 0.2f, 0.9f);
			
			TextEditField primary = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			TextEditField secundary = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			TextEditField combined = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			
			addComponent(new DynamicTextComponent("Name of primary item set:", EditProps.LABEL), 0.2f, 0.6f, 0.45f, 0.7f);
			addComponent(primary, 0.5f, 0.6f, 0.7f, 0.7f);
			addComponent(new DynamicTextComponent("Name of secundary item set:", EditProps.LABEL), 0.2f, 0.45f, 0.47f, 0.55f);
			addComponent(secundary, 0.5f, 0.45f, 0.7f, 0.55f);
			
			addComponent(new DynamicTextComponent("Name of the new item set:", EditProps.LABEL), 0.2f, 0.2f, 0.45f, 0.3f);
			addComponent(combined, 0.5f, 0.2f, 0.7f, 0.3f);
			
			DynamicTextComponent errorComponent = new DynamicTextComponent("", EditProps.ERROR);
			addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
			
			addComponent(new DynamicTextButton("Combine", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				File primaryFile = new File(Editor.getFolder() + "/" + primary.getText() + ".cisb");
				if (!primaryFile.exists()) {
					errorComponent.setText("Can't find file " + primaryFile);
					return;
				}
				if (primaryFile.length() > Integer.MAX_VALUE) {
					errorComponent.setText("File " + primaryFile + " is too large");
					return;
				}
				
				File secundaryFile = new File(Editor.getFolder() + "/" + secundary.getText() + ".cisb");
				if (!secundaryFile.exists()) {
					errorComponent.setText("Can't find file " + secundaryFile);
					return;
				}
				if (secundaryFile.length() > Integer.MAX_VALUE) {
					errorComponent.setText("File " + secundaryFile + " is too large");
					return;
				}
				
				File destFile = new File(Editor.getFolder() + "/" + combined.getText() + ".cisb");
				if (destFile.exists()) {
					errorComponent.setText("There is already an item set with name " + combined.getText());
					return;
				}
				
				ItemSet primarySet;
				try {
					byte[] primaryBytes = new byte[(int) primaryFile.length()];
					DataInputStream input = new DataInputStream(Files.newInputStream(primaryFile.toPath()));
					input.readFully(primaryBytes);
					input.close();
					try {
						primarySet = new ItemSet(combined.getText(), new ByteArrayBitInput(primaryBytes));
					} catch (Exception ex) {
						errorComponent.setText("Error in primary item set: " + ex.getMessage());
						return;
					}
				} catch (IOException io) {
					errorComponent.setText("Couldn't open file " + primaryFile + ": " + io.getMessage());
					return;
				}
				
				ItemSet secundarySet;
				try {
					byte[] secundaryBytes = new byte[(int) secundaryFile.length()];
					DataInputStream input = new DataInputStream(Files.newInputStream(secundaryFile.toPath()));
					input.readFully(secundaryBytes);
					input.close();
					try {
						secundarySet = new ItemSet(secundary.getText(), new ByteArrayBitInput(secundaryBytes));
					} catch (Exception ex) {
						errorComponent.setText("Error in secundary item set: " + ex.getMessage());
						return;
					}
				} catch (IOException io) {
					errorComponent.setText("Couldn't open file " + secundaryFile + ": " + io.getMessage());
					return;
				}
				
				Collection<NamedImage> primaryTextures = primarySet.getBackingTextures();
				Collection<NamedImage> secundaryTextures = secundarySet.getBackingTextures();
				for (NamedImage texture : secundaryTextures) {
					
					// Do not tolerate multiple textures with the same name
					if (primarySet.hasTexture(texture.getName())) {
						errorComponent.setText("Both item sets have a texture with name " + texture.getName());
						return;
					}
					primaryTextures.add(texture);
				}
				
				TypeDamageMap typeDamageMap = new TypeDamageMap();
				Collection<CustomItem> primaryItems = primarySet.getBackingItems();
				Collection<CustomItem> secundaryItems = secundarySet.getBackingItems();
				
				for (CustomItem item : primaryItems) {
					typeDamageMap.set(item.getItemType(), item.getItemDamage());
				}
				
				for (CustomItem item : secundaryItems) {
					
					// Do not tolerate multiple items with the same name
					String name = item.getName();
					if (primarySet.hasCustomItem(item.getName())) {
						errorComponent.setText("Both item sets have an item with name " + name);
						return;
					}
					
					// If there will be multiple items with the same internal item type and damage
					// we will try to find an internal item damage that will not cause conflicts.
					short newDamage = item.getItemDamage();
					CustomItemType type = item.getItemType();
					if (typeDamageMap.has(type, newDamage)) {
						newDamage = typeDamageMap.find(type);
						if (newDamage == 0) {
							errorComponent.setText("There are too many items with internal item type " + type);
							return;
						}
					}
					item.setItemDamage(newDamage);
					typeDamageMap.set(type, newDamage);
					primaryItems.add(item);
				}
				
				Collection<Recipe> secundaryRecipes = secundarySet.getBackingRecipes();
				for (Recipe recipe : secundaryRecipes) {
					
					// The add...Recipe methods of ItemSet are quite nice for these checks, so lets use them
					String error;
					if (recipe instanceof ShapedRecipe) {
						error = primarySet.addShapedRecipe(((ShapedRecipe) recipe).getIngredients(), recipe.getResult());
					} else {
						error = primarySet.addShapelessRecipe(((ShapelessRecipe) recipe).getIngredients(), recipe.getResult());
					}
					
					if (error != null) {
						errorComponent.setText("Recipe for " + recipe.getResult() + ": " + error);
						return;
					}
				}
				
				// Since drops can't conflict, we can simply do this
				primarySet.getBackingBlockDrops().addAll(secundarySet.getBackingBlockDrops());
				primarySet.getBackingMobDrops().addAll(secundarySet.getBackingMobDrops());
				
				// When we created the instance of primarySet, we already gave it the new name
				// So this should do the trick
				String error = primarySet.save();
				if (error != null) {
					errorComponent.setText("Error with saving the final set: " + error);
				} else {
					LoadMenu.INSTANCE.refresh();
					state.getWindow().setMainComponent(MainMenu.INSTANCE);
				}
			}), 0.025f, 0.1f, 0.2f, 0.2f);
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND;
		}
		
		private static class TypeDamageMap {
			
			private final boolean[][] has;
			
			private TypeDamageMap() {
				CustomItemType[] types = CustomItemType.values();
				has = new boolean[types.length][];
				for (CustomItemType type : types) {
					has[type.ordinal()] = new boolean[type.getMaxDurability()];
				}
			}
			
			private void set(CustomItemType type, short damage) {
				if (has(type, damage)) {
					
					// Just to protect programmer, should never happen in production
					throw new IllegalStateException("Already claimed");
				}
				
				has[type.ordinal()][damage - 1] = true;
			}
			
			private boolean has(CustomItemType type, short damage) {
				return has[type.ordinal()][damage - 1];
			}
			
			/**
			 * @return An available internal item damage, or 0 if there is no available
			 */
			private short find(CustomItemType type) {
				boolean[] damages = has[type.ordinal()];
				short length = (short) damages.length;
				for (short index = 0; index < length; index++) {
					if (!damages[index]) {
						index++;
						return index;
					}
				}
				return 0;
			}
		}
	}
}
