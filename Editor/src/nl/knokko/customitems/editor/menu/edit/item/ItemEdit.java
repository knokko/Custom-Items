package nl.knokko.customitems.editor.menu.edit.item;

import java.awt.Color;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.CustomItem;
import nl.knokko.customitems.editor.set.NamedImage;
import nl.knokko.customitems.item.ItemType;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.menu.TextArrayEditMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.util.TextBuilder.Properties;

public class ItemEdit extends GuiMenu {
	
	protected final EditMenu menu;
	protected final CustomItem previous;
	
	protected TextEditField name;
	protected ItemTypeSelect internalType;
	protected TextEditField internalDamage;
	protected TextEditField displayName;
	protected String[] lore;
	protected TextureSelect textureSelect;
	protected TextComponent errorComponent;

	public ItemEdit(EditMenu menu, CustomItem current) {
		this.menu = menu;
		previous = current;
		if(current != null)
			lore = current.getLore();
		else
			lore = new String[] {"First line", "Second line"};
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		errorComponent = new TextComponent("", Properties.createLabel(Color.RED));
		addComponent(new TextButton("Cancel", Properties.createButton(new Color(200, 0, 0), new Color(50, 0, 0)), Properties.createButton(new Color(250, 0, 0), new Color(65, 0, 0)), () -> {
			state.getWindow().setMainComponent(menu.getItemOverview());
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		addComponent(new TextComponent("Name: ", EditProps.LABEL), 0.35f, 0.75f, 0.5f, 0.85f);
		addComponent(new TextComponent("Internal item type: ", EditProps.LABEL), 0.35f, 0.6f, 0.6f, 0.7f);
		addComponent(new TextComponent("Internal item damage: ", EditProps.LABEL), 0.35f, 0.45f, 0.6f, 0.55f);
		addComponent(new TextComponent("Display name: ", EditProps.LABEL), 0.35f, 0.3f, 0.55f, 0.4f);
		addComponent(new TextComponent("Lore: ", EditProps.LABEL), 0.35f, 0.15f, 0.45f, 0.25f);
		if(previous != null) {
			name = new TextEditField(previous.getName(), EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			internalType = new ItemTypeSelect(previous.getItemType());
			internalDamage = new TextEditField(Short.toString(previous.getItemDamage()), EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			displayName = new TextEditField(previous.getDisplayName(), EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			textureSelect = new TextureSelect(previous.getTexture());
			addComponent(new TextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				String error = null;
				try {
					short damage = Short.parseShort(internalDamage.getText());
					if(damage > 0 && damage < internalType.currentType.getMaxDurability()) {
						error = menu.getSet().changeItem(previous, internalType.currentType, damage, name.getText(), displayName.getText(), lore, textureSelect.currentTexture);
					}
					else {
						error = "The internal item damage must be larger than 0 and smaller than " + internalType.currentType.getMaxDurability();
					}
				} catch(NumberFormatException nfe) {
					error = "The internal item damage must be an integer.";
				}
				if(error != null)
					errorComponent.setText(error);
				else
					state.getWindow().setMainComponent(menu.getItemOverview());
			}), 0.1f, 0.1f, 0.25f, 0.2f);
		}
		else {
			name = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			internalType = new ItemTypeSelect(ItemType.DIAMOND_HOE);
			internalDamage = new TextEditField(Short.toString(menu.getSet().nextAvailableDamage(internalType.currentType)), EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			displayName = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			textureSelect = new TextureSelect(null);
			addComponent(new TextButton("Create", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				String error = null;
				try {
					short damage = Short.parseShort(internalDamage.getText());
					if(damage > 0 && damage < internalType.currentType.getMaxDurability()) {
						error = menu.getSet().addItem(new CustomItem(internalType.currentType, damage, name.getText(), displayName.getText(), lore, textureSelect.currentTexture));
						if(error == null) {
							state.getWindow().setMainComponent(menu.getItemOverview());
						}
					}
					else {
						error = "The internal item damage must be larger than 0 and smaller than " + internalType.currentType.getMaxDurability();
					}
				} catch(NumberFormatException nfe) {
					error = "The internal item damage must be an integer.";
				}
				if(error != null)
					errorComponent.setText(error);
				else
					state.getWindow().setMainComponent(menu.getItemOverview());
			}), 0.1f, 0.1f, 0.25f, 0.2f);
		}
		addComponent(errorComponent, 0.1f, 0.9f, 0.9f, 1f);
		addComponent(name, 0.65f, 0.8f, 0.85f, 0.9f);
		addComponent(internalType, 0.65f, 0.65f, 0.85f, 0.75f);
		addComponent(internalDamage, 0.65f, 0.5f, 0.85f, 0.6f);
		addComponent(displayName, 0.65f, 0.35f, 0.85f, 0.45f);
		addLoreComponent();
		addComponent(textureSelect, 0.65f, 0.05f, 0.85f, 0.15f);
	}
	
	private class ItemTypeSelect extends TextButton {
		
		private ItemType currentType;

		public ItemTypeSelect(ItemType initial) {
			super(initial.toString(), EditProps.SAVE_BASE, EditProps.SELECT_HOVER, null);
			currentType = initial;
		}
		
		@Override
		public void init() {
			super.init();
			clickAction = () -> {
				state.getWindow().setMainComponent(new SelectItemType(ItemEdit.this, (ItemType type) -> {
					currentType = type;
					setText(type.toString());
				}));
			};
		}
	}
	
	private class TextureSelect extends TextButton {
		
		private NamedImage currentTexture;
		
		private TextureSelect(NamedImage initial) {
			super(initial != null ? initial.getName() : "None", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, null);
			currentTexture = initial;
		}
		
		@Override
		public void init() {
			super.init();
			clickAction = () -> {
				state.getWindow().setMainComponent(new SelectTexture(menu.getSet(), ItemEdit.this, (NamedImage texture) -> {
					currentTexture = texture;
					setText(texture.getName());
				}));
			};
		}
	}
	
	private void addLoreComponent() {
		addComponent(new TextButton("Change lore...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new TextArrayEditMenu(ItemEdit.this, (String[] newLore) -> {
				lore = newLore;
			}, EditProps.CANCEL_BASE, EditProps.CANCEL_ACTIVE, EditProps.SAVE_BASE, EditProps.SAVE_HOVER, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE, lore));
		}), 0.65f, 0.25f, 0.85f, 0.35f);
	}
}