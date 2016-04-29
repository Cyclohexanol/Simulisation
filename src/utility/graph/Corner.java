package utility.graph;

import java.util.ArrayList;

import utility.geom.Point;

public class Corner 
{
	private int index;
	
	private Point point;
	private boolean water, ocean, coast, border;
	private String biome;
	private double elevation, moisture;
	
	ArrayList<Center> touches;
	ArrayList<Edge> protrudes;
	ArrayList<Corner> adjacent;
	
	private int river, watershedSize;
	private Corner downslope, watershed;
	
	public Corner(int index)
	{
		this(index, null, false, false, false, false, null, 0, 0, 0, 0, null, null);
	}
	
	public Corner(int index, Point point, boolean water, boolean ocean, boolean coast, boolean border, String biome,
			float elevation, float moisture, int river, int watershedSize, Corner downslope, Corner watershed) {
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
		this.river = river;
		this.watershedSize = watershedSize;
		this.downslope = downslope;
		this.watershed = watershed;
		touches = new ArrayList<Center>();
		protrudes = new ArrayList<Edge>();
		adjacent = new ArrayList<Corner>();
	}

	public Point getPoint() {
		return point;
	}

	public int getIndex() {
		return index;
	}

	public boolean isWater() {
		return water;
	}

	public boolean isOcean() {
		return ocean;
	}

	public boolean isCoast() {
		return coast;
	}

	public boolean isBorder() {
		return border;
	}

	public String getBiome() {
		return biome;
	}

	public double getElevation() {
		return elevation;
	}

	public double getMoisture() {
		return moisture;
	}

	public int getRiver() {
		return river;
	}

	public int getWatershedSize() {
		return watershedSize;
	}

	public Corner getDownslope() {
		return downslope;
	}

	public Corner getWatershed() {
		return watershed;
	}

	public ArrayList<Center> getTouches() {
		return touches;
	}

	public ArrayList<Edge> getProtrudes() {
		return protrudes;
	}

	public ArrayList<Corner> getAdjacent() {
		return adjacent;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public void setWater(boolean water) {
		this.water = water;
	}

	public void setOcean(boolean ocean) {
		this.ocean = ocean;
	}

	public void setCoast(boolean coast) {
		this.coast = coast;
	}

	public void setBorder(boolean border) {
		this.border = border;
	}

	public void setBiome(String biome) {
		this.biome = biome;
	}

	public void setElevation(double d) {
		this.elevation = d;
	}

	public void setMoisture(double moisture) {
		this.moisture = moisture;
	}

	public void setRiver(int river) {
		this.river = river;
	}

	public void setWatershedSize(int watershedSize) {
		this.watershedSize = watershedSize;
	}

	public void setDownslope(Corner downslope) {
		this.downslope = downslope;
	}

	public void setWatershed(Corner watershed) {
		this.watershed = watershed;
	}
	
	
	
}
