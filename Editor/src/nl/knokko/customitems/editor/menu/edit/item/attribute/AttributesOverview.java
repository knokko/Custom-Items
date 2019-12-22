/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2019 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package nl.knokko.customitems.editor.menu.edit.item.attribute;

import java.util.List;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.AttributeModifier.Attribute;
import nl.knokko.customitems.item.AttributeModifier.Operation;
import nl.knokko.customitems.item.AttributeModifier.Slot;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.component.text.FloatEditField;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.texture.loader.GuiTextureLoader;
import nl.knokko.gui.util.Option;

public class AttributesOverview extends GuiMenu {
	
	private final AttributeModifier[] current;
	private final Receiver receiver;
	private final GuiComponent returnMenu;
	
	private final AttributeModifier exampleModifier;
	
	private final DynamicTextComponent errorComponent;
	
	private GuiTexture deleteBase;
	private GuiTexture deleteHover;

	public AttributesOverview(AttributeModifier exampleModifier, AttributeModifier[] currentAttributes, GuiComponent returnMenu, Receiver receiver) {
		this.current = currentAttributes;
		this.receiver = receiver;
		this.returnMenu = returnMenu;
		this.exampleModifier = exampleModifier;
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
		addComponent(new DynamicTextButton("New Attribute", EditProps.BUTTON, EditProps.HOVER, () -> {
			float y = 0.8f - (getComponents().size() - 4) * 0.125f;
			addComponent(new Entry(exampleModifier), 0.325f, y, 1f, y + 0.1f);
		}), 0.05f, 0.5f, 0.3f, 0.6f);
		addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			List<SubComponent> components = getComponents();
			AttributeModifier[] result = new AttributeModifier[components.size() - 4];
			int index = 0;
			for (SubComponent component : components) {
				if (component.getComponent() instanceof Entry) {
					Entry entry = (Entry) component.getComponent();
					Option.Double maybeValue = entry.valueField.getDouble();
					if (maybeValue.hasValue()) {
						result[index++] = new AttributeModifier(entry.attribute, entry.slot, entry.operation, maybeValue.getValue());
					} else {
						errorComponent.setText("All values must be numbers");
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
			addComponent(new Entry(current[index]), 0.325f, y, 1f, y + 0.1f);
		}
	}
	
	private class Entry extends GuiMenu {
		
		private Attribute attribute;
		private Slot slot;
		private Operation operation;
		private FloatEditField valueField;
		
		private Entry(AttributeModifier attribute) {
			this.attribute = attribute.getAttribute();
			this.slot = attribute.getSlot();
			this.operation = attribute.getOperation();
			this.valueField = new FloatEditField(attribute.getValue(), -Double.MAX_VALUE, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND;
		}

		@Override
		protected void addComponents() {
			GuiComponent attributeButton = EnumSelect.createSelectButton(Attribute.class, (Attribute newAttribute) -> {
				this.attribute = newAttribute;
			}, this.attribute);
			GuiComponent slotButton = EnumSelect.createSelectButton(Slot.class, (Slot newSlot) -> {
				this.slot = newSlot;
			}, this.slot);
			GuiComponent operationButton = EnumSelect.createSelectButton(Operation.class, (Operation newOperation) -> {
				this.operation = newOperation;
			}, operation);
			addComponent(new ImageButton(deleteBase, deleteHover, () -> {
				AttributesOverview.this.removeComponent(this);
			}), 0f, 0f, 0.075f, 1f);
			addComponent(attributeButton, 0.09f, 0f, 0.41f, 1f);
			addComponent(slotButton, 0.425f, 0f, 0.6f, 1f);
			addComponent(operationButton, 0.61f, 0f, 0.76f, 1f);
			addComponent(new DynamicTextComponent("Value: ", EditProps.LABEL), 0.775f, 0f, 0.87f, 1f);
			addComponent(valueField, 0.875f, 0f, 0.995f, 1f);
		}
	}
	
	public static interface Receiver {
		
		void onComplete(AttributeModifier[] newAttributes);
	}
}