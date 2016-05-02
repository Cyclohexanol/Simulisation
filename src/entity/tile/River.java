package entity.tile;

import java.awt.Color;

public class River extends Water {

	public River(Material material, boolean potable, Color color) {
		super(Material.RIVER, true, Tile.COLOR_RIVER);
	}

}
