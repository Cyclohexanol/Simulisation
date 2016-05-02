package entity.tile;

import java.awt.Color;

public class Grass extends Land {
	
	public Grass() {
		 this(Tile.COLOR_GRASSLAND);
	}
	
	public Grass(Color color)
	{
		super(Material.GRASS, true, true, color);
	}

}
