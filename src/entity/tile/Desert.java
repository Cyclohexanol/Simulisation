package entity.tile;

import java.awt.Color;

public class Desert extends Land 
{
	public Desert() {
		this(Tile.COLOR_DIRT);
	}
	
	public Desert(Color color)
	{
		super(Material.DESERT, true, true, color);
	}
	
}
