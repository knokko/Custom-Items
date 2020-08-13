package nl.knokko.customitems.editor.menu.edit;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class CollectionSelect<T> extends GuiMenu {
	
	public static <T> DynamicTextButton createButton(Iterable<T> backingCollection, Receiver<T> onSelect,
			Filter<T> filter, Formatter<T> formatter, T current) {
		String text = current == null ? "None" : current.toString();
		return new DynamicTextButton(text, EditProps.BUTTON, EditProps.HOVER, null) {
			
			@Override
			public void click(float x, float y, int button) {
				state.getWindow().setMainComponent(new CollectionSelect<T>(backingCollection, (T selected) -> {
					setText(selected.toString());
					onSelect.onSelect(selected);
				}, filter, formatter, state.getWindow().getMainComponent()));
			}
		};
	}
	
	public static <T> DynamicTextButton createButton(Iterable<T> backingCollection, Receiver<T> onSelect,
			Formatter<T> formatter, T current) {
		return createButton(backingCollection, onSelect, (T item) -> { return true; }, formatter, current);
	}
	
	private final Iterable<T> collection;
	private final Receiver<T> onSelect;
	private final Filter<T> filter;
	private final Formatter<T> formatter;
	
	private final GuiComponent returnMenu;

	public CollectionSelect(Iterable<T> backingCollection, Receiver<T> onSelect, Filter<T> filter,
			Formatter<T> formatter, GuiComponent returnMenu) {
		this.collection = backingCollection;
		this.onSelect = onSelect;
		this.filter = filter;
		this.returnMenu = returnMenu;
		this.formatter = formatter;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.15f, 0.8f);
		
		int counter = 0;
		for (T item : collection) {
			if (filter.canSelect(item)) {
				T copy = item;
				String name = formatter.getName(item);
				addComponent(new DynamicTextButton(name, EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
					onSelect.onSelect(copy);
					state.getWindow().setMainComponent(returnMenu);
				}), 0.3f, 0.9f - counter * 0.12f, 0.3f + Math.min(0.7f, name.length() * 0.015f), 1f - counter * 0.12f);
				counter++;
			}
		}
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	public static interface Receiver<T> {
		
		void onSelect(T selected);
	}
	
	public static interface Filter<T> {
		
		boolean canSelect(T item);
	}
	
	public static interface Formatter<T> {
		
		String getName(T item);
	}
}
