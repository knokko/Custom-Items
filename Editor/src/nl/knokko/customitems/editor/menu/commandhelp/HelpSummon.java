package nl.knokko.customitems.editor.menu.commandhelp;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.select.item.SelectCustomItem;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class HelpSummon extends GuiMenu {

	private final ItemSet set;
	private final GuiComponent returnMenu;

	private CustomItem selectedMainHand, selectedOffHand, selectedHelmet, selectedChestplate, selectedLeggings,
			selectedBoots;

	public HelpSummon(ItemSet set, GuiComponent returnMenu) {
		this.set = set;
		this.returnMenu = returnMenu;
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		TextComponent infoComponent = new TextComponent("", EditProps.LABEL);
		WrapperComponent<SimpleImageComponent> mainHandImage = new WrapperComponent<SimpleImageComponent>(null);
		WrapperComponent<SimpleImageComponent> offHandImage = new WrapperComponent<SimpleImageComponent>(null);
		WrapperComponent<SimpleImageComponent> helmetImage = new WrapperComponent<SimpleImageComponent>(null);
		WrapperComponent<SimpleImageComponent> chestplateImage = new WrapperComponent<SimpleImageComponent>(null);
		WrapperComponent<SimpleImageComponent> leggingsImage = new WrapperComponent<SimpleImageComponent>(null);
		WrapperComponent<SimpleImageComponent> bootsImage = new WrapperComponent<SimpleImageComponent>(null);

		addComponent(new DynamicTextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.8f, 0.175f, 0.9f);
		addComponent(infoComponent, 0.025f, 0.9f, 0.975f, 1f);
		addComponent(
				new DynamicTextComponent("First select the equipment, then click on one of the generate buttons below.",
						EditProps.LABEL),
				0.01f, 0.6f, 0.75f, 0.7f);
		addComponent(
				new DynamicTextComponent("Thereafter, the command will be copied to your clipboard.", EditProps.LABEL),
				0.01f, 0.5f, 0.6f, 0.6f);
		addComponent(
				new DynamicTextComponent("Then you can paste it in a command block by holding control and pressing v.",
						EditProps.LABEL),
				0.01f, 0.4f, 0.75f, 0.5f);

		// The select buttons + their images
		addComponent(new DynamicTextButton("Select maind hand...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectCustomItem(this, (CustomItem chosen) -> {
				selectedMainHand = chosen;
				mainHandImage.setComponent(new SimpleImageComponent(
						state.getWindow().getTextureLoader().loadTexture(chosen.getTexture().getImage())));
			}, set));
		}), 0.75f, 0.8f, 0.9f, 0.9f);
		addComponent(mainHandImage, 0.9f, 0.8f, 1f, 0.9f);
		addComponent(new DynamicTextButton("Select off hand...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectCustomItem(this, (CustomItem chosen) -> {
				selectedOffHand = chosen;
				offHandImage.setComponent(new SimpleImageComponent(
						state.getWindow().getTextureLoader().loadTexture(chosen.getTexture().getImage())));
			}, set));
		}), 0.75f, 0.675f, 0.9f, 0.775f);
		addComponent(offHandImage, 0.9f, 0.675f, 1f, 0.775f);
		addComponent(new DynamicTextButton("Select helmet...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectCustomItem(this, (CustomItem chosen) -> {
				selectedHelmet = chosen;
				helmetImage.setComponent(new SimpleImageComponent(
						state.getWindow().getTextureLoader().loadTexture(chosen.getTexture().getImage())));
			}, set));
		}), 0.75f, 0.55f, 0.9f, 0.65f);
		addComponent(helmetImage, 0.9f, 0.55f, 1f, 0.65f);
		addComponent(new DynamicTextButton("Select chestplate...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectCustomItem(this, (CustomItem chosen) -> {
				selectedChestplate = chosen;
				chestplateImage.setComponent(new SimpleImageComponent(
						state.getWindow().getTextureLoader().loadTexture(chosen.getTexture().getImage())));
			}, set));
		}), 0.75f, 0.425f, 0.9f, 0.525f);
		addComponent(chestplateImage, 0.9f, 0.425f, 1f, 0.525f);
		addComponent(new DynamicTextButton("Select leggings...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectCustomItem(this, (CustomItem chosen) -> {
				selectedLeggings = chosen;
				leggingsImage.setComponent(new SimpleImageComponent(
						state.getWindow().getTextureLoader().loadTexture(chosen.getTexture().getImage())));
			}, set));
		}), 0.75f, 0.3f, 0.9f, 0.4f);
		addComponent(leggingsImage, 0.9f, 0.3f, 1f, 0.4f);
		addComponent(new DynamicTextButton("Select boots...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectCustomItem(this, (CustomItem chosen) -> {
				selectedBoots = chosen;
				bootsImage.setComponent(new SimpleImageComponent(
						state.getWindow().getTextureLoader().loadTexture(chosen.getTexture().getImage())));
			}, set));
		}), 0.75f, 0.175f, 0.9f, 0.275f);
		addComponent(bootsImage, 0.9f, 0.175f, 1f, 0.275f);

		addComponent(new DynamicTextButton("Generate for minecraft 1.12", EditProps.BUTTON, EditProps.HOVER, () -> {
			String command = "/summon zombie ~ ~1 ~ {HandItems:[" + getEquipmentTag12(selectedMainHand) + ","
					+ getEquipmentTag12(selectedOffHand) + "],ArmorItems:[" + getEquipmentTag12(selectedBoots) + ","
					+ getEquipmentTag12(selectedLeggings) + "," + getEquipmentTag12(selectedChestplate) + ","
					+ getEquipmentTag12(selectedHelmet) + "]}";
			String error = CommandBlockHelpOverview.setClipboard(command);
			if (error == null) {
				infoComponent.setProperties(EditProps.LABEL);
				infoComponent.setText("Copied command to clipboard");
			} else {
				infoComponent.setProperties(EditProps.ERROR);
				infoComponent.setText("Could not copy command to clipboard because: " + error);
			}
		}), 0.2f, 0.05f, 0.45f, 0.15f);
		addComponent(new DynamicTextButton("Generate for minecraft 1.14", EditProps.BUTTON, EditProps.HOVER, () -> {
			String error = null;
			if (selectedMainHand != null && selectedMainHand.getAttributes().length == 0) {
				error = "You can't summon custom items without attribute modifiers, but the main hand doesn't have them.";
			} else if (selectedOffHand != null && selectedOffHand.getAttributes().length == 0) {
				error = "You can't summon custom items without attribute modifiers, but the off hand doesn't have them.";
			} else if (selectedHelmet != null && selectedHelmet.getAttributes().length == 0) {
				error = "You can't summon custom items without attribute modifiers, but the helmet doesn't have them.";
			} else if (selectedChestplate != null && selectedChestplate.getAttributes().length == 0) {
				error = "You can't summon custom items without attribute modifiers, but the chestplate doesn't have them.";
			} else if (selectedLeggings != null && selectedLeggings.getAttributes().length == 0) {
				error = "You can't summon custom items without attribute modifiers, but the leggings don't have them.";
			} else if (selectedBoots != null && selectedBoots.getAttributes().length == 0) {
				error = "You can't summon custom items without attribute modifiers, but the boots don't have them.";
			}
			if (error != null) {
				infoComponent.setProperties(EditProps.ERROR);
				infoComponent.setText(error);
			} else {
				String command = "/summon zombie ~ ~1 ~ {HandItems:[" + getEquipmentTag14(selectedMainHand) + ","
						+ getEquipmentTag14(selectedOffHand) + "],ArmorItems:[" + getEquipmentTag14(selectedBoots) + ","
						+ getEquipmentTag14(selectedLeggings) + "," + getEquipmentTag14(selectedChestplate) + ","
						+ getEquipmentTag14(selectedHelmet) + "]}";
				error = CommandBlockHelpOverview.setClipboard(command);
				if (error == null) {
					infoComponent.setProperties(EditProps.LABEL);
					infoComponent.setText("Copied command to clipboard");
				} else {
					infoComponent.setProperties(EditProps.ERROR);
					infoComponent.setText("Could not copy command to clipboard because: " + error);
				}
			}
		}), 0.55f, 0.05f, 0.8f, 0.15f);
	}

	private static String getEquipmentTag12(CustomItem item) {
		return item == null ? "{}" : item.getEquipmentTag12(1);
	}

	private static String getEquipmentTag14(CustomItem item) {
		return item == null ? "{}" : item.getEquipmentTag14(1);
	}
}
