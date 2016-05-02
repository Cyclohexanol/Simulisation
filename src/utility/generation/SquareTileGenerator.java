package utility.generation;

import entity.tile.*;

public class SquareTileGenerator {
	
	public static Tile[][] generateTile(int size, int seed)
	{
		// layer frequency, higher equals more features
		float terLayerF = 0.0006f;
		float waterLayerF = 0.00008f;
		float dirtLayerF = 0.0003f;
		// weight, higher = smoother
		float terWeight = 1f;
		float waterWeight = 1.1f;
		float dirtWeight = 1.2f;
		// number of noise loop
		int terLoops = 3;
		int waterLoops = 2;
		int dirtLoops = 2;
		
		float[][] ter = NoiseArrayGenerator.generateNoise(size, size, seed, terLayerF, terWeight, terLoops);
		float[][] water = NoiseArrayGenerator.generateNoise(size, size, seed, waterLayerF, waterWeight, waterLoops);
		float[][] dirt = NoiseArrayGenerator.generateNoise(size, size, seed%11, dirtLayerF, dirtWeight, dirtLoops);
		
		Tile[][] tiles = new Tile[size][size];
		
		for(int i=0;i<size;++i)
			for(int j=0;j<size;++j)
			{
				Tile tempTile = null;
				if(water[i][j]>0.4)
					tempTile = new Ocean();
				else if(water[i][j]>0.395)
					tempTile = new Beach();
				else if(water[i][j]<-0.7)
					tempTile = new Lake();
				else if(water[i][j]<-0.699)
					tempTile = new Beach();
				else if(ter[i][j]>0.95)
					tempTile = new Rock();
				else if(ter[i][j]<0.2)
					tempTile = new Forest();
				else if(dirt[i][j]>-0.95)
					tempTile = new Grass();
				else
					tempTile = new Desert();
				
				tiles[i][j] = bindTiles(tempTile,tiles,i,j);
			}
		
		return tiles;
	}
	
	public static Tile bindTiles(Tile tempTile, Tile [][] tiles, int x, int y)
	{
		Tile tile = tempTile;
		
		if(x-1>=0 && tiles[x-1][y]!=null && tiles[x-1][y].getMaterial()==tempTile.getMaterial())
			tile = tiles[x-1][y];
		else if(y-1>=0 && tiles[x][y-1]!=null && tiles[x][y-1].getMaterial()==tempTile.getMaterial())
			tile = tiles[x][y-1];
		else if(x+1<tiles.length && tiles[x+1][y]!=null && tiles[x+1][y].getMaterial()==tempTile.getMaterial())
			tile = tiles[x+1][y];
		else if(y+1<tiles[0].length && tiles[x][y+1]!=null && tiles[x][y+1].getMaterial()==tempTile.getMaterial())
			tile = tiles[x][y+1];
		
		
		return tile;
	}
}
