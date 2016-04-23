package utility.geom;

public class Polygon 
{
	private final Point[] vertices;

	public Polygon(Point[] vertices) {
		super();
		this.vertices = vertices;
	}
	
	public float area()
	{
		return (float) (Math.abs(signedDoubleArea()) * 0.5);
	}
	
	public String winding()
	{
		float signedDoubleArea = signedDoubleArea();
		if (signedDoubleArea < 0)
		{
			return "clockwise";
		}
		if (signedDoubleArea > 0)
		{
			return "counterclockwise";
		}
		return "none";
	}
	
	private float signedDoubleArea()
	{
		int index, nextIndex;
		int n = vertices.length;
		Point current, next;
		float signedDoubleArea = 0;
		for (index = 0; index < n; ++index)
		{
			nextIndex = (index + 1) % n;
			current = vertices[index];
			next = vertices[nextIndex];
			signedDoubleArea += current.getX() * next.getY() - next.getX() * current.getY();
		}
		return signedDoubleArea;
	}
}
