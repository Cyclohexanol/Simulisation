package utility.delaunay;

import utility.geom.Point;
import utility.geom.Rectangle;

public class BoundsCheck 
{
	public static int TOP = 1;
	public static int BOTTOM = 2;
	public static int LEFT = 4;
	public static int RIGHT = 8;

	public static int check(Point point, Rectangle bounds)
	{
		int value = 0;
		if (point.getX() == bounds.left())
		{
			value |= LEFT;
		}
		if (point.getX() == bounds.right())
		{
			value |= RIGHT;
		}
		if (point.getY() == bounds.top())
		{
			value |= TOP;
		}
		if (point.getY() == bounds.bottom())
		{
			value |= BOTTOM;
		}
		return value;
	}
}
