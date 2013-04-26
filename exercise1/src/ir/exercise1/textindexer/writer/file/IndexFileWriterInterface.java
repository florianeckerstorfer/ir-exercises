package ir.exercise1.textindexer.writer.file;

import ir.exercise1.textindexer.model.WeightedInvertedIndex;

public interface IndexFileWriterInterface
{
	public void buildIndexFile(WeightedInvertedIndex index, boolean stemming, double lowerThreshold, double upperThreshold);
	
}
