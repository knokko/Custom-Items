package nl.knokko.customitems.editor.menu.edit.item.effect;

import java.util.ArrayList;
import java.util.List;

import nl.knokko.customitems.editor.HelpButtons;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.effect.EffectType;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.texture.loader.GuiTextureLoader;
import nl.knokko.gui.util.Option;

public class EffectsOverview extends GuiMenu {
	
	private static final PotionEffect EXAMPLE_EFFECT = new PotionEffect(EffectType.HEAL, 1, 1);
	
	private final List<PotionEffect> current;
	private final Receiver receiver;
	private final GuiComponent returnMenu;
	
	private final DynamicTextComponent errorComponent;
	
	private GuiTexture deleteBase;
	private GuiTexture deleteHover;
	
	public EffectsOverview(List<PotionEffect> currentEffects, GuiComponent returnMenu, Receiver receiver) {
		this.current = currentEffects;
		this.receiver = receiver;
		this.returnMenu = returnMenu;
		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
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
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.05f, 0.8f, 0.2f, 0.9f);
		addComponent(new DynamicTextButton("New Effect", EditProps.BUTTON, EditProps.HOVER, () -> {
			float y = 0.8f - (getComponents().size() - 5) * 0.125f;
			addComponent(new Entry(EXAMPLE_EFFECT), 0.4f, y, 1f, y + 0.1f);
		}), 0.05f, 0.5f, 0.3f, 0.6f);
		addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			List<SubComponent> components = getComponents();
			List<PotionEffect> result = new ArrayList<PotionEffect>();
			for (SubComponent component : components) {
				if (component.getComponent() instanceof Entry) {
					Entry entry = (Entry) component.getComponent();
					Option.Int maybeValue = entry.valueField.getInt();
					Option.Int maybeDuration = entry.durationField.getInt();
					if (!maybeValue.hasValue() || !maybeDuration.hasValue()) {
						errorComponent.setText("All levels and durations must be integers");
						return;
					}
					result.add(new PotionEffect(entry.type, maybeDuration.getValue(), maybeValue.getValue()));
				}
			}
			receiver.onComplete(result);
			state.getWindow().setMainComponent(returnMenu);
		}), 0.05f, 0.2f, 0.2f, 0.3f);
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
		for (int index = 0; index < current.size(); index++) {
			float y = 0.8f - index * 0.125f;
			addComponent(new Entry(current.get(index)), 0.4f, y, 1f, y + 0.1f);
		}
		
		HelpButtons.addHelpLink(this, "edit%20menu/items/edit/effects.html");
	}
	
	private class Entry extends GuiMenu {
		
		private EffectType type;
		private IntEditField valueField;
		private IntEditField durationField;
		
		private Entry(PotionEffect exampleEffect) {
			this.type = exampleEffect.getEffect();
			this.durationField = new IntEditField(exampleEffect.getDuration(), 0, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			this.valueField = new IntEditField(exampleEffect.getLevel(), 0, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND;
		}

		@Override
		protected void addComponents() {
			GuiComponent effectButton = EnumSelect.createSelectButton(EffectType.class, (EffectType newEffect) -> {
				this.type = newEffect;
			}, type);
			addComponent(new ImageButton(deleteBase, deleteHover, () -> {
				EffectsOverview.this.removeComponent(this);
			}), 0f, 0f, 0.075f, 1f);
			addComponent(effectButton, 0.09f, 0f, 0.41f, 1f);
			addComponent(new DynamicTextComponent("Duration:", EditProps.LABEL), 0.5f, 0f, 0.645f, 1f);
			addComponent(durationField, 0.65f, 0f, 0.75f, 1f);
			addComponent(new DynamicTextComponent("Level: ", EditProps.LABEL), 0.775f, 0f, 0.87f, 1f);
			addComponent(valueField, 0.875f, 0f, 0.975f, 1f);
		}
	}
	
	public static interface Receiver {
		
		void onComplete(List<PotionEffect> playerEffects);
	}
}
