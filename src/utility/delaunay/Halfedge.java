package utility.delaunay;

import java.util.Stack;

import utility.geom.Point;

public class Halfedge 
{
	private static Stack<Halfedge> pool = new Stack<Halfedge>();
	
	private Halfedge edgeListLeftNeighbor, edgeListRightNeighbor;
	private Halfedge nextInPriorityQueue;
	
	private Edge edge;
	private LR leftRight;
	private Vertex vertex;
	
	private double ystar;
	
	public static Halfedge create(Edge edge, LR lr)
	{
		if (pool.size() > 0)
		{
			return pool.pop().init(edge, lr);
		}
		else
		{
			return new Halfedge(edge, lr);
		}
	}
	
	public static Halfedge createDummy()
	{
		return create(null, null);
	}
	
	public Halfedge(Edge edge, LR lr)
	{	
		init(edge, lr);
	}
	
	private Halfedge init(Edge edge, LR lr)
	{
		this.edge = edge;
		this.leftRight = lr;
		this.nextInPriorityQueue = null;
		this.vertex = null;
		return this;
	}
	
	protected boolean isLeftOf(Point p)
	{
		Site topSite;
		boolean rightOfSite, above, fast;
		double dxp, dyp, dxs, t1, t2, t3, yl;
		
		topSite = edge.getRightSite();
		rightOfSite = p.getX() > topSite.getX();
		if (rightOfSite && this.leftRight == LR.LEFT)
		{
			return true;
		}
		if (!rightOfSite && this.leftRight == LR.RIGHT)
		{
			return false;
		}
		
		if (edge.getA() == 1.0)
		{
			dyp = p.getY() - topSite.getY();
			dxp = p.getX() - topSite.getX();
			fast = false;
			if ((!rightOfSite && edge.getB() < 0.0) || (rightOfSite && edge.getB() >= 0.0) )
			{
				above = dyp >= edge.getB() * dxp;	
				fast = above;
			}
			else 
			{
				above = p.getX() + p.getY() * edge.getB() > edge.getC();
				if (edge.getB() < 0.0)
				{
					above = !above;
				}
				if (!above)
				{
					fast = true;
				}
			}
			if (!fast)
			{
				dxs = topSite.getX() - edge.getLeftSite().getX();
				above = edge.getB() * (dxp * dxp - dyp * dyp) <
				        dxs * dyp * (1.0 + 2.0 * dxp/dxs + edge.getB() * edge.getB());
				if (edge.getB() < 0.0)
				{
					above = !above;
				}
			}
		}
		else 
		{
			yl = edge.getC() - edge.getA() * p.getX();
			t1 = p.getY() - yl;
			t2 = p.getX() - topSite.getX();
			t3 = yl - topSite.getY();
			above = t1 * t1 > t2 * t2 + t3 * t3;
		}
		return this.leftRight == LR.LEFT ? above : !above;
	}

	public Edge getEdge() {
		return edge;
	}

	public LR getLeftRight() {
		return leftRight;
	}

	public Halfedge getNextInPriorityQueue() {
		return nextInPriorityQueue;
	}

	public void setNextInPriorityQueue(Halfedge nextInPriorityQueue) {
		this.nextInPriorityQueue = nextInPriorityQueue;
	}

	public Halfedge getEdgeListLeftNeighbor() {
		return edgeListLeftNeighbor;
	}

	public void setEdgeListLeftNeighbor(Halfedge edgeListLeftNeighbor) {
		this.edgeListLeftNeighbor = edgeListLeftNeighbor;
	}

	public Halfedge getEdgeListRightNeighbor() {
		return edgeListRightNeighbor;
	}

	public void setEdgeListRightNeighbor(Halfedge edgeListRightNeighbor) {
		this.edgeListRightNeighbor = edgeListRightNeighbor;
	}

	public Vertex getVertex() {
		return vertex;
	}

	public double getYstar() {
		return ystar;
	}

	public void setEdge(Edge edge) {
		this.edge = edge;
	}

	public void setLeftRight(LR leftRight) {
		this.leftRight = leftRight;
	}

	public void setVertex(Vertex vertex) {
		this.vertex = vertex;
	}

	public void setYstar(double ystar) {
		this.ystar = ystar;
	}
	
	public void dispose()
	{
		if (edgeListLeftNeighbor != null || edgeListRightNeighbor != null)
		{
			// still in EdgeList
			return;
		}
		if (nextInPriorityQueue != null)
		{
			// still in PriorityQueue
			return;
		}
		edge = null;
		leftRight = null;
		vertex = null;
		pool.push(this);
	}
	
	public void reallyDispose()
	{
		edgeListLeftNeighbor = null;
		edgeListRightNeighbor = null;
		nextInPriorityQueue = null;
		edge = null;
		leftRight = null;
		vertex = null;
		pool.push(this);
	}
}
