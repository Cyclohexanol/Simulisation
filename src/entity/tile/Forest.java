package entity.tile;

import java.awt.Color;

public class Forest extends Land{

	public Forest() {
		this(Tile.COLOR_FOREST);
	}
	
	public Forest(Color color) {
		super(Material.FOREST, true, false,color);
	}

}
