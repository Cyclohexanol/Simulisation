package utility.geom;

public enum Winding 
{
	CLOCKWISE("clockwise"), COUNTERCLOCKWISE("counterclockwise"), NONE("none");
	
	private String name;
	
	Winding(String name)
	{
		this.name = name;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
