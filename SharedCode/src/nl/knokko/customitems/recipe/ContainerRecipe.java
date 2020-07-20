package nl.knokko.customitems.recipe;

import java.util.ArrayList;
import java.util.Collection;

public class ContainerRecipe {
	
	private final Collection<InputEntry> inputs;
	private final Collection<OutputEntry> outputs;
	
	private int duration;
	
	public ContainerRecipe() {
		this.inputs = new ArrayList<>(1);
		this.outputs = new ArrayList<>(1);
		this.duration = 0;
	}
	
	public ContainerRecipe(Collection<InputEntry> inputs, 
			Collection<OutputEntry> outputs, int duration) {
		this.inputs = inputs;
		this.outputs = outputs;
		this.duration = duration;
	}
	
	public Collection<InputEntry> getInputs() {
		return inputs;
	}
	
	public Collection<OutputEntry> getOutputs() {
		return outputs;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public void setDuration(int newDuration) {
		duration = newDuration;
	}
	
	public static class InputEntry {
		
		public String inputSlotName;
		public SCIngredient ingredient;
		
		public InputEntry(String inputSlotName, SCIngredient ingredient) {
			this.inputSlotName = inputSlotName;
			this.ingredient = ingredient;
		}
	}
	
	public static class OutputEntry {
		
		public String outputSlotName;
		/**
		 * One of the results of a recipe. In the plug-in, this should be of type
		 * ItemStack. In the editor, this should be of type Result.
		 */
		public Object result;
		
		public OutputEntry(String outputSlotName, Object result) {
			this.outputSlotName = outputSlotName;
			this.result = result;
		}
	}
}
