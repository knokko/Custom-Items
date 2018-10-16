package nl.knokko.customitems.encoding;

public class RecipeEncoding {
	
	public static final byte SHAPED_RECIPE = 0;
	public static final byte SHAPELESS_RECIPE = 1;
	
	public static class Ingredient {
		
		public static final byte NONE = 0;
		public static final byte VANILLA_SIMPLE = 1;
		public static final byte VANILLA_DATA = 2;
		public static final byte VANILLA_ADVANCED_1 = 3;
		public static final byte CUSTOM = 4;
	}
	
	public static class Result {
		
		public static final byte VANILLA_SIMPLE = 0;
		public static final byte VANILLA_DATA = 1;
		public static final byte VANILLA_ADVANCED_1 = 2;
		public static final byte CUSTOM = 3;
		
	}
}