package utility.generation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

import entity.tile.*;

public class TileGeneratorTest 
{
	private final int SIZE = 5000;
	private final int SEED = 0;
	
	@Test
	public void test()
	{
		// image size
		int size = SIZE;
		// seed (leave at 0 for random)
		int seed = SEED;
		// generating empty buffered images
		BufferedImage img = new BufferedImage(size, size,BufferedImage.TYPE_INT_RGB);
		
		System.out.println("Generating tiles...");
		long start = System.currentTimeMillis();
		
		Tile[][] tiles = SquareTileGenerator.generateTile(size, seed);

		System.out.println((size*size)+" tiles generated in " + (System.currentTimeMillis()-start)/1000d+"s.");
		System.out.println("Generating picture...");
		start = System.currentTimeMillis();
		for(int i=0; i<size; ++i)
			for(int j=0; j<size; ++j)
			{
				img.setRGB(i, j, tiles[i][j].getColor().getRGB());
			}
		
		File f = new File("terrain.png");
		try {
			ImageIO.write(img, "PNG", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Picture generated in " +(System.currentTimeMillis()-start)/1000d+"s.");
	}
}
