package utility.geom;

public class Circle 
{
	
	private final Point center;
	private final double radius;
	
	public Circle(Point center, double radius)
	{
		this.center = center;
		this.radius = radius;
	}
	
	public Circle(double x, double y, double radius)
	{
		this(new Point(x,y), radius);
	}

	public Point getCenter() {
		return center;
	}

	public double getRadius() {
		return radius;
	}
	
	@Override
	public String toString()
	{
		return "Circle (center: " + center + "; radius: " + radius + ")";
	}
	
}
