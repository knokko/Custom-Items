package nl.knokko.customitems.item;

public class ReplaceCondition {
	private final ReplacementCondition condition;
	private final ReplacementOperation op;
	private final int value;
	private final String item;
	private final String replacingItem;
	
	public ReplaceCondition (ReplacementCondition condition, String item, ReplacementOperation op, int value, String replacingItem) {
		this.condition = condition;
		this.op = op;
		this.value = value;
		if (item == null) {
			this.item = new String();
		} else {
			this.item = item;
		}
		if (replacingItem == null) {
			throw new IllegalArgumentException("Some item has conditions to be replaced but has no replacing item assigned!");
		} else {
			this.replacingItem = replacingItem;
		}
	}
	
	public ReplacementCondition getCondition() {
		return condition;
	}
	
	public ReplacementOperation getOp() {
		return op;
	}
	
	public int getValue() {
		return value;
	}
	
	public String getItemName() {
		return item;
	}
	
	public String getReplacingItemName() {
		return replacingItem;
	}

	public static enum ReplacementCondition {
		HASITEM,
		MISSINGITEM,
		ISBROKEN
	}
	
	public static enum ReplacementOperation {
		ATMOST,
		ATLEAST,
		EXACTLY,
		NONE
	}
	
	public static enum ConditionOperation {
		AND,
		OR,
		NONE
	}
}