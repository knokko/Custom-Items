package nl.knokko.customitems.editor.set.item;

import java.awt.image.BufferedImage;

import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class NamedImage {
	
	private String name;
	
	private BufferedImage image;
	
	public NamedImage(String name, BufferedImage image) {
		this.name = name;
		this.image = image;
	}
	
	public NamedImage(BitInput input) {
		name = input.readJavaString();
		image = new BufferedImage(input.readInt(), input.readInt(), BufferedImage.TYPE_INT_ARGB);
		input.increaseCapacity(32 * image.getWidth() * image.getHeight());
		for(int x = 0; x < image.getWidth(); x++)
			for(int y = 0; y < image.getHeight(); y++)
				image.setRGB(x, y, input.readDirectInt());
	}
	
	public String getName() {
		return name;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	public void save(BitOutput output) {
		output.addJavaString(name);
		output.ensureExtraCapacityCapacity(32 * (2 + image.getWidth() * image.getHeight()));
		output.addDirectInt(image.getWidth());
		output.addDirectInt(image.getHeight());
		for(int x = 0; x < image.getWidth(); x++)
			for(int y = 0; y < image.getHeight(); y++)
				output.addDirectInt(image.getRGB(x, y));
	}
}