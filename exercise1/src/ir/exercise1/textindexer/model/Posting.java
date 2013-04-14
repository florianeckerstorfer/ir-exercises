package ir.exercise1.textindexer.model;

public class Posting implements PostingInterface
{
	int count = 0;
	
	public Posting(int count)
	{
		this.count = count;
	}
	
	public void incrementCount()
	{
		count++;
	}
	
	public double getCount()
	{
		return count;
	}
}
