package utility.geom;

public class LineSegment {

	private final Point p0, p1;
	
	public LineSegment(Point p0, Point p1)
	{
		this.p0 = p0;
		this.p1 = p1;
	}
	
	public static int compareLengths(LineSegment segment0, LineSegment segment1)
	{
		double length0 = Point.distance(segment0.p0, segment0.p1);
		double length1 = Point.distance(segment1.p0, segment1.p1);
		
		if(length0<length1)
			return 1;
		if(length1<length0)
			return -1;
		return 0;
	}
}
