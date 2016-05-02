package entity.tile;

import java.awt.Color;

public class Rock extends Land {

	public Rock() 
	{
		this(Tile.COLOR_SCORCHED);
	}
	
	public Rock(Color color)
	{
		super(Material.ROCK, true, false,color);
	}

}
