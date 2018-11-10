package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.item.CustomTool;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.CustomItemType.Category;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.text.TextComponent;
import nl.knokko.gui.component.text.TextEditField;

public class EditItemTool extends EditItemBase {
	
	private final CustomTool previous;
	private final Category category;
	
	private final CheckboxComponent allowEnchanting;
	private final CheckboxComponent allowAnvil;
	private final TextEditField durability;

	public EditItemTool(EditMenu menu, CustomTool previous, Category toolCategory) {
		super(menu, previous);
		this.previous = previous;
		category = toolCategory;
		if (previous != null) {
			allowEnchanting = new CheckboxComponent(previous.allowEnchanting());
			allowAnvil = new CheckboxComponent(previous.allowAnvilActions());
			durability = new TextEditField(previous.getDurability() + "", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		} else {
			allowEnchanting = new CheckboxComponent(true);
			allowAnvil = new CheckboxComponent(true);
			durability = new TextEditField("500", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		}
		if (toolCategory == Category.SWORD)
			internalType.currentType = CustomItemType.IRON_SWORD;
		else if (toolCategory == Category.PICKAXE)
			internalType.currentType = CustomItemType.IRON_PICKAXE;
		else if (toolCategory == Category.AXE)
			internalType.currentType = CustomItemType.IRON_AXE;
		else if (toolCategory == Category.SHOVEL)
			internalType.currentType = CustomItemType.IRON_SHOVEL;
		else if (toolCategory == Category.HOE)
			internalType.currentType = CustomItemType.IRON_HOE;
		else if (toolCategory == Category.SHEAR)
			internalType.currentType = CustomItemType.SHEARS;
		else
			throw new Error("Unsupported category for EditItemTool: " + toolCategory);
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		internalType.setText(internalType.currentType.toString());
		addComponent(allowEnchanting, 0.75f, 0.7f, 0.775f, 0.725f);
		addComponent(new TextComponent("Allow enchanting", EditProps.LABEL), 0.8f, 0.7f, 0.95f, 0.8f);
		addComponent(allowAnvil, 0.75f, 0.6f, 0.775f, 0.625f);
		addComponent(new TextComponent("Allow anvil actions", EditProps.LABEL), 0.8f, 0.6f, 0.95f, 0.7f);
		addComponent(durability, 0.9f, 0.5f, 0.95f, 0.6f);
		addComponent(new TextComponent("Max uses: ", EditProps.LABEL), 0.75f, 0.5f, 0.875f, 0.6f);
	}

	@Override
	protected String create() {
		String error = null;
		try {
			short damage = Short.parseShort(internalDamage.getText());
			if(damage > 0 && damage < internalType.currentType.getMaxDurability()) {
				try {
					int maxUses = Integer.parseInt(durability.getText());
					if (maxUses > 0 || maxUses == CustomItem.UNBREAKABLE_TOOL_DURABILITY) {
						error = menu.getSet().addTool(new CustomTool(internalType.currentType, damage, name.getText(), 
								displayName.getText(), lore, attributes, maxUses, allowEnchanting.isChecked(), 
								allowAnvil.isChecked(), textureSelect.currentTexture));
					} else
						error = "The max uses must be greater than 0 or " + CustomItem.UNBREAKABLE_TOOL_DURABILITY + " to become unbreakable";
				} catch (NumberFormatException nfe) {
					error = "The max uses must be an integer";
				}
			} else
				error = "The internal item damage must be larger than 0 and smaller than " + internalType.currentType.getMaxDurability();
		} catch (NumberFormatException nfe) {
			error = "The internal item damage must be an integer.";
		}
		return error;
	}

	@Override
	protected String apply() {
		String error = null;
		try {
			short damage = Short.parseShort(internalDamage.getText());
			if(damage > 0 && damage < internalType.currentType.getMaxDurability()) {
				try {
					int maxUses = Integer.parseInt(durability.getText());
					if (maxUses > 0 || maxUses == CustomItem.UNBREAKABLE_TOOL_DURABILITY) {
						error = menu.getSet().changeTool(previous, internalType.currentType, damage, name.getText(), 
								displayName.getText(), lore, attributes, allowEnchanting.isChecked(), 
								allowAnvil.isChecked(), maxUses, textureSelect.currentTexture);
					} else
						error = "The max uses must be greater than 0 or " + CustomItem.UNBREAKABLE_TOOL_DURABILITY + " to become unbreakable";
				} catch (NumberFormatException nfe) {
					error = "The max uses must be an integer";
				}
			} else
				error = "The internal item damage must be larger than 0 and smaller than " + internalType.currentType.getMaxDurability();
		} catch (NumberFormatException nfe) {
			error = "The internal item damage must be an integer.";
		}
		return error;
	}

	@Override
	protected CustomItem previous() {
		return previous;
	}

	@Override
	protected Category getCategory() {
		return category;
	}
}