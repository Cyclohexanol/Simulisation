package utility.delaunay;

import java.util.ArrayList;
import java.util.List;

import utility.geom.Point;

public class EdgeList {
	
	private double deltax;
	private double xmin;
	
	private int hashsize;
	private List<Halfedge> hash;
	private Halfedge leftEnd;
	private Halfedge rightEnd;
	
	public EdgeList(double xmin, double deltax, int sqrt_nsites)
	{
		this.xmin = xmin;
		this.deltax = deltax;
		this.hashsize = 2 * sqrt_nsites;

		int i;
		hash = new ArrayList<Halfedge>(hashsize);
		
		// two dummy Halfedges:
		leftEnd = Halfedge.createDummy();
		rightEnd = Halfedge.createDummy();
		leftEnd.setEdgeListLeftNeighbor(null);
		leftEnd.setEdgeListRightNeighbor(rightEnd);
		rightEnd.setEdgeListLeftNeighbor(leftEnd);
		rightEnd.setEdgeListRightNeighbor(null);
		hash.set(0,leftEnd);
		hash.set(hashsize-1,rightEnd);
	}
	
	public Halfedge edgeListLeftNeighbor(Point p)
	{
		int i, bucket;
		Halfedge halfEdge;
	
		/* Use hash table to get close to desired halfedge */
		bucket = (int) ((p.getX() - xmin)/deltax * hashsize);
		if (bucket < 0)
		{
			bucket = 0;
		}
		if (bucket >= hashsize)
		{
			bucket = hashsize - 1;
		}
		halfEdge = getHash(bucket);
		if (halfEdge == null)
		{
			for (i = 1; true ; ++i)
		    {
				if ((halfEdge = getHash(bucket - i)) != null) break;
				if ((halfEdge = getHash(bucket + i)) != null) break;
		    }
		}
		/* Now search linear list of halfedges for the correct one */
		if (halfEdge == leftEnd  || (halfEdge != rightEnd && halfEdge.isLeftOf(p)))
		{
			do
			{
				halfEdge = halfEdge.getEdgeListRightNeighbor();
			}
			while (halfEdge != rightEnd && halfEdge.isLeftOf(p));
			halfEdge = halfEdge.getEdgeListLeftNeighbor();
		}
		else
		{
			do
			{
				halfEdge = halfEdge.getEdgeListLeftNeighbor();
			}
			while (halfEdge != leftEnd && !halfEdge.isLeftOf(p));
		}
	
		/* Update hash table and reference counts */
		if (bucket > 0 && bucket <hashsize - 1)
		{
			hash.set(bucket, halfEdge);
		}
		return halfEdge;
	}
	
	private Halfedge getHash(int b)
	{
		Halfedge halfEdge;
	
		if (b < 0 || b >= hashsize)
		{
			return null;
		}
		halfEdge = hash.get(b); 
		if (halfEdge != null && halfEdge.getEdge().equals(Edge.DELETED))
		{
			/* Hash table points to deleted halfedge.  Patch as necessary. */
			hash.set(b, null);
			// still can't dispose halfEdge yet!
			return null;
		}
		else
		{
			return halfEdge;
		}
	}
	
	public void insert(Halfedge lb, Halfedge newHalfedge)
	{
		newHalfedge.setEdgeListLeftNeighbor(lb);
		newHalfedge.setEdgeListRightNeighbor(lb.getEdgeListRightNeighbor());
		lb.getEdgeListRightNeighbor().setEdgeListLeftNeighbor(newHalfedge);
		lb.setEdgeListRightNeighbor(newHalfedge);
	}
	
	public void remove(Halfedge halfEdge)
	{
		halfEdge.getEdgeListLeftNeighbor().setEdgeListRightNeighbor(halfEdge.getEdgeListRightNeighbor());
		halfEdge.getEdgeListRightNeighbor().setEdgeListLeftNeighbor(halfEdge.getEdgeListLeftNeighbor());
		halfEdge.setEdge(Edge.DELETED);
		halfEdge.setEdgeListLeftNeighbor(null); 
		halfEdge.setEdgeListRightNeighbor(null);
	}
	
	public void dispose()
	{
		Halfedge halfEdge = leftEnd;
		Halfedge prevHe;
		while (halfEdge != rightEnd)
		{
			prevHe = halfEdge;
			halfEdge = halfEdge.getEdgeListRightNeighbor();
			prevHe.dispose();
		}
		leftEnd = null;
		rightEnd.dispose();
		rightEnd = null;

		int i;
		for (i = 0; i < hashsize; ++i)
		{
			hash.set(i, null);
		}
		hash = null;
	}
}
