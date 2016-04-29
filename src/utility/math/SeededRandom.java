package utility.math;

public class SeededRandom 
{
	private int seed;
	
	public SeededRandom(int seed)
	{
		this.seed = seed;
	}
	
	public SeededRandom()
	{
		this(1);
	}
	
	public double nextDouble()
	{
		return (gen() / 2147483647);
	}
	
	public int nextIntRange(double min, double max)
	{
		min -= .4999;
		max += .4999;
		return (int) Math.round(min + ((max - min) * nextDouble()));
	}
	
	public double nextDoubleRange(double min, double max)
	{
		return min + ((max - min) * nextDouble());
	}
	
	private int gen()
	{
		//integer version 1, for max int 2^46 - 1 or larger.
		return seed = (seed * 16807) % 2147483647;
	}
}
