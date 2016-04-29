package utility.delaunay;

import java.util.ArrayList;
import java.util.List;

import utility.geom.Circle;
import utility.geom.Point;
import utility.geom.Rectangle;

public class SiteList 
{
	private List<Site> sites;
	private int currentIndex;
	private boolean sorted;
	
	public SiteList()
	{
		sites = new ArrayList<>();
		sorted = false;
	}
	
	public boolean add(Site s)
	{
		sorted = false;
		return sites.add(s);
	}
	
	public int size()
	{
		return sites.size();
	}
	
	public Site next()
	{
		if (sorted == false)
		{
			throw new Error("SiteList::next():  sites have not been sorted");
		}
		if (currentIndex < sites.size())
		{
			return sites.get(currentIndex++);
		}
		else
		{
			return null;
		}
	}
	
	protected Rectangle getSitesBounds()
	{
		if (sorted == false)
		{
			Site.sortSites(sites);
			currentIndex = 0;
			sorted = true;
		}
		double xmin, xmax, ymin, ymax;
		if (sites.size() == 0)
		{
			return new Rectangle(0, 0, 0, 0);
		}
		
		xmin = Double.MAX_VALUE;
		xmax = Double.MIN_VALUE;
		
		for(int i=0; i<sites.size(); ++i)
		{
			if (sites.get(i).getX() < xmin)
			{
				xmin = sites.get(i).getX();
			}
			if (sites.get(i).getX() > xmax)
			{
				xmax = sites.get(i).getX();
			}
		}
		
		ymin = sites.get(0).getY();
		ymax = sites.get(sites.size() - 1).getY();
		
		return new Rectangle(xmin, ymin, xmax - xmin, ymax - ymin);
	}
	
	public List<Point> siteCoords()
	{
		List<Point> coords = new ArrayList<>();
		sites.forEach(site ->
		{
			coords.add(site.getCoord());
		});
		return coords;
	}
	
	public List<Circle> circles()
	{
		List<Circle> circles = new ArrayList<Circle>();
		sites.forEach(site ->
		{
			double radius = 0;
			Edge nearestEdge = site.nearestEdge();
			
			if(!nearestEdge.isPartOfConvexHull())
				radius = nearestEdge.sitesDistance() * 0.5;
			circles.add(new Circle(site.getX(), site.getY(), radius));
		});
		return circles;
	}
}
