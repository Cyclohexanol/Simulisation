package utility.graph;

import java.util.ArrayList;

import utility.geom.Point;

public class Corner 
{
	private int index;
	
	private Point point;
	private boolean water, ocean, coast, border;
	private String biome;
	private float elevation, moisture;
	
	ArrayList<Center> touches;
	ArrayList<Edge> protrudes;
	ArrayList<Corner> adjacent;
	
	private int river, watershedSize;
	private Corner downslope, watershed;
	
}
