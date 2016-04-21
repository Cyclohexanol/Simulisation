package utility.generation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

public class NoiseArrayGeneratorTest 
{
	@Test
	public void test1() {
		// image size
		int size = 256;
		// seed (leave at 0 for random)
		int seed = 0;
		// layer frequency, higher equals more features
		float layerF = 0.003f;
		// weight, higher = smoother
		float weight = 1;
		// number of noise loop
		int loops = 3;
		
		// generating empty buffered images
		BufferedImage grayScaleImg = new BufferedImage(size, size,BufferedImage.TYPE_INT_RGB);
		
		float[][] noiseArray = NoiseArrayGenerator.generateNoise(size, size, seed, layerF, weight, loops);
		
		for(int i=0; i<size; ++i)
			for(int j=0; j<size; ++j)
			{
				int shade = (int)(126 - noiseArray[i][j]*126);
				int r = shade;
				int g = shade;
				int b = shade;
				int col = (r << 16) | (g << 8) | b;
				grayScaleImg.setRGB(i, j, col);
			}
		
		File f = new File("grayScale.png");
		try {
			ImageIO.write(grayScaleImg, "PNG", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
