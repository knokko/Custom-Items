package nl.knokko.customitems.item;

public class AttributeModifier {
	
	private final Attribute attribute;
	private final Slot slot;
	private final Operation operation;
	private final double value;
	
	public AttributeModifier(Attribute attribute, Slot slot, Operation operation, double value) {
		this.attribute = attribute;
		this.slot = slot;
		this.operation = operation;
		this.value = value;
	}
	
	public Attribute getAttribute() {
		return attribute;
	}
	
	public Slot getSlot() {
		return slot;
	}
	
	public Operation getOperation() {
		return operation;
	}
	
	public double getValue() {
		return value;
	}
	
	public static enum Attribute {
		
		MAX_HEALTH("generic.maxHealth"),
		KNOCKBACK_RESISTANCE("generic.knockbackResistance"),
		MOVEMENT_SPEED("generic.movementSpeed"),
		ATTACK_DAMAGE("generic.attackDamage"),
		ARMOR("generic.armor"),
		ARMOR_TOUGHNESS("generic.armorToughness"),
		ATTACK_SPEED("generic.attackSpeed"),
		LUCK("generic.luck");
		
		private final String attributeName;
		
		private Attribute(String name) {
			attributeName = name;
		}
		
		public String getName() {
			return attributeName;
		}
	}
	
	public static enum Slot {
		
		FEET,
		LEGS,
		CHEST,
		HEAD,
		MAINHAND,
		OFFHAND;
		
		public String getSlot() {
			return name().toLowerCase();
		}
	}
	
	public static enum Operation {
		
		ADD,
		MULTIPLY,
		CHAIN_MULTIPLY;
		
		public int getOperation() {
			return ordinal();
		}
	}
}