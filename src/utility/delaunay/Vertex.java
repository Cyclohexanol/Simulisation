package utility.delaunay;

import java.util.Stack;

import utility.geom.Point;

public class Vertex 
{
	protected static Vertex VERTEX_AT_INFINITY = new Vertex(Double.NaN, Double.NaN);
	private Point coord;
	private static Stack<Vertex> pool = new Stack<Vertex>();
	private static int nvertices = 0;
	private int vertexIndex;
	
	private static Vertex create(double x, double y)
	{
		if (Double.isNaN(x) || Double.isNaN(y))
		{
			return VERTEX_AT_INFINITY;
		}
		if (pool.size() > 0)
		{
			return pool.pop().init(x, y);
		}
		else
		{
			return new Vertex(x, y);
		}
	}
	
	private Vertex init(double x, double y)
	{
		coord = new Point(x, y);
		return this;
	}
	
	public Vertex(double x, double y)
	{		
		init(x, y);
	}
	
	public Point getCoord()
	{
		return coord;
	}
	
	public double getX()
	{
		return coord.getX();
	}
	
	public double getY()
	{
		return coord.getY();
	}
	
	public int getVertexIndex()
	{
		return vertexIndex;
	}
	
	public static Vertex intersect(Halfedge halfedge0, Halfedge halfedge1)
	{
		Edge edge0, edge1, edge;
		Halfedge halfedge;
		double determinant, intersectionX, intersectionY;
		boolean rightOfSite;
	
		edge0 = halfedge0.getEdge();
		edge1 = halfedge1.getEdge();
		if (edge0 == null || edge1 == null)
		{
			return null;
		}
		if (edge0.getRightSite() == edge1.getRightSite())
		{
			return null;
		}
	
		determinant = edge0.getA() * edge1.getB() - edge0.getB() * edge1.getA();
		if (-1.0e-10 < determinant && determinant < 1.0e-10)
		{
			return null;
		}
	
		intersectionX = (edge0.getC() * edge1.getB() - edge1.getC() * edge0.getB())/determinant;
		intersectionY = (edge1.getC() * edge0.getA() - edge0.getC() * edge1.getA())/determinant;
	
		if (Voronoi.compareByYThenX(edge0.getRightSite(), edge1.getRightSite()) < 0)
		{
			halfedge = halfedge0; edge = edge0;
		}
		else
		{
			halfedge = halfedge1; edge = edge1;
		}
		rightOfSite = intersectionX >= edge.getRightSite().getX();
		if ((rightOfSite && halfedge.getLeftRight() == LR.LEFT)
		||  (!rightOfSite && halfedge.getLeftRight() == LR.RIGHT))
		{
			return null;
		}
	
		return Vertex.create(intersectionX, intersectionY);
	}
	
	public void setIndex()
	{
		vertexIndex = ++nvertices;
	}
	
	public void dispose()
	{
		coord = null;
		pool.push(this);
	}
}
