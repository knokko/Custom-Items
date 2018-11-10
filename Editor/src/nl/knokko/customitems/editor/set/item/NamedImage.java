/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2018 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
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
	
	@Override
	public String toString() {
		return "Texture " + name;
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