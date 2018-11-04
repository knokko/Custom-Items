package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.item.CustomItemType.Category;

public class EditItemSimple extends EditItemBase {
	
	private final CustomItem previous;

	public EditItemSimple(EditMenu menu, CustomItem previous) {
		super(menu, previous);
		this.previous = previous;
		
	}

	@Override
	protected String create() {
		String error = null;
		try {
			short damage = Short.parseShort(internalDamage.getText());
			if(damage > 0 && damage < internalType.currentType.getMaxDurability())
				error = menu.getSet().addItem(new CustomItem(internalType.currentType, damage, name.getText(), displayName.getText(), lore, attributes, textureSelect.currentTexture));
			else
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
			if(damage > 0 && damage < internalType.currentType.getMaxDurability())
				error = menu.getSet().changeItem(previous, internalType.currentType, damage, name.getText(), displayName.getText(), lore, attributes, textureSelect.currentTexture);
			else
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
		return Category.DEFAULT;
	}
}