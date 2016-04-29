package utility.delaunay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import utility.geom.*;

public final class Site 
{
	private static Stack<Site> pool = new Stack<Site>();
	private final static double EPSILON = .005;
	
	private Point coord;
	private int siteIndex;
	private double weight;
	private int color;
	private List<Point> region;
	private List<Edge> edges;
	private List<LR> edgeOrientations;
	
	private Site(Point p, int index, double weight, int color)
	{
		init(p, index, weight, color);
	}
	
	public static Site create(Point p, int index, double weight, int color)
	{
		if (pool.size() > 0)
		{
			return pool.pop().init(p, index, weight, color);
		}
		else
		{
			return new Site(p, index, weight, color);
		}
	}
	
	private Site init(Point p, int index, double weight, int color)
	{
		this.coord = p;
		this.siteIndex = index;
		this.weight = weight;
		this.color = color;
		this.edges = new ArrayList<Edge>();
		region = null;
		return this;
	}
	
	public static void sortSites(List<Site> sites)
	{
		sites.sort(new Comparator<Site>() {
			public int compare(Site s1, Site s2) {
				return Site.compare(s1, s2);
			}
		});
	}
	
	public static int compare(Site s1, Site s2)
	{
		int returnVal = Voronoi.compareByYThenX(s1, s2);
		int tempIndex;
		if (returnVal == -1)
		{
			if (s1.siteIndex > s2.siteIndex)
			{
				tempIndex = s1.siteIndex;
				s1.siteIndex = s2.siteIndex;
				s2.siteIndex = tempIndex;
			}
		}
		else if (returnVal == 1)
		{
			if (s2.siteIndex > s1.siteIndex)
			{
				tempIndex = s2.siteIndex;
				s2.siteIndex = s1.siteIndex;
				s1.siteIndex = tempIndex;
			}
			
		}
		
		return returnVal;
	}
	
	public double getX()
	{
		return coord.getX();
	}
	
	public double getY()
	{
		return coord.getY();
	}
	
	@Override
	public String toString()
	{
		return "Site " + siteIndex + ": " + coord;
	}
	
	private void clear()
	{

		if (edges != null)
		{
			edges = null;
		}
		if (edgeOrientations != null)
		{
			edgeOrientations = null;
		}
		if (region != null)
		{
			region = null;
		}
	}
	
	private void move(Point p)
	{
		clear();
		coord = p;
	}
	
	private void dispose()
	{
		coord = null;
		clear();
		pool.push(this);
	}
	
	protected void addEdge(Edge edge)
	{
		edges.add(edge);
	}
	
	protected Edge nearestEdge()
	{
		edges.sort(new Comparator<Edge>() {

			public int compare(Edge edge1, Edge edge2) {
				return Edge.compareSitesDistances(edge1, edge2);
			}
		});
		return edges.get(0);
	}
	
	protected List<Site> neighbourSites()
	{
		if (edges == null || edges.size() == 0)
		{
			return new ArrayList<Site>();
		}
		if (edgeOrientations == null)
		{ 
			reorderEdges();
		}
		List<Site> list = new ArrayList<Site>();

		edges.forEach(e -> list.add(neighborSite(e)));

		return list;
	}
	
	private Site neighborSite(Edge edge)
	{
		if (this == edge.getLeftSite())
		{
			return edge.getRightSite();
		}
		if (this == edge.getRightSite())
		{
			return edge.getLeftSite();
		}
		return null;
	}
	
	public Point getCoord()
	{
		return this.coord;
	}
	
	protected List<Point> region(Rectangle clippingBounds)
	{
		if (edges == null || edges.size() == 0)
		{
			return new ArrayList<Point>();
		}
		if (edgeOrientations == null)
		{ 
			reorderEdges();
			region = clipToBounds(clippingBounds);
			if ((new Polygon(region)).winding() == Winding.CLOCKWISE)
			{
				Collections.reverse(region);
			}
		}
		return region;
	}
	
	private void reorderEdges()
	{
		EdgeReorderer reorderer = new EdgeReorderer(edges, Criterion.VERTEX);
		edges = reorderer.getEdges();
		edgeOrientations = reorderer.getEdgeOrientations();
		reorderer.dispose();
	}
	
	private static boolean closeEnough(Point p0, Point p1)
	{
		return Point.distance(p0, p1) < EPSILON;
	}
	
