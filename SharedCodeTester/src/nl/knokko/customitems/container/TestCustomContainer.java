package nl.knokko.customitems.container;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import nl.knokko.customitems.container.fuel.CustomFuelRegistry;
import nl.knokko.customitems.container.fuel.FuelMode;
import nl.knokko.customitems.container.slot.CustomSlot;
import nl.knokko.customitems.container.slot.FuelCustomSlot;
import nl.knokko.customitems.container.slot.FuelIndicatorCustomSlot;
import nl.knokko.customitems.container.slot.InputCustomSlot;
import nl.knokko.customitems.container.slot.OutputCustomSlot;
import nl.knokko.customitems.container.slot.ProgressIndicatorCustomSlot;
import nl.knokko.customitems.container.slot.display.CustomItemSlotDisplay;
import nl.knokko.customitems.container.slot.display.DataVanillaSlotDisplay;
import nl.knokko.customitems.container.slot.display.SimpleVanillaSlotDisplay;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.recipe.ContainerRecipe;
import nl.knokko.customitems.recipe.ContainerRecipe.InputEntry;
import nl.knokko.customitems.recipe.ContainerRecipe.OutputEntry;
import nl.knokko.customitems.test.DummyIngredient;
import nl.knokko.customitems.test.TestCustomItem;
import nl.knokko.customitems.test.TestHelper;
import nl.knokko.customitems.trouble.UnknownEncodingException;

public class TestCustomContainer {

	@Test
	public void testSaveLoad() {
		TestHelper.testSaveLoad(
				output -> createTestContainer().save(output, 
						ingredient -> output.addInt(((DummyIngredient)ingredient).getId()), 
						result -> output.addInt(((DummyIngredient)result).getId())),
				input -> {
					try {
						CustomContainer container = CustomContainer.load(input, 
								name -> new TestCustomItem(name), 
								name -> new CustomFuelRegistry(name, new ArrayList<>(0)), 
								() -> new DummyIngredient(input.readInt()), 
								() -> new DummyIngredient(input.readInt()));
						
						// General properties
						assertEquals("test_container", container.getName());
						assertEquals("Test Container", container.getDisplayName());
						assertEquals(FuelMode.ANY, container.getFuelMode());
						assertEquals(VanillaContainerType.BLAST_FURNACE, container.getVanillaType());
						assertTrue(container.hasPersistentStorage());
						
						// Recipes
						assertEquals(1, container.getRecipes().size());
						{
							ContainerRecipe recipe = container.getRecipes().iterator().next();
							assertEquals(2, recipe.getInputs().size());
							Iterator<InputEntry> inputs = recipe.getInputs().iterator();
							InputEntry input1 = inputs.next();
							assertEquals("ingot", input1.inputSlotName);
							assertEquals(1, ((DummyIngredient)input1.ingredient).getId());
							InputEntry input2 = inputs.next();
							assertEquals("dust", input2.inputSlotName);
							assertEquals(2, ((DummyIngredient)input2.ingredient).getId());
							
							assertEquals(1, recipe.getOutputs().size());
							OutputEntry output = recipe.getOutputs().iterator().next();
							assertEquals("ingot", output.outputSlotName);
							assertEquals(3, ((DummyIngredient)output.result).getId());
						}
						
						// Slots
						assertEquals(2, container.getHeight());
						
						assertEquals("ingot", ((OutputCustomSlot)container.getSlot(1, 1)).getName());
						assertEquals("ingot", ((InputCustomSlot)container.getSlot(3, 0)).getName());
						assertEquals("dust", ((InputCustomSlot)container.getSlot(3, 1)).getName());
						{
							FuelCustomSlot fuelSlot = (FuelCustomSlot) container.getSlot(5, 1);
							assertEquals("theFuel", fuelSlot.getName());
							assertEquals("theRegistry", fuelSlot.getRegistry().getName());
						}
						{
							FuelIndicatorCustomSlot indicator = (FuelIndicatorCustomSlot) container.getSlot(4, 0);
							assertEquals("theFuel", indicator.getFuelSlotName());
							
							SimpleVanillaSlotDisplay display = (SimpleVanillaSlotDisplay) indicator.getDisplay();
							assertEquals(CIMaterial.LOG, display.getMaterial());
							assertEquals("SomeLog", display.getDisplayName());
							assertArrayEquals(new String[] {"Just some log"}, display.getLore());
							assertEquals(1, display.getAmount());
							
							DataVanillaSlotDisplay placeholder = (DataVanillaSlotDisplay) indicator.getPlaceholder();
							assertEquals(CIMaterial.WOOL, placeholder.getMaterial());
							assertEquals(2, placeholder.getData());
							assertEquals("ColoredWool", placeholder.getDisplayName());
							assertArrayEquals(new String[] {"This wool has some color", "But I don't know which"}, placeholder.getLore());
							assertEquals(3, placeholder.getAmount());
							
							assertEquals(15, indicator.getDomain().getBegin());
							assertEquals(25, indicator.getDomain().getEnd());
						}
						{
							ProgressIndicatorCustomSlot indicator = (ProgressIndicatorCustomSlot) container.getSlot(5, 0);
							assertEquals("test_item", ((CustomItemSlotDisplay) indicator.getDisplay()).getItem().getName());
							assertEquals(5, indicator.getDisplay().getAmount());
							assertEquals("another", ((CustomItemSlotDisplay) indicator.getPlaceHolder()).getItem().getName());
							assertEquals(2, indicator.getPlaceHolder().getAmount());
							assertEquals(1, indicator.getDomain().getBegin());
							assertEquals(2, indicator.getDomain().getEnd());
						}
					} catch (UnknownEncodingException e) {
						throw new Error(e);
					}
				}
		);
	}
	
	private static CustomContainer createTestContainer() {
		Collection<ContainerRecipe> recipes = new ArrayList<>(1);
		{
			Collection<InputEntry> inputs = new ArrayList<>(2);
			inputs.add(new InputEntry("ingot", new DummyIngredient(1)));
			inputs.add(new InputEntry("dust", new DummyIngredient(2)));
			
			Collection<OutputEntry> outputs = new ArrayList<>(1);
			outputs.add(new OutputEntry("ingot", new DummyIngredient(3)));
			recipes.add(new ContainerRecipe(inputs, outputs, 14));
		}
		
		CustomSlot[][] slots = new CustomSlot[9][2];
		
		CustomFuelRegistry fuelRegistry = new CustomFuelRegistry("theRegistry", new ArrayList<>(0));
		
		slots[1][1] = new OutputCustomSlot("ingot");
		slots[3][0] = new InputCustomSlot("ingot");
		slots[3][1] = new InputCustomSlot("dust");
		slots[5][1] = new FuelCustomSlot("theFuel", fuelRegistry);
		slots[4][0] = new FuelIndicatorCustomSlot("theFuel", 
				new SimpleVanillaSlotDisplay(CIMaterial.LOG, "SomeLog", new String[] {
						"Just some log"
				}, 1), new DataVanillaSlotDisplay(CIMaterial.WOOL, (byte) 2, 
						"ColoredWool", new String[] {
								"This wool has some color", "But I don't know which"
				}, 3), new IndicatorDomain(15, 25));
		slots[5][0] = new ProgressIndicatorCustomSlot(
				new CustomItemSlotDisplay(new TestCustomItem("test_item"), 5), 
				new CustomItemSlotDisplay(new TestCustomItem("another"), 2),
				new IndicatorDomain(1, 2));
		return new CustomContainer(
				"test_container", "Test Container", 
				recipes, FuelMode.ANY, slots, 
				VanillaContainerType.BLAST_FURNACE, true
		);
	}
}
