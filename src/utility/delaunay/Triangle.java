package utility.delaunay;

import java.util.ArrayList;
import java.util.List;

public class Triangle 
{
	private List<Site> sites;
	
	public Triangle(Site a, Site b, Site c)
	{
		sites = new ArrayList<Site>();
		sites.add(a);
		sites.add(b);
		sites.add(c);
	}
	
	public List<Site> getSites()
	{
		return sites;
	}
	
	public void dispose()
	{
		sites = null;
	}
}
