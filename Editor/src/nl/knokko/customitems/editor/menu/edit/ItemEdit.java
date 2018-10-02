package nl.knokko.customitems.editor.menu.edit;

import java.awt.Color;
import java.util.Arrays;

import nl.knokko.customitems.editor.set.CustomItem;
import nl.knokko.customitems.item.ItemType;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextArrayEditMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.util.TextBuilder.Properties;

public class ItemEdit extends GuiMenu {
	
	public static final Properties LABEL_PROPERTIES = Properties.createLabel();
	public static final Properties EDIT_PROPERTIES = Properties.createEdit(new Color(200, 200, 200), new Color(10, 30, 30));
	public static final Properties ACTIVE_EDIT_PROPERTIES = Properties.createEdit(new Color(255, 255, 255), new Color(100, 255, 255));
	
	public static final Properties SAVE_PROPERTIES = Properties.createButton(new Color(0, 200, 0), new Color(0, 50, 0));
	public static final Properties SAVE_HOVER_PROPERTIES = Properties.createButton(new Color(0, 250, 0), new Color(0, 65, 0));
	
	protected final EditMenu menu;
	protected final CustomItem previous;
	
	protected TextEditField name;
	protected ItemTypeSelect internalType;
	protected TextEditField internalDamage;
	protected TextEditField displayName;
	protected String[] lore;
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
		return SimpleGuiColor.GREEN;
	}

	@Override
	protected void addComponents() {
		errorComponent = new TextComponent("", Properties.createLabel(Color.RED));
		addComponent(new TextButton("Cancel", Properties.createButton(new Color(200, 0, 0), new Color(50, 0, 0)), Properties.createButton(new Color(250, 0, 0), new Color(65, 0, 0)), () -> {
			state.getWindow().setMainComponent(menu.itemOverview);
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		addComponent(new TextComponent("Name: ", LABEL_PROPERTIES), 0.35f, 0.75f, 0.5f, 0.85f);
		addComponent(new TextComponent("Internal item type: ", LABEL_PROPERTIES), 0.35f, 0.6f, 0.6f, 0.7f);
		addComponent(new TextComponent("Internal item damage: ", LABEL_PROPERTIES), 0.35f, 0.45f, 0.6f, 0.55f);
		addComponent(new TextComponent("Display name: ", LABEL_PROPERTIES), 0.35f, 0.3f, 0.55f, 0.4f);
		addComponent(new TextComponent("Lore: ", LABEL_PROPERTIES), 0.35f, 0.15f, 0.45f, 0.25f);
		if(previous != null) {
			name = new TextEditField(previous.getName(), EDIT_PROPERTIES, ACTIVE_EDIT_PROPERTIES);
			internalType = new ItemTypeSelect(previous.getItemType());
			internalDamage = new TextEditField(Short.toString(previous.getItemDamage()), EDIT_PROPERTIES, ACTIVE_EDIT_PROPERTIES);
			displayName = new TextEditField(previous.getDisplayName(), EDIT_PROPERTIES, ACTIVE_EDIT_PROPERTIES);
			addComponent(new TextButton("Apply", SAVE_PROPERTIES, SAVE_HOVER_PROPERTIES, () -> {
				String error = null;
				try {
					short damage = Short.parseShort(internalDamage.getText());
					if(damage > 0 && damage < internalType.currentType.getMaxDurability()) {
						error = menu.getSet().changeItem(previous, internalType.currentType, damage, name.getText(), displayName.getText(), lore, null);
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
					state.getWindow().setMainComponent(menu.itemOverview);
			}), 0.1f, 0.1f, 0.25f, 0.2f);
		}
		else {
			name = new TextEditField("", EDIT_PROPERTIES, ACTIVE_EDIT_PROPERTIES);
			internalType = new ItemTypeSelect(ItemType.DIAMOND_HOE);
			internalDamage = new TextEditField(Short.toString(menu.getSet().nextAvailableDamage(internalType.currentType)), EDIT_PROPERTIES, ACTIVE_EDIT_PROPERTIES);
			displayName = new TextEditField("", EDIT_PROPERTIES, ACTIVE_EDIT_PROPERTIES);
			addComponent(new TextButton("Create", SAVE_PROPERTIES, SAVE_HOVER_PROPERTIES, () -> {
				String error = null;
				try {
					short damage = Short.parseShort(internalDamage.getText());
					if(damage > 0 && damage < internalType.currentType.getMaxDurability()) {
						error = menu.getSet().addItem(new CustomItem(internalType.currentType, damage, name.getText(), displayName.getText(), lore, null));
						//TODO use texture in both save buttons!
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
					state.getWindow().setMainComponent(menu.itemOverview);
			}), 0.1f, 0.1f, 0.25f, 0.2f);
		}
		addComponent(name, 0.65f, 0.75f, 0.85f, 0.85f);
		addComponent(internalType, 0.65f, 0.6f, 0.85f, 0.7f);
		addComponent(internalDamage, 0.65f, 0.45f, 0.85f, 0.55f);
		addComponent(displayName, 0.65f, 0.3f, 0.85f, 0.4f);
		addLoreComponent();
	}
	
	public static final Properties SELECT_PROPERTIES = Properties.createButton(new Color(0, 0, 200), new Color(0, 0, 50));
	public static final Properties SELECT_HOVER_PROPERTIES = Properties.createButton(new Color(0, 0, 250), new Color(0, 0, 65));
	
	private class ItemTypeSelect extends TextButton {
		
		private ItemType currentType;

		public ItemTypeSelect(ItemType initial) {
			super(initial.toString(), SELECT_PROPERTIES, SELECT_HOVER_PROPERTIES, null);
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
	
	public static final Properties LORE_BUTTON_PROPERTIES = Properties.createButton(new Color(200, 0, 200), new Color(50, 0, 50));
	public static final Properties LORE_HOVER_PROPERTIES = Properties.createButton(new Color(255, 0, 255), new Color(65, 0, 65));
	public static final Properties LORE_CANCEL_PROPERTIES = Properties.createButton(new Color(200, 200, 0), new Color(50, 50, 0));
	public static final Properties LORE_CANCEL_HOVER_PROPERTIES = Properties.createButton(new Color(255, 255, 0), new Color(65, 65, 0));
	
	private void addLoreComponent() {
		addComponent(new TextButton("Change lore...", LORE_BUTTON_PROPERTIES, LORE_HOVER_PROPERTIES, () -> {
			state.getWindow().setMainComponent(new TextArrayEditMenu(ItemEdit.this, (String[] newLore) -> {
				lore = newLore;
				System.out.println("Set lore to " + Arrays.toString(lore));
			}, LORE_CANCEL_PROPERTIES, LORE_CANCEL_HOVER_PROPERTIES, SAVE_PROPERTIES, SAVE_HOVER_PROPERTIES, EDIT_PROPERTIES, ACTIVE_EDIT_PROPERTIES, lore));
		}), 0.65f, 0.15f, 0.85f, 0.25f);
	}
}