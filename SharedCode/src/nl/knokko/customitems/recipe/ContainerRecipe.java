package nl.knokko.customitems.recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class ContainerRecipe {
	
	public static ContainerRecipe load(
			BitInput input,
			Supplier<SCIngredient> loadIngredient,
			Supplier<Object> loadResult
	) throws UnknownEncodingException {
		
		byte encoding = input.readByte();
		switch (encoding) {
		case Encodings.ENCODING1: return load1(input, loadIngredient, loadResult);
		default: throw new UnknownEncodingException("ContainerRecipe", encoding);
		}
	}
	
	private static ContainerRecipe load1(
			BitInput input,
			Supplier<SCIngredient> loadIngredient,
			Supplier<Object> loadResult
	) {
		
		int numInputs = input.readInt();
		Collection<InputEntry> inputs = new ArrayList<>(numInputs);
		for (int inputCounter = 0; inputCounter < numInputs; inputCounter++) {
			
			String inputSlotName = input.readString();
			SCIngredient ingredient = loadIngredient.get();
			inputs.add(new InputEntry(inputSlotName, ingredient));
		}
		
		int numOutputs = input.readInt();
		Collection<OutputEntry> outputs = new ArrayList<>(numOutputs);
		for (int outputCounter = 0; outputCounter < numOutputs; outputCounter++) {
			
			String outputSlotName = input.readString();
			Object result = loadResult.get();
			outputs.add(new OutputEntry(outputSlotName, result));
		}
		
		int recipeDuration = input.readInt();
		
		return new ContainerRecipe(inputs, outputs, recipeDuration);
	}
	
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
	
	public void save(
			BitOutput output, 
			Consumer<SCIngredient> saveIngredient,
			Consumer<Object> saveResult
	) {
		output.addByte(Encodings.ENCODING1);
		
		output.addInt(inputs.size());
		for (InputEntry input : inputs) {
			output.addString(input.inputSlotName);
			saveIngredient.accept(input.ingredient);
		}
		
		output.addInt(outputs.size());
		for (OutputEntry outputEntry : outputs) {
			output.addString(outputEntry.outputSlotName);
			saveResult.accept(outputEntry.result);
		}
		
		output.addInt(duration);
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
		
		public final String inputSlotName;
		public final SCIngredient ingredient;
		
		public InputEntry(String inputSlotName, SCIngredient ingredient) {
			this.inputSlotName = inputSlotName;
			this.ingredient = ingredient;
		}
	}
	
	public static class OutputEntry {
		
		public final String outputSlotName;
		/**
		 * One of the results of a recipe. In the plug-in, this should be of type
		 * ItemStack. In the editor, this should be of type Result.
		 */
		public final Object result;
		
		public OutputEntry(String outputSlotName, Object result) {
			this.outputSlotName = outputSlotName;
			this.result = result;
		}
	}
	
	private static class Encodings {
		
		static final byte ENCODING1 = 1;
	}
}
