package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.NamedImage;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public abstract class TextureSelectButton extends DynamicTextButton {
	
	private final ItemSet set;
	private NamedImage selectedTexture;

	public TextureSelectButton(NamedImage initial, ItemSet set) {
		super(initial == null ? "None" : initial.getName(), EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, null);
		this.set = set;
		this.clickAction = () -> {
			handleClick(this);
		};
	}
	
	public NamedImage getSelected() {
		return selectedTexture;
	}
	
	protected abstract boolean allowTexture(NamedImage texture);
	
	private static void handleClick(TextureSelectButton self) {
		self.state.getWindow().setMainComponent(new SelectTexture(self.set, self.state.getWindow().getMainComponent(), (NamedImage texture) -> {
			return self.allowTexture(texture);
		}, (NamedImage texture) -> {
			self.selectedTexture = texture;
			self.setText(texture.getName());
		}));
	}
}
