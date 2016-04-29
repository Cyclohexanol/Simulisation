package utility.geom;

import java.util.List;

public class Polygon 
{
	private final List<Point> vertices;

	public Polygon(List<Point> vertices) {
		super();
		this.vertices = vertices;
	}
	
	public float area()
	{
		return (float) (Math.abs(signedDoubleArea()) * 0.5);
	}
	
	public Winding winding()
	{
		double signedDoubleArea = signedDoubleArea();
		if (signedDoubleArea < 0)
		{
			return Winding.CLOCKWISE;
		}
		if (signedDoubleArea > 0)
		{
			return Winding.COUNTERCLOCKWISE;
		}
		return Winding.NONE;
	}
	
	private float signedDoubleArea()
	{
		int index, nextIndex;
		int n = vertices.size();
		Point current, next;
		float signedDoubleArea = 0;
		for (index = 0; index < n; ++index)
		{
			nextIndex = (index + 1) % n;
			current = vertices.get(index);
			next = vertices.get(nextIndex);
			signedDoubleArea += current.getX() * next.getY() - next.getX() * current.getY();
		}
		return signedDoubleArea;
	}
}
