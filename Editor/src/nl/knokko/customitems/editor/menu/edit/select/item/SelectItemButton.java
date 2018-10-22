package nl.knokko.customitems.editor.menu.edit.select.item;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.Material;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.texture.loader.GuiTextureLoader;
import nl.knokko.gui.util.Condition;
import nl.knokko.gui.util.TextBuilder;

public class SelectItemButton extends ImageButton {
	
	private static final GuiTexture[] IMAGES;
	private static final GuiTexture[] HOVER_IMAGES;
	private static final GuiTexture[] ACTIVE_IMAGES;
	
	static {
		Material[] values = Material.values();
		IMAGES = new GuiTexture[values.length];
		HOVER_IMAGES = new GuiTexture[values.length];
		ACTIVE_IMAGES = new GuiTexture[values.length];
	}
	
	private static GuiTexture get(Material material, GuiTextureLoader loader) {
		if (IMAGES[material.ordinal()] == null)
			IMAGES[material.ordinal()] = loader.loadTexture(TextBuilder.createTexture(material.name().toLowerCase(), EditProps.SELECT_BASE));
		return IMAGES[material.ordinal()];
	}
	
	private static GuiTexture getHover(Material material, GuiTextureLoader loader) {
		if (HOVER_IMAGES[material.ordinal()] == null) 
			HOVER_IMAGES[material.ordinal()] = loader.loadTexture(TextBuilder.createTexture(material.name().toLowerCase(), EditProps.SELECT_HOVER));
		return HOVER_IMAGES[material.ordinal()];
	}
	
	private static GuiTexture getActive(Material material, GuiTextureLoader loader) {
		if (ACTIVE_IMAGES[material.ordinal()] == null) 
			ACTIVE_IMAGES[material.ordinal()] = loader.loadTexture(TextBuilder.createTexture(material.name().toLowerCase(), EditProps.SELECT_ACTIVE));
		return ACTIVE_IMAGES[material.ordinal()];
	}

	public SelectItemButton(GuiTextureLoader loader, Material material, Runnable clickAction) {
		super(get(material, loader), getHover(material, loader), clickAction);
	}
	
	public static class Active extends SelectItemButton {
		
		private Condition active;
		private GuiTexture activeTexture;
		
		private final Material material;

		public Active(GuiTextureLoader loader, Material material, Runnable clickAction, Condition active) {
			super(loader, material, clickAction);
			this.active = active;
			this.material = material;
		}
		
		@Override
		public void render(GuiRenderer renderer) {
			if (active.isTrue()) {
				if (activeTexture == null) activeTexture = getActive(material, state.getWindow().getTextureLoader());
				renderer.renderTexture(activeTexture, 0, 0, 1, 1);
			} else
				super.render(renderer);
		}
	}
}