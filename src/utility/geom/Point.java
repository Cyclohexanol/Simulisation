package utility.geom;

public class Point 
{
	private double x,y;

	public Point(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	public double distance(double x, double y)
	{
		return Math.sqrt(Math.pow(x-this.x,2)+Math.pow(y-this.y,2));
	}
	
	public double distance(Point p2)
	{
		return Math.sqrt(Math.pow(p2.x-this.x,2)+Math.pow(p2.y-this.y,2));
	}
	
	public static double distance(Point p1, Point p2)
	{
		return Math.sqrt(Math.pow(p2.x-p1.x,2)+Math.pow(p2.y-p1.y,2));
	}

	@Override
	public String toString()
	{
		return "["+x+","+y+"]";
	}

	public static Point interpolate(Point p1, Point p2, double f)
	{
		double dx = p2.x - p1.x;
		double dy = p2.y - p1.y;
		
		return new Point(p1.x + (dx/f), p1.y + (dy/f));
	}

	public Point subtract(Point p1)
	{
		double x = this.x - p1.x;
		double y = this.y - p1.y;
		
		return new Point(x,y);
	}
	
	public double length()
	{
		return Math.abs(Math.sqrt(x*x + y*y));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}
