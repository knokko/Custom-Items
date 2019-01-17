package nl.knokko.customitems.editor.menu.edit.item.enchantment;

import java.util.List;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.EnchantmentType;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.texture.loader.GuiTextureLoader;

public class EnchantmentsOverview extends GuiMenu {

	private static final Enchantment EXAMPLE_ENCHANTMENT = new Enchantment(EnchantmentType.DURABILITY, 2);
	
	private final Enchantment[] current;
	private final Receiver receiver;
	private final GuiComponent returnMenu;
	
	private final TextComponent errorComponent;
	
	private GuiTexture deleteBase;
	private GuiTexture deleteHover;

	public EnchantmentsOverview(Enchantment[] currentEnchantments, GuiComponent returnMenu, Receiver receiver) {
		this.current = currentEnchantments;
		this.receiver = receiver;
		this.returnMenu = returnMenu;
		this.errorComponent = new TextComponent("", EditProps.ERROR);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		GuiTextureLoader loader = state.getWindow().getTextureLoader();
		deleteBase = loader.loadTexture("nl/knokko/gui/images/icons/delete.png");
		deleteHover = loader.loadTexture("nl/knokko/gui/images/icons/delete_hover.png");
		addComponent(new TextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.05f, 0.8f, 0.2f, 0.9f);
		addComponent(new TextButton("New Enchantment", EditProps.BUTTON, EditProps.HOVER, () -> {
			float y = 0.8f - (getComponents().size() - 4) * 0.125f;
			addComponent(new Entry(EXAMPLE_ENCHANTMENT), 0.4f, y, 1f, y + 0.1f);
		}), 0.05f, 0.5f, 0.3f, 0.6f);
		addComponent(new TextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			List<SubComponent> components = getComponents();
			Enchantment[] result = new Enchantment[components.size() - 4];
			int index = 0;
			for (SubComponent component : components) {
				if (component.getComponent() instanceof Entry) {
					Entry entry = (Entry) component.getComponent();
					try {
						int value = Integer.parseInt(entry.valueField.getText());
						result[index++] = new Enchantment(entry.type, value);
					} catch (NumberFormatException ex) {
						errorComponent.setText("All levels must be integers");
						return;
					}
				}
			}
			receiver.onComplete(result);
			state.getWindow().setMainComponent(returnMenu);
		}), 0.05f, 0.2f, 0.2f, 0.3f);
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
		for (int index = 0; index < current.length; index++) {
			float y = 0.8f - index * 0.125f;
			addComponent(new Entry(current[index]), 0.4f, y, 1f, y + 0.1f);
		}
	}
	
	private class Entry extends GuiMenu {
		
		private EnchantmentType type;
		private TextEditField valueField;
		
		private TextButton enchantmentButton;
		
		private Entry(Enchantment enchantment) {
			this.type = enchantment.getType();
			this.valueField = new TextEditField(enchantment.getLevel() + "", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND;
		}

		@Override
		protected void addComponents() {
			enchantmentButton = new TextButton(type.getName(), EditProps.BUTTON, EditProps.HOVER, () -> {
				state.getWindow().setMainComponent(new EnchantmentSelect((EnchantmentType newEnchantment) -> {
					this.type = newEnchantment;
					enchantmentButton.setText(type.getName());
				}, EnchantmentsOverview.this));
			});
			addComponent(new ImageButton(deleteBase, deleteHover, () -> {
				EnchantmentsOverview.this.removeComponent(this);
			}), 0f, 0f, 0.075f, 1f);
			addComponent(enchantmentButton, 0.09f, 0f, 0.41f, 1f);
			addComponent(new TextComponent("Value: ", EditProps.LABEL), 0.775f, 0f, 0.87f, 1f);
			addComponent(valueField, 0.875f, 0f, 0.975f, 1f);
		}
	}
	
	public static interface Receiver {
		
		void onComplete(Enchantment[] newEnchantments);
	}
}
