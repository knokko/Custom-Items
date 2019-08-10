package nl.knokko.customitems.editor.menu.edit.drops.mob;

import java.util.Collection;

import nl.knokko.customitems.drops.EntityDrop;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class MobDropsOverview extends GuiMenu {
	
	private final EditMenu editMenu;
	private final GuiComponent returnMenu;
	private final DropList list;
	
	public MobDropsOverview(EditMenu editMenu, GuiComponent returnMenu) {
		this.editMenu = editMenu;
		this.returnMenu = returnMenu;
		this.list = new DropList();
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.8f, 0.2f, 0.9f);
		addComponent(new DynamicTextButton("New mob drop", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditMobDrop(editMenu.getSet(), this, null));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		addComponent(list, 0.4f, 0f, 1f, 1f);
	}
	
	@Override
	public void init() {
		if(didInit) list.refresh();
		super.init();
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	private class DropList extends GuiMenu {

		@Override
		protected void addComponents() {
			refresh();
		}
		
		private void refresh() {
			clearComponents();
			
			Collection<EntityDrop> mobDrops = editMenu.getSet().getBackingMobDrops();
			int index = 0;
			
			for (EntityDrop drop : mobDrops) {
				float offset = index * 0.125f;
				float minY = 0.9f - offset;
				float maxY = 1f - offset;
				
				addComponent(new DynamicTextComponent(drop.toString(), EditProps.LABEL), 0f, minY, 0.5f, maxY);
				addComponent(new DynamicTextButton("Edit", EditProps.BUTTON, EditProps.HOVER, () -> {
					state.getWindow().setMainComponent(new EditMobDrop(editMenu.getSet(), MobDropsOverview.this, drop));
				}), 0.55f, minY, 0.7f, maxY);
				addComponent(new DynamicTextButton("Delete", EditProps.QUIT_BASE, EditProps.QUIT_HOVER, () -> {
					editMenu.getSet().removeMobDrop(drop);
					refresh();
				}), 0.75f, minY, 0.95f, maxY);
				index++;
			}
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND2;
		}
	}
}