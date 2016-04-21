package entity;

import entity.tile.Tile;

public class WorldMap {
	private final Tile [][] tiles;
	private final int xSize, ySize;
	
	public WorldMap(Tile [][] tiles)
	{
		if(tiles == null)
			throw new IllegalArgumentException("Map must contain at least one tile!");
		
		this.tiles = tiles;
		this.xSize = tiles.length;
		this.ySize = tiles[0].length;
	}
	
	public int getHeight()
	{
		return ySize;
	}
	
	public int getWidth()
	{
		return xSize;
	}
	
	public boolean isInMap(int x, int y)
	{
		if(x<0||y<0||x>=xSize||y>=ySize)
			return false;
		return true;
	}
	
	public boolean isWalkableTile(int x, int y)
	{
		if(!isInMap(x,y))
			return false;
		
		if(!tiles[x][y].isWalkable())
			return false;
		
		return true;
	}
	
	// needs to be added
	/*public static class Builder {
		
		private Tile [][] tiles;
		
		public Builder()
		{
			this.tiles = new Tile[0][0];
		}
		
		public WorldMap build()
		{
			return new WorldMap(tiles);
		}
		
	}*/
}
