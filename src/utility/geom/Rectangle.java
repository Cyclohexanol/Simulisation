package utility.geom;

public class Rectangle {
	
	private double x,y,width,height;
	
	public Rectangle(double x, double y, double width, double height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public double left()
	{ 
		return x;
	}
	
	public double right()
	{
		return x + width;
	}
	
	public double top()
	{
		return y;
	}
	
	public double bottom()
	{
		return y + height;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}
	
	
}
