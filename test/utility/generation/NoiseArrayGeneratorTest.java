package utility.generation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

public class NoiseArrayGeneratorTest 
{
	private final int SIZE = 6000;
	private final int SEED = 0;
	
	@Test
	public void humidityTest() {
		// image size
		int size = SIZE;
		// seed (leave at 0 for random)
		int seed = SEED;
		// layer frequency, higher equals more features
		float layerF = 0.0001f;
		// weight, higher = smoother
		float weight = 1.5f;
		// number of noise loop
		int loops = 3;
		
		// generating empty buffered images
		BufferedImage img = new BufferedImage(size, size,BufferedImage.TYPE_INT_RGB);
		
		float[][] humidity = NoiseArrayGenerator.generateNoise(size, size, seed, layerF, weight, loops);
		for(int i=0; i<size; ++i)
			for(int j=0; j<size; ++j)
			{
				int shade = (int)(126 - humidity[i][j]*126);
				int r = shade;
				int g = shade;
				int b = 255;
				int col = (r << 16) | (g << 8) | b;
				img.setRGB(i, j, col);
			}
		
		File f = new File("humidity.png");
		try {
			ImageIO.write(img, "PNG", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void terrainTest() {
		// image size
		int size = SIZE;
		// seed (leave at 0 for random)
		int seed = SEED;
		// layer frequency, higher equals more features
		float layerF = 0.0003f;
		// weight, higher = smoother
		float weight = 1f;
		// number of noise loop
		int loops = 3;
		
		// generating empty buffered images
		BufferedImage img = new BufferedImage(size, size,BufferedImage.TYPE_INT_RGB);
		
		float[][] humidity = NoiseArrayGenerator.generateNoise(size, size, seed, layerF, weight, loops);
		for(int i=0; i<size; ++i)
			for(int j=0; j<size; ++j)
			{
				int shade = (int)(126 - humidity[i][j]*126);
				int r = shade;
				int g = shade;
				int b = shade;
				int col = (r << 16) | (g << 8) | b;
				img.setRGB(i, j, col);
			}
		
		File f = new File("shapeShade.png");
		try {
			ImageIO.write(img, "PNG", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
