package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.item.attribute.AttributesOverview;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.item.NamedImage;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.menu.TextArrayEditMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;
import nl.knokko.gui.component.text.TextEditField;

public abstract class EditItemBase extends GuiMenu {
	
	private static final float LABEL_X = 0.2f;
	private static final float BUTTON_X = 0.5f;
	
	private static final AttributeModifier[] DEFAULT_ATTRIBUTES = {};
	
	protected final EditMenu menu;
	
	protected TextEditField name;
	protected ItemTypeSelect internalType;
	protected TextEditField internalDamage;
	protected TextEditField displayName;
	protected String[] lore;
	protected AttributeModifier[] attributes;
	protected TextureSelect textureSelect;
	protected TextComponent errorComponent;

	public EditItemBase(EditMenu menu, CustomItem previous) {
		this.menu = menu;
		if(previous != null) {
			name = new TextEditField(previous.getName(), EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			internalType = new ItemTypeSelect(previous.getItemType());
			internalDamage = new TextEditField(Short.toString(previous.getItemDamage()), EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			displayName = new TextEditField(previous.getDisplayName(), EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			textureSelect = new TextureSelect(previous.getTexture());
			lore = previous.getLore();
			attributes = previous.getAttributes();
		} else {
			name = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			internalType = new ItemTypeSelect(CustomItemType.DIAMOND_HOE);
			internalDamage = new TextEditField(Short.toString(menu.getSet().nextAvailableDamage(internalType.currentType)), EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			displayName = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			textureSelect = new TextureSelect(null);
			lore = new String[] {"First line", "Second line"};
			attributes = DEFAULT_ATTRIBUTES;
		}
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		errorComponent = new TextComponent("", EditProps.ERROR);
		addComponent(new TextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(menu.getItemOverview());
		}), 0.025f, 0.7f, 0.15f, 0.8f);
		addComponent(new TextComponent("Name: ", EditProps.LABEL), LABEL_X, 0.8f, LABEL_X + 0.15f, 0.9f);
		addComponent(new TextComponent("Internal item type: ", EditProps.LABEL), LABEL_X, 0.675f, LABEL_X + 0.25f, 0.775f);
		addComponent(new TextComponent("Internal item damage: ", EditProps.LABEL), LABEL_X, 0.55f, LABEL_X + 0.25f, 0.65f);
		addComponent(new TextComponent("Display name: ", EditProps.LABEL), LABEL_X, 0.425f, LABEL_X + 0.2f, 0.525f);
		addComponent(new TextComponent("Lore: ", EditProps.LABEL), LABEL_X, 0.3f, LABEL_X + 0.1f, 0.4f);
		addComponent(new TextComponent("Attribute modifiers: ", EditProps.LABEL), LABEL_X, 0.175f, LABEL_X + 0.25f, 0.275f);
		addComponent(new TextComponent("Texture: ", EditProps.LABEL), LABEL_X, 0.05f, LABEL_X + 0.15f, 0.15f);
		if(previous() != null) {
			addComponent(new TextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				String error = apply();
				if(error != null)
					errorComponent.setText(error);
				else
					state.getWindow().setMainComponent(menu.getItemOverview());
			}), 0.025f, 0.1f, 0.15f, 0.2f);
		}
		else {
			addComponent(new TextButton("Create", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				String error = create();
				if(error != null)
					errorComponent.setText(error);
				else
					state.getWindow().setMainComponent(menu.getItemOverview());
			}), 0.025f, 0.1f, 0.15f, 0.2f);
		}
		addComponent(errorComponent, 0.1f, 0.9f, 0.9f, 1f);
		addComponent(name, BUTTON_X, 0.8f, BUTTON_X + 0.2f, 0.9f);
		addComponent(internalType, BUTTON_X, 0.675f, BUTTON_X + 0.2f, 0.775f);
		addComponent(internalDamage, BUTTON_X, 0.55f, BUTTON_X + 0.2f, 0.65f);
		addComponent(displayName, BUTTON_X, 0.425f, BUTTON_X + 0.2f, 0.525f);
		addLoreComponent();
		addAttributesComponent();
		addComponent(textureSelect, BUTTON_X, 0.05f, BUTTON_X + 0.2f, 0.15f);
	}
	
	protected abstract String create();
	
	protected abstract String apply();
	
	protected abstract CustomItem previous();
	
	protected class ItemTypeSelect extends TextButton {
		
		protected CustomItemType currentType;

		public ItemTypeSelect(CustomItemType initial) {
			super(initial.toString(), EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, null);
			currentType = initial;
		}
		
		@Override
		public void init() {
			super.init();
			clickAction = () -> {
				state.getWindow().setMainComponent(new SelectItemType(EditItemBase.this, (CustomItemType type) -> {
					currentType = type;
					setText(type.toString());
				}, getCategory()));
			};
		}
	}
	
	protected class TextureSelect extends TextButton {
		
		protected NamedImage currentTexture;
		
		private TextureSelect(NamedImage initial) {
			super(initial != null ? initial.getName() : "None", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, null);
			currentTexture = initial;
		}
		
		@Override
		public void init() {
			super.init();
			clickAction = () -> {
				state.getWindow().setMainComponent(new SelectTexture(menu.getSet(), EditItemBase.this, (NamedImage texture) -> {
					currentTexture = texture;
					setText(texture.getName());
				}));
			};
		}
	}
	
	private void addLoreComponent() {
		addComponent(new TextButton("Change lore...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new TextArrayEditMenu(EditItemBase.this, (String[] newLore) -> {
				lore = newLore;
			}, EditProps.BACKGROUND, EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, EditProps.SAVE_BASE, EditProps.SAVE_HOVER, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE, lore));
		}), BUTTON_X, 0.3f, BUTTON_X + 0.2f, 0.4f);
	}
	
	private void addAttributesComponent() {
		addComponent(new TextButton("Change attributes...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new AttributesOverview(attributes, EditItemBase.this, (AttributeModifier[] attributes) -> {
				this.attributes = attributes;
			}));
		}), BUTTON_X, 0.175f, BUTTON_X + 0.2f, 0.275f);
	}
	
	protected abstract CustomItemType.Category getCategory();
}