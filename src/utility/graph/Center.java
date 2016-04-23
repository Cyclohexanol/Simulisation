package utility.graph;

import java.util.ArrayList;

import utility.geom.Point;

public class Center 
{
	private int index;
	
	private Point point;
	private boolean water, ocean, coast, border;
	private String biome;
	private float elevation, moisture;
	
	ArrayList<Center> neighbours;
	ArrayList<Edge> edges;
	ArrayList<Corner> corner;
	public Center(int index, Point point, boolean water, boolean ocean, boolean coast, boolean border, String biome,
			float elevation, float moisture) {
		super();
		this.index = index;
		this.point = point;
		this.water = water;
		this.ocean = ocean;
		this.coast = coast;
		this.border = border;
		this.biome = biome;
		this.elevation = elevation;
		this.moisture = moisture;
		neighbours = new ArrayList<Center>();
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public Point getPoint() {
		return point;
	}
	public void setPoint(Point point) {
		this.point = point;
	}
	public boolean isWater() {
		return water;
	}
	public void setWater(boolean water) {
		this.water = water;
	}
	public boolean isOcean() {
		return ocean;
	}
	public void setOcean(boolean ocean) {
		this.ocean = ocean;
	}
	public boolean isCoast() {
		return coast;
	}
	public void setCoast(boolean coast) {
		this.coast = coast;
	}
	public boolean isBorder() {
		return border;
	}
	public void setBorder(boolean border) {
		this.border = border;
	}
	public String getBiome() {
		return biome;
	}
	public void setBiome(String biome) {
		this.biome = biome;
	}
	public float getElevation() {
		return elevation;
	}
	public void setElevation(float elevation) {
		this.elevation = elevation;
	}
	public float getMoisture() {
		return moisture;
	}
	public void setMoisture(float moisture) {
		this.moisture = moisture;
	}
	
	
}
