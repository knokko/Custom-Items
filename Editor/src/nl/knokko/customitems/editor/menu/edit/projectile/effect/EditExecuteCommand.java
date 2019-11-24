package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

import java.util.Collection;

import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.projectile.effects.ExecuteCommand;
import nl.knokko.customitems.projectile.effects.ExecuteCommand.Executor;
import nl.knokko.customitems.projectile.effects.ProjectileEffect;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditExecuteCommand extends EditProjectileEffect {
	
	private static final float BUTTON_X = 0.4f;
	private static final float LABEL_X = BUTTON_X - 0.01f;
	
	private final ExecuteCommand original;
	
	private Executor executor;

	public EditExecuteCommand(ExecuteCommand original, Collection<ProjectileEffect> backingCollection,
			GuiComponent returnMenu) {
		super(backingCollection, returnMenu);
		this.original = original;
		
		this.executor = original == null ? Executor.PROJECTILE : original.executor;
	}

	@Override
	protected void addComponents() {
		super.addComponents();
		
		TextEditField commandField = new TextEditField(original == null ? "" : original.command, EDIT_BASE, EDIT_ACTIVE);
		addComponent(new DynamicTextComponent("Command:", LABEL), LABEL_X - 0.15f, 0.7f, LABEL_X, 0.8f);
		addComponent(commandField, BUTTON_X, 0.71f, 0.99f, 0.79f);
		addComponent(new DynamicTextComponent("Executed by:", LABEL), LABEL_X - 0.18f, 0.6f, LABEL_X, 0.7f);
		addComponent(EnumSelect.createSelectButton(Executor.class, (Executor newExecutor) -> {
			executor = newExecutor;
		}, executor), BUTTON_X, 0.61f, BUTTON_X + 0.2f, 0.69f);
		
		addComponent(new DynamicTextButton(original == null ? "Create" : "Apply", SAVE_BASE, SAVE_HOVER, () -> {
			ExecuteCommand dummy = new ExecuteCommand(commandField.getText(), executor);
			String error = dummy.validate();
			if (error == null) {
				if (original == null) {
					backingCollection.add(dummy);
				} else {
					original.command = dummy.command;
					original.executor = dummy.executor;
				}
				state.getWindow().setMainComponent(returnMenu);
			} else {
				errorComponent.setText(error);
			}
		}), 0.025f, 0.2f, 0.2f, 0.3f);
	}
}
