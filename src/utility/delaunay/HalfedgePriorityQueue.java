package utility.delaunay;

import java.util.ArrayList;
import java.util.List;

import utility.geom.Point;

public class HalfedgePriorityQueue 
{
	private List<Halfedge> hash;
	private int count;
	private int minBucket;
	private int hashsize;
	
	private double ymin;
	private double deltay;
	
	public HalfedgePriorityQueue(double ymin, double deltay, int sqrt_nsites)
	{
		this.ymin = ymin;
		this.deltay = deltay;
		this.hashsize = 4 * sqrt_nsites;
		initialize();
	}
	
	private void initialize()
	{
		int i;
	
		count = 0;
		minBucket = 0;
		hash = new ArrayList<Halfedge>(hashsize);
		// dummy Halfedge at the top of each hash
		for (i = 0; i < hashsize; ++i)
		{
			hash.set(i,Halfedge.createDummy());
			hash.get(i).setNextInPriorityQueue(null);
		}
	}
	
	public boolean isEmpty()
	{
		return count == 0;
	}
	
	public Point min()
	{
		adjustMinBucket();
		Halfedge answer = hash.get(minBucket).getNextInPriorityQueue();
		return new Point(answer.getVertex().getX(), answer.getYstar());
	}
	
	private void adjustMinBucket()
	{
		while (minBucket < hashsize - 1 && isEmpty(minBucket))
		{
			++minBucket;
		}
	}
	
	private boolean isEmpty(int bucket)
	{
		return (hash.get(bucket).getNextInPriorityQueue() == null);
	}
	
	public void remove(Halfedge halfEdge)
	{
		Halfedge previous;
		int removalBucket = bucket(halfEdge);
		
		if (halfEdge.getVertex() != null)
		{
			previous = hash.get(removalBucket);
			while (previous.getNextInPriorityQueue() != halfEdge)
			{
				previous = previous.getNextInPriorityQueue();
			}
			previous.setNextInPriorityQueue(halfEdge.getNextInPriorityQueue());
			--count;
			halfEdge.setVertex(null);
			halfEdge.setNextInPriorityQueue(null);
			halfEdge.dispose();
		}
	}
	
	private int bucket(Halfedge halfEdge)
	{
		int theBucket = (int) ((halfEdge.getYstar() - ymin)/deltay * hashsize);
		if (theBucket < 0) theBucket = 0;
		if (theBucket >= hashsize) theBucket = hashsize - 1;
		return theBucket;
	}
	

	public void insert(Halfedge halfEdge)
	{
		Halfedge previous, next;
		int insertionBucket = bucket(halfEdge);
		if (insertionBucket < minBucket)
		{
			minBucket = insertionBucket;
		}
		previous = hash.get(insertionBucket);
		while ((next = previous.getNextInPriorityQueue()) != null
		&&     (halfEdge.getYstar()  > next.getYstar() || (halfEdge.getYstar() == next.getYstar() && halfEdge.getVertex().getX() > next.getVertex().getX())))
		{
			previous = next;
		}
		halfEdge.setNextInPriorityQueue(previous.getNextInPriorityQueue()); 
		previous.setNextInPriorityQueue(halfEdge);
		++count;
	}
	
	public Halfedge extractMin()
	{
		Halfedge answer;
	
		// get the first real Halfedge in _minBucket
		answer = hash.get(minBucket).getNextInPriorityQueue();
		
		hash.get(minBucket).setNextInPriorityQueue(answer.getNextInPriorityQueue());
		--count;
		answer.setNextInPriorityQueue(null);
		
		return answer;
	}
	
	public void dispose()
	{
		// get rid of dummies
		for (int i = 0; i < hashsize; ++i)
		{
			hash.get(i).dispose();
			hash.set(i, null);
		}
		hash = null;
	}
}
