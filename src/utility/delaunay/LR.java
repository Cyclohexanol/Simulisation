package utility.delaunay;

public enum LR 
{
	LEFT("left"), RIGHT("right");
	
	private String name;
	
	LR(String name)
	{
		this.name = name;
	}
	
	public static LR other(LR leftRight)
	{
		return leftRight == LEFT ? RIGHT : LEFT;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
