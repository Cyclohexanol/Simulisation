package utility.geom;

public class Circle 
{
	
	private final Point center;
	private final double radius;
	
	public Circle(Point center, int radius)
	{
		this.center = center;
		this.radius = radius;
	}
	
	public Circle(int centerX, int centerY, int radius)
	{
		this(new Point(centerX,centerY), radius);
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
