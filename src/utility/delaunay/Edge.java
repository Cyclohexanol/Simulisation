package utility.delaunay;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Stack;

import utility.geom.LineSegment;
import utility.geom.Point;
import utility.geom.Rectangle;

public class Edge 
{
	private static Stack<Edge> pool = new Stack<Edge>();
	protected static final Edge DELETED = new Edge();
	
	private Site leftSite, rightSite;
	private int edgeIndex;
	private Vertex leftVertex, rightVertex;
	private Dictionary<LR,Point> clippedVertices;
	private Dictionary<LR, Site> sites;
	private double a,b,c;
	
	protected static Edge createBisectingEdge(Site site0, Site site1)
	{
		double dx, dy, absdx, absdy;
		double a, b, c;
	
		dx = site1.getX() - site0.getX();
		dy = site1.getY() - site0.getY();
		absdx = dx > 0 ? dx : -dx;
		absdy = dy > 0 ? dy : -dy;
		c = site0.getX() * dx + site0.getY() * dy + (dx * dx + dy * dy) * 0.5;
		if (absdx > absdy)
		{
			a = 1.0; b = dy/dx; c /= dx;
		}
		else
		{
			b = 1.0; a = dx/dy; c /= dy;
		}
		
		Edge edge = Edge.create();
	
		edge.leftSite = site0;
		edge.rightSite = site1;
		site0.addEdge(edge);
		site1.addEdge(edge);
		
		edge.leftVertex = null;
		edge.rightVertex = null;
		
		edge.a = a; edge.b = b; edge.c = c;
		
		return edge;
	}
	
	public LineSegment delaunayLine()
	{
		// draw a line connecting the input Sites for which the edge is a bisector:
		return new LineSegment(leftSite.getCoord(), rightSite.getCoord());
	}
	
	private static Edge create()
	{
		Edge edge;
		if (pool.size() > 0)
		{
			edge = pool.pop();
			edge.init();
		}
		else
		{
			edge = new Edge();
		}
		return edge;
	}
	
	private void init()
	{	
		sites = new Hashtable<LR, Site>();
	}
	
	public double sitesDistance()
	{
		return Point.distance(leftSite.getCoord(), rightSite.getCoord());
	}
	
	public LineSegment voronoiEdge()
    {
      if (!visible()) return new LineSegment(null, null);
      return new LineSegment(clippedVertices.get(LR.LEFT),
                             clippedVertices.get(LR.RIGHT));
    }
	
	public static int compareSitesDistances_MAX(Edge edge0, Edge edge1)
	{
		double length0 = edge0.sitesDistance();
		double length1 = edge1.sitesDistance();
		if (length0 < length1)
		{
			return 1;
		}
		if (length0 > length1)
		{
			return -1;
		}
		return 0;
	}
	
	public static int compareSitesDistances(Edge edge0, Edge edge1)
	{
		return - compareSitesDistances_MAX(edge0, edge1);
	}

	public Site getLeftSite() {
		return leftSite;
	}

	public Site getRightSite() {
		return rightSite;
	}

	public Vertex getLeftVertex() {
		return leftVertex;
	}

	public Vertex getRightVertex() {
		return rightVertex;
	}
	
	protected Dictionary<LR,Point> getClippedEnds()
	{
		return clippedVertices;
	}
	
	protected boolean visible()
	{
		return clippedVertices != null;
	}

	protected void setLeftSites(Site s)
	{
		sites.put(LR.LEFT,s);
	}
	
	protected Site getLeftSites()
	{
		return sites.get(LR.LEFT);
	}
	
	protected void setRightSites(Site s)
	{
		sites.put(LR.RIGHT,s);
	}
	
	protected Site getRightSites()
	{
		return sites.get(LR.RIGHT);
	}
	
	protected Site sites(LR leftRight)
	{
		return sites.get(leftRight);
	}
	
	protected boolean isPartOfConvexHull()
	{
		return (leftVertex == null || rightVertex == null);
	}
	
	protected void clipVertices(Rectangle bounds)
	{
		double xmin = bounds.getX();
		double ymin = bounds.getY();
		double xmax = bounds.right();
		double ymax = bounds.bottom();
		
		Vertex vertex0, vertex1;
		double x0,x1,y0,y1;
		
		if (a == 1.0 && b >= 0.0)
		{
			vertex0 = rightVertex;
			vertex1 = leftVertex;
		}
		else 
		{
			vertex0 = leftVertex;
			vertex1 = rightVertex;
		}
	
		if (a == 1.0)
		{
			y0 = ymin;
			if (vertex0 != null && vertex0.getY() > ymin)
			{
				 y0 = vertex0.getY();
			}
			if (y0 > ymax)
			{
				return;
			}
			x0 = c - b * y0;
			
			y1 = ymax;
			if (vertex1 != null && vertex1.getY() < ymax)
			{
				y1 = vertex1.getY();
			}
			if (y1 < ymin)
			{
				return;
			}
			x1 = c - b * y1;
			
			if ((x0 > xmax && x1 > xmax) || (x0 < xmin && x1 < xmin))
			{
				return;
			}
			
			if (x0 > xmax)
			{
				x0 = xmax; y0 = (c - x0)/b;
			}
			else if (x0 < xmin)
			{
				x0 = xmin; y0 = (c - x0)/b;
			}
			
			if (x1 > xmax)
			{
				x1 = xmax; y1 = (c - x1)/b;
			}
			else if (x1 < xmin)
			{
				x1 = xmin; y1 = (c - x1)/b;
			}
		}
		else
		{
			x0 = xmin;
			if (vertex0 != null && vertex0.getX() > xmin)
			{
				x0 = vertex0.getX();
			}
			if (x0 > xmax)
			{
				return;
			}
			y0 = c - a * x0;
			
			x1 = xmax;
			if (vertex1 != null && vertex1.getX() < xmax)
			{
				x1 = vertex1.getX();
			}
			if (x1 < xmin)
			{
				return;
			}
			y1 = c - a * x1;
			
			if ((y0 > ymax && y1 > ymax) || (y0 < ymin && y1 < ymin))
			{
				return;
			}
			
			if (y0 > ymax)
			{
				y0 = ymax; x0 = (c - y0)/a;
			}
			else if (y0 < ymin)
			{
				y0 = ymin; x0 = (c - y0)/a;
			}
			
			if (y1 > ymax)
			{
				y1 = ymax; x1 = (c - y1)/a;
			}
			else if (y1 < ymin)
			{
				y1 = ymin; x1 = (c - y1)/a;
			}
		}

		clippedVertices = new Hashtable<LR,Point>();
		if (vertex0 == leftVertex)
		{
			clippedVertices.put(LR.LEFT,new Point(x0, y0));
			clippedVertices.put(LR.RIGHT,new Point(x1, y1));
		}
		else
		{
			clippedVertices.put(LR.RIGHT,new Point(x0, y0));
			clippedVertices.put(LR.LEFT,new Point(x1, y1));
		}
	}

	public double getA() {
		return a;
	}

	public double getB() {
		return b;
	}

	public double getC() {
		return c;
	}
	
	public Site site(LR leftRight)
	{
		return sites.get(leftRight);
	}
	

	protected void setVertex(LR leftRight, Vertex v)
	{
		if (leftRight == LR.LEFT)
		{
			leftVertex = v;
		}
		else
		{
			rightVertex = v;
		}
	}
	
	
}
