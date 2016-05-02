package entity.tile;

import java.awt.Color;

public abstract class Tile 
{
	public static final Color COLOR_FOREST = new Color(70, 110, 30);
	public static final Color COLOR_OCEAN = new Color(0x44447a);
	public static final Color COLOR_LAKE = new Color(0x336699);
	public static final Color COLOR_RIVER = new Color(0x225588);
	public static final Color COLOR_DIRT = new Color(200, 135, 80);
	public static final Color COLOR_BEACH = new Color(0xa09077);
	public static final Color COLOR_SNOW = new Color(0xffffff);
	public static final Color COLOR_TUNDRA = new Color(0xbbbbaa);
	public static final Color COLOR_BARE = new Color(0x888888);
	public static final Color COLOR_SCORCHED = new Color(0x555555);
	public static final Color COLOR_TAIGA = new Color(0x99aa77);
	public static final Color COLOR_SHRUBLAND = new Color(0x889977);
	public static final Color COLOR_TEMPERATE_DESERT = new Color(0xc9d29b);
	public static final Color COLOR_TEMPERATE_RAIN_FOREST = new Color(0x448855);
	public static final Color COLOR_TEMPERATE_DECIDUOUS_FOREST = new Color(0x679459);
	public static final Color COLOR_GRASSLAND = new Color(0x88aa55);
	public static final Color COLOR_SUBTROPICAL_DESERT = new Color(0xd2b98b);
	public static final Color COLOR_TROPICAL_RAIN_FOREST = new Color(0x337755);
	public static final Color COLOR_TROPICAL_SEASONAL_FOREST = new Color(0x559944);
	
	private final Type type;
	private final Material material;
	private final boolean walkable;
	private final boolean constructable;
	private final Color color;
	
	public Tile(Type type, Material material, boolean walkable, boolean constructable, Color color) {
		super();
		this.type = type;
		this.material = material;
		this.walkable = walkable;
		this.constructable = constructable;
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public Type getType() {
		return type;
	}

	public Material getMaterial() {
		return material;
	}

	public boolean isWalkable() {
		return walkable;
	}

	public boolean isConstructable() {
		return constructable;
	}

	public enum Type
	{
		WATER, LAND
	}
	
	public enum Material
	{
		DESERT,ROCK,LAKE,OCEAN,GRASS,FOREST,BEACH,
		RIVER,SNOW
	}
	
}