	private List<Point> clipToBounds(Rectangle bounds)
	{
		List<Point> points = new ArrayList<>();
		int n = edges.size();
		int i = 0;
		Edge edge;
		while (i < n && edges.get(i).visible() == false)
		{
			++i;
		}
		
		if (i == n)
		{
			// no edges visible
			return new ArrayList<Point>();
		}
		edge = edges.get(i);
		LR orientation = edgeOrientations.get(i);
		points.add(edge.getClippedEnds().get(orientation));
		points.add(edge.getClippedEnds().get(LR.other(orientation)));
		
		for (int j = i + 1; j < n; ++j)
		{
			edge = edges.get(j);
			if (edge.visible() == false)
			{
				continue;
			}
			connect(points, j, bounds, false);
		}
		connect(points, i, bounds, true);
		
		return points;
	}
	
	private void connect(List<Point> points, int j, Rectangle bounds, boolean closingUp)
	{
		int npoint = points.size();
		Point rightPoint = points.get(npoint - 1);
		Edge newEdge = edges.get(j);
		LR newOrientation = edgeOrientations.get(j);
		Point newPoint = newEdge.getClippedEnds().get(newOrientation);
		if (!closeEnough(rightPoint, newPoint))
		{
			if (rightPoint.getX() != newPoint.getX()
			&&  rightPoint.getY() != newPoint.getY())
			{
				int rightCheck = BoundsCheck.check(rightPoint, bounds);
				int newCheck = BoundsCheck.check(newPoint, bounds);
				double px, py;
				if (rightCheck == BoundsCheck.RIGHT)
				{
					px = bounds.right();
					if (newCheck == BoundsCheck.BOTTOM)
					{
						py = bounds.bottom();
						points.add(new Point(px, py));
					}
					else if (newCheck == BoundsCheck.TOP)
					{
						py = bounds.top();
						points.add(new Point(px, py));
					}
					else if (newCheck == BoundsCheck.LEFT)
					{
						if (rightPoint.getY()- bounds.getY() + newPoint.getY() - bounds.getY() < bounds.getHeight())
						{
							py = bounds.top();
						}
						else
						{
							py = bounds.bottom();
						}
						points.add(new Point(px, py));
						points.add(new Point(bounds.left(), py));
					}
				}
				else if (rightCheck == BoundsCheck.LEFT)
				{
					px = bounds.left();
					if (newCheck == BoundsCheck.BOTTOM)
					{
						py = bounds.bottom();
						points.add(new Point(px, py));
					}
					else if (newCheck == BoundsCheck.TOP)
					{
						py = bounds.top();
						points.add(new Point(px, py));
					}
					else if (newCheck == BoundsCheck.RIGHT)
					{
						if (rightPoint.getY() - bounds.getY() + newPoint.getY() - bounds.getY() < bounds.getHeight())
						{
							py = bounds.top();
						}
						else
						{
							py = bounds.bottom();
						}
						points.add(new Point(px, py));
						points.add(new Point(bounds.right(), py));
					}
				}
				else if (rightCheck == BoundsCheck.TOP)
				{
					py = bounds.top();
					if (newCheck == BoundsCheck.RIGHT)
					{
						px = bounds.right();
						points.add(new Point(px, py));
					}
					else if (newCheck == BoundsCheck.LEFT)
					{
						px = bounds.left();
						points.add(new Point(px, py));
					}
					else if (newCheck == BoundsCheck.BOTTOM)
					{
						if (rightPoint.getX() - bounds.getX() + newPoint.getX() - bounds.getX() < bounds.getWidth())
						{
							px = bounds.left();
						}
						else
						{
							px = bounds.right();
						}
						points.add(new Point(px, py));
						points.add(new Point(px, bounds.bottom()));
					}
				}
				else if (rightCheck == BoundsCheck.BOTTOM)
				{
					py = bounds.bottom();
					if (newCheck == BoundsCheck.RIGHT)
					{
						px = bounds.right();
						points.add(new Point(px, py));
					}
					else if (newCheck == BoundsCheck.LEFT)
					{
						px = bounds.left();
						points.add(new Point(px, py));
					}
					else if (newCheck == BoundsCheck.TOP)
					{
						if (rightPoint.getX() - bounds.getX() + newPoint.getX() - bounds.getX() < bounds.getWidth())
						{
							px = bounds.left();
						}
						else
						{
							px = bounds.right();
						}
						points.add(new Point(px, py));
						points.add(new Point(px, bounds.top()));
					}
				}
			}
			if (closingUp)
			{
				// newEdge's ends have already been added
				return;
			}
			points.add(newPoint);
		}
		Point newRightPoint = newEdge.getClippedEnds().get(LR.other(newOrientation));
		if (!closeEnough(points.get(0), newRightPoint))
		{
			points.add(newRightPoint);
		}
	}
	
	
}
