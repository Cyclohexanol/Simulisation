package utility.polygonmap;

import java.util.ArrayList;
import java.util.List;

import utility.geom.Point;
import utility.graph.Center;
import utility.graph.Edge;
import utility.math.SeededRandom;

public class NoisyEdges 
{
	static public final double NOISY_LINE_TRADEOFF = 0.5;
	
	private List<Point> path0, path1;
	
	public NoisyEdges() 
	{
		this.path0 = new ArrayList<Point>();
		this.path1 = new ArrayList<Point>();
	}
	
	static public List<Point> buildNoisyLineSegments(SeededRandom random, Point A, Point B, Point C, Point D, double minLength)
	{
	      List<Point> points = new ArrayList<Point>();

	      points.add(A);
	      subdivide(random, points, A, B, C, D, minLength);
	      points.add(C);
	      return points;
	      }

	private static void subdivide(SeededRandom random, List<Point> points, Point A, Point B, Point C, Point D, double minLength)
	{
        if (A.subtract(C).length() < minLength || B.subtract(D).length() < minLength) {
          return;
        }

        // Subdivide the quadrilateral
        double p = random.nextDoubleRange(0.2, 0.8);  // vertical (along A-D and B-C)
        double q = random.nextDoubleRange(0.2, 0.8);  // horizontal (along A-B and D-C)

        // Midpoints
        Point E = Point.interpolate(A, D, p);
        Point F = Point.interpolate(B, C, p);
        Point G = Point.interpolate(A, B, q);
        Point I = Point.interpolate(D, C, q);
        
        // Central point
        Point H = Point.interpolate(E, F, q);
        
        // Divide the quad into subquads, but meet at H
        double s = 1.0 - random.nextDoubleRange(-0.4, +0.4);
        double t = 1.0 - random.nextDoubleRange(-0.4, +0.4);

        subdivide(random, points, A, Point.interpolate(G, B, s), H, Point.interpolate(E, D, t), minLength);
        points.add(H);
        subdivide(random, points, H, Point.interpolate(F, C, s), C, Point.interpolate(I, D, t), minLength);
        }
	
	public void buildNoisyEdges(Map map, Lava lava, SeededRandom random) {
	      for(int i=0; i<map.getCenters().size(); ++i) {
	    	  Center p = map.getCenters().get(i);
	    	  for(int j=0; j<p.getBorders().size(); ++j) {
	    		  Edge edge = p.getBorders().get(j);
	              if (edge.getD0() != null && edge.getD1() != null && edge.getD0() != null && edge.getD1() != null && path0.get(edge.getIndex()) == null) {
	              double f = NOISY_LINE_TRADEOFF;
	              Point t = Point.interpolate(edge.getV0().getPoint(), edge.getD0().getPoint(), f);
	              Point q = Point.interpolate(edge.getV0().getPoint(), edge.getD1().getPoint(), f);
	              Point r = Point.interpolate(edge.getV1().getPoint(), edge.getD0().getPoint(), f);
	              Point s = Point.interpolate(edge.getV1().getPoint(), edge.getD1().getPoint(), f);
	
	              int minLength = 10;
	              if (edge.getD0().getBiome() != edge.getD1().getBiome()) minLength = 3;
	              if (edge.getD0().isOcean() && edge.getD1().isOcean()) minLength = 100;
	              if (edge.getD0().isCoast() || edge.getD1().isCoast()) minLength = 1;
	              if (edge.isRiver() || lava.getLava().get(edge.getIndex())) minLength = 1;
	                
	              path0.addAll(edge.getIndex(), buildNoisyLineSegments(random, edge.getV0().getPoint(), t, edge.getMidPoint(), q, minLength));
	              path1.addAll(edge.getIndex(), buildNoisyLineSegments(random, edge.getV1().getPoint(), s, edge.getMidPoint(), r, minLength));
	              }
	            }
	        }
	    }
}
