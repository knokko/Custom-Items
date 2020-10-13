package nl.knokko.customitems.recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ExceptionSupplier;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class ContainerRecipe {
	
	public static ContainerRecipe load(
			BitInput input,
			ExceptionSupplier<SCIngredient, UnknownEncodingException> loadIngredient,
			ExceptionSupplier<Object, UnknownEncodingException> loadResult
	) throws UnknownEncodingException {
		
		byte encoding = input.readByte();
		switch (encoding) {
		case Encodings.ENCODING1: return load1(input, loadIngredient, loadResult);
		default: throw new UnknownEncodingException("ContainerRecipe", encoding);
		}
	}
	
	private static ContainerRecipe load1(
			BitInput input,
			ExceptionSupplier<SCIngredient, UnknownEncodingException> loadIngredient,
			ExceptionSupplier<Object, UnknownEncodingException> loadResult
	) throws UnknownEncodingException {
		
		int numInputs = input.readInt();
		Collection<InputEntry> inputs = new ArrayList<>(numInputs);
		for (int inputCounter = 0; inputCounter < numInputs; inputCounter++) {
			
			String inputSlotName = input.readString();
			SCIngredient ingredient = loadIngredient.get();
			
			// A bug in the past made it possible that NoIngredient instances are
			// encoded ;( This seems like the easiest way to nullify the damage.
			if (!ingredient.getClass().getSimpleName().equals("NoIngredient")) {
				inputs.add(new InputEntry(inputSlotName, ingredient));
			}
		}
		
		int numOutputs = input.readInt();
		Collection<OutputEntry> outputs = new ArrayList<>(numOutputs);
		for (int outputCounter = 0; outputCounter < numOutputs; outputCounter++) {
			
			String outputSlotName = input.readString();
			Object result = loadResult.get();
			outputs.add(new OutputEntry(outputSlotName, result));
		}
		
		int recipeDuration = input.readInt();
		int experience = input.readInt();
		
		return new ContainerRecipe(inputs, outputs, recipeDuration, experience);
	}
	
	private final Collection<InputEntry> inputs;
	private final Collection<OutputEntry> outputs;
	
	private int duration;
	private int experience;
	
	public ContainerRecipe() {
		this.inputs = new ArrayList<>(1);
		this.outputs = new ArrayList<>(1);
		this.duration = 0;
		this.experience = 0;
	}
	
	public ContainerRecipe(Collection<InputEntry> inputs, 
			Collection<OutputEntry> outputs, int duration, int experience) {
		this.inputs = inputs;
		this.outputs = outputs;
		this.duration = duration;
		this.experience = experience;
	}
	
	@Override
	public ContainerRecipe clone() {
		return new ContainerRecipe(new ArrayList<>(inputs), 
				new ArrayList<>(outputs), duration, experience
		);
	}
	
	@Override
	public String toString() {
		return "ContainerRecipe(inputs=" + inputs + ", outputs=" + outputs + ")";
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
		output.addInt(experience);
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
	
	public int getExperience() {
		return experience;
	}
	
	public void setExperience(int newExperience) {
		experience = newExperience;
	}
	
	public static class InputEntry {
		
		private final String inputSlotName;
		private final SCIngredient ingredient;
		
		public InputEntry(String inputSlotName, SCIngredient ingredient) {
			this.inputSlotName = inputSlotName;
			this.ingredient = ingredient;
		}
		
		@Override
		public String toString() {
			return inputSlotName + "==" + ingredient;
		}
		
		public String getInputSlotName() {
			return inputSlotName;
		}
		
		public SCIngredient getIngredient() {
			return ingredient;
		}
	}
	
	public static class OutputEntry {
		
		private final String outputSlotName;
		/**
		 * One of the results of a recipe. In the plug-in, this should be of type
		 * ItemStack. In the editor, this should be of type Result.
		 */
		private final Object result;
		
		public OutputEntry(String outputSlotName, Object result) {
			this.outputSlotName = outputSlotName;
			this.result = result;
		}
		
		@Override
		public String toString() {
			return outputSlotName + ":=" + result;
		}
		
		public String getOutputSlotName() {
			return outputSlotName;
		}
		
		public Object getResult() {
			return result;
		}
	}
	
	private static class Encodings {
		
		static final byte ENCODING1 = 1;
	}
}
