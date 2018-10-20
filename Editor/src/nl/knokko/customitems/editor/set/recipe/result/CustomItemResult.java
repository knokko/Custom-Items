package nl.knokko.customitems.editor.set.recipe.result;

import java.util.Collection;

import nl.knokko.customitems.editor.set.CustomItem;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class CustomItemResult extends Result {
	
	private final CustomItem item;

	public CustomItemResult(CustomItem item, byte amount) {
		super(amount);
		this.item = item;
		initInfo();
	}
	
	public CustomItemResult(BitInput input, ItemSet set) {
		super(input);
		String name = input.readJavaString();
		Collection<CustomItem> items = set.getItems();
		for (CustomItem item : items) {
			if (item.getName().equals(name)) {
				this.item = item;
				initInfo();
				return;
			}
		}
		throw new IllegalArgumentException("There is no item with name " + name);
	}
	
	public CustomItem getItem() {
		return item;
	}

	@Override
	protected void saveOwn(BitOutput output) {
		output.addJavaString(item.getName());
	}

	@Override
	public byte getID() {
		return RecipeEncoding.Result.CUSTOM;
	}

	@Override
	protected String[] createInfo() {
		return new String[] {
				"Custom Item:",
				"Name: " + item.getName()
		};
	}

	@Override
	public String getString() {
		return item.getName();
	}

	@Override
	public Result amountClone(byte amount) {
		return new CustomItemResult(item, amount);
	}
}