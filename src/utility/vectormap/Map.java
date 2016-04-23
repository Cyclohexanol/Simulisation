package utility.vectormap;

import java.util.ArrayList;
import java.util.List;

import utility.geom.*;
import utility.graph.*;

public class Map 
{
	public static final double LAKE_THRESHOLD = 0.3; // 0 to 1, fraction of water corners for water polygon
	
	private boolean needsMoreRandomness; 
	private int size;
	private int nbPoints;
	private List<Point> points;
	private List<Corner> corner;
	private List<Edge> edges;
	private List<Center> center;
	
	
	public Map(int size)
	{
		this.size = size;
		nbPoints = 0;
	}
	
	
}
