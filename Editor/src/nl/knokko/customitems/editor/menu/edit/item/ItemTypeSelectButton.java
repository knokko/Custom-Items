package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemDamageClaim;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.CustomItemType.Category;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.util.Option;

public abstract class ItemTypeSelectButton extends DynamicTextButton {
	
	protected CustomItemType currentType;
	protected final Category category;
	
	public ItemTypeSelectButton(CustomItemType initialType, Category category) {
		super(initialType.toString(), EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, null);
		this.currentType = initialType;
		this.category = category;
		this.clickAction = () -> {
			handleClick(this);
		};
	}
	
	public ItemTypeSelectButton(ItemSet set, Category category, CustomItemType preferred, ItemDamageClaim original) {
		this(chooseInitial(set, category, preferred, original), category);
	}
	
	public CustomItemType getCurrentType() {
		return currentType;
	}
	
	protected abstract void setErrorText(String error);
	
	protected abstract Option.Short getInternalDamage();
	
	protected abstract void setInternalDamage(short newDamage);
	
	protected abstract ItemSet getSet();
	
	protected abstract ItemDamageClaim getOriginal();
	
	private static CustomItemType chooseInitial(ItemSet set, Category category, CustomItemType preferred, 
			ItemDamageClaim original) {
		
		// First check if the preferred internal item type is available
		if (set.nextAvailableDamage(preferred, original) != -1)
			return preferred;
		
		// If it is not, try all other item types
		CustomItemType[] all = CustomItemType.values();
		for (CustomItemType type : all)
			if (type.canServe(category) && set.nextAvailableDamage(type, original) != -1)
				return type;
		
		// If we reach this point, there are no suitable pairs of item type and item damage left.
		// This means that no new item for the given category can be created.
		// To prevent null pointer exceptions, we will just return preferred.
		return preferred;
	}
	
	private static void handleClick(ItemTypeSelectButton self) {
		self.state.getWindow().setMainComponent(new SelectItemType(self.state.getWindow().getMainComponent(), 
			(CustomItemType newType) -> {
				
				// Change the internal item type
				self.currentType = newType;
				self.setText(newType.toString());
				
				// Check if we should change the internal item damage
				Option.Short maybeInternalDamage = self.getInternalDamage();
				ItemSet set = self.getSet();
				boolean editInternalDamage;
				
				if (maybeInternalDamage.hasValue()) {
					short internalDamage = maybeInternalDamage.getValue();
					editInternalDamage = !set.isItemDamageTypeFree(newType, internalDamage, null);
				} else {
					editInternalDamage = true;
				}
				
				if (editInternalDamage) {
					short newDamage = set.nextAvailableDamage(newType, self.getOriginal());
					if (newDamage == -1) {
						self.setErrorText("There is no internal item damage available for this type!");
					} else {
						self.setErrorText("");
						self.setInternalDamage(newDamage);
					}
				}
			}, self.category));
	}
}
