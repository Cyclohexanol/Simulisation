package utility.graph;

import java.util.ArrayList;
import java.util.List;

import utility.geom.Point;

public class Center {
	private int index;

	private Point point;
	private boolean water, ocean, coast, border;
	private String biome;
	private double elevation, moisture;

	List<Center> neighbours;
	List<Edge> borders;
	List<Corner> corners;

	public Center(int index, Point point)
	{
		this(index,point,false,false,false,false,null,0,0);
	}
	
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
		borders = new ArrayList<Edge>();
		corners = new ArrayList<Corner>();
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

	public double getElevation() {
		return elevation;
	}

	public void setElevation(double d) {
		this.elevation = d;
	}

	public double getMoisture() {
		return moisture;
	}

	public void setMoisture(double moisture) {
		this.moisture = moisture;
	}

	public List<Center> getNeighbours() {
		return neighbours;
	}

	public List<Edge> getBorders() {
		return borders;
	}

	public List<Corner> getCorners() {
		return corners;
	}

}
