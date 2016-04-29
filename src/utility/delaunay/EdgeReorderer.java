package utility.delaunay;

import java.util.ArrayList;
import java.util.List;

import utility.geom.Point;

public final class EdgeReorderer 
{
	private List<Edge> edges;
	private List<LR> edgeOrientations;
	
	public EdgeReorderer(List<Edge> origEdges, Criterion crit)
	{
		if (crit != Criterion.SITE && crit != Criterion.VERTEX)
		{
			throw new IllegalArgumentException("Edges: criterion must be Vertex or Site");
		}
		edges = new ArrayList<Edge>();
		edgeOrientations = new ArrayList<LR>();
		if (origEdges.size() > 0)
		{
			edges = reorderEdges(origEdges, crit);
		}
	}
	
	public void dispose()
	{
		edges = null;
		edgeOrientations = null;
	}
	
	public List<Edge> getEdges()
	{
		return edges;
	}
	
	public List<LR> getEdgeOrientations()
	{
		return edgeOrientations;
	}
	
	private List<Edge> reorderEdges(List <Edge> origEdges, Criterion crit)
	{
		int i;
		int j;
		int n = origEdges.size();
		Edge edge;
		// we're going to reorder the edges in order of traversal
		boolean[] done = new boolean[n];
		int nDone = 0;
		for(int k=0; k<n; ++k)
			done[k] = false;
		
		List<Edge> newEdges = new ArrayList<Edge>();
		
		i = 0;
		edge = origEdges.get(i);
		newEdges.add(edge);
		edgeOrientations.add(LR.LEFT);
		Object firstPoint = (crit == Criterion.VERTEX) ? edge.getLeftVertex() : edge.getLeftSite();
		Object lastPoint = (crit == Criterion.VERTEX) ? edge.getRightVertex() : edge.getRightSite();
		
		if (firstPoint.equals(Vertex.VERTEX_AT_INFINITY) || lastPoint.equals(Vertex.VERTEX_AT_INFINITY))
		{
			return new ArrayList<Edge>();
		}
		
		done[i] = true;
		++nDone;
		
		while (nDone < n)
		{
			for (i = 1; i < n; ++i)
			{
				if (done[i])
				{
					continue;
				}
				edge = origEdges.get(i);
				Object leftPoint = (crit == Criterion.VERTEX) ? edge.getLeftVertex() : edge.getLeftSite();
				Object rightPoint = (crit == Criterion.VERTEX) ? edge.getRightVertex() : edge.getRightSite();
				if (leftPoint.equals(Vertex.VERTEX_AT_INFINITY) || rightPoint.equals(Vertex.VERTEX_AT_INFINITY))
				{
					return new ArrayList<Edge>();
				}
				if (leftPoint == lastPoint)
				{
					lastPoint = rightPoint;
					edgeOrientations.add(LR.LEFT);
					newEdges.add(edge);
					done[i] = true;
				}
				else if (rightPoint == firstPoint)
				{
					firstPoint = leftPoint;
					edgeOrientations.add(0,LR.LEFT);
					newEdges.add(0,edge);
					done[i] = true;
				}
				else if (leftPoint == firstPoint)
				{
					firstPoint = rightPoint;
					edgeOrientations.add(0,LR.RIGHT);
					newEdges.add(0,edge);
					done[i] = true;
				}
				else if (rightPoint == lastPoint)
				{
					lastPoint = leftPoint;
					edgeOrientations.add(LR.RIGHT);
					newEdges.add(edge);
					done[i] = true;
				}
				if (done[i])
				{
					++nDone;
				}
			}
		}
		
		return newEdges;
	}
}
