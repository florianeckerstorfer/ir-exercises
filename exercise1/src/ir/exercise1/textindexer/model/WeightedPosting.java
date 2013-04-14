package ir.exercise1.textindexer.model;

public class WeightedPosting implements PostingInterface
{
	double tfIdf = 0.0;
	
	public WeightedPosting(Double tfIdf)
	{
		this.tfIdf = tfIdf;  
	}
	
	public double getTfIdf()
	{
		return tfIdf;
	}
}
