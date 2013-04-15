package ir.exercise1.similarityretrieval.reader.file;

import ir.exercise1.textindexer.model.WeightedInvertedIndex;

import java.io.BufferedReader;
import java.io.IOException;

import weka.core.Instances;

/**
 * Reads an index in the ARFF format.
 * 
 * @author florian@eckerstorfer.co (Florian Eckerstorfer)
 */
public class ArffIndexFileReader
{
	public WeightedInvertedIndex readIndex(BufferedReader reader)
	{
		WeightedInvertedIndex index = new WeightedInvertedIndex();
		
		Instances data;
		int docId;
		
		try {
			data = new Instances(reader);
			reader.close();
			data.setClassIndex(data.numAttributes() - 1);
			
			for (int i = 0; i < data.numInstances(); i++) {
				docId = index.addDocument(data.attribute(1).value(i));
				for (int j = 0; j < data.numAttributes(); j++) {
					index.addToken(data.attribute(j).name(), docId, data.instance(i).value(j));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return index;
	}
}
