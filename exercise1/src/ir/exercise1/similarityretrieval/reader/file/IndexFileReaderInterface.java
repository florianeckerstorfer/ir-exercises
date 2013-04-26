package ir.exercise1.similarityretrieval.reader.file;

import ir.exercise1.textindexer.model.WeightedInvertedIndex;

import java.io.BufferedReader;

/**
 * Interface for index file readers.
 * 
 * @author florian@eckerstorfer.co (Florian Eckerstorfer)
 */
public interface IndexFileReaderInterface
{
	public WeightedInvertedIndex readIndex(BufferedReader reader);
}
