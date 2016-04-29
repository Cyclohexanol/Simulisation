package utility.graph;

import utility.geom.Point;

public class Edge {

	private int index;
	private Center d0, d1;
	private Corner v0, v1;
	private Point midPoint;
	private int river;
	
	public Edge(int index)
	{
		this(index, null, null, null, null, null, 0);
	}

	public Edge(int index, Center d0, Center d1, Corner v0, Corner v1, Point midPoint, int river) {
		super();
		this.index = index;
		this.d0 = d0;
		this.d1 = d1;
		this.v0 = v0;
		this.v1 = v1;
		this.midPoint = midPoint;
		this.river = river;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Center getD0() {
		return d0;
	}

	public void setD0(Center d0) {
		this.d0 = d0;
	}

	public Center getD1() {
		return d1;
	}

	public void setD1(Center d1) {
		this.d1 = d1;
	}

	public Corner getV0() {
		return v0;
	}

	public void setV0(Corner v0) {
		this.v0 = v0;
	}

	public Corner getV1() {
		return v1;
	}

	public void setV1(Corner v1) {
		this.v1 = v1;
	}

	public Point getMidPoint() {
		return midPoint;
	}

	public void setMidPoint(Point midPoint) {
		this.midPoint = midPoint;
	}

	public int getRiver() {
		return river;
	}

	public void setRiver(int river) {
		this.river = river;
	}

	public boolean isRiver() {
		return river != 0;
	}
}
