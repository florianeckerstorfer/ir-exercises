package ir.exercise1.similarityretrieval.reader.file;

import ir.exercise1.textindexer.model.WeightedInvertedIndex;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

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
		Map<Integer, Integer> docLengths = new HashMap<Integer, Integer>();

		Instances data;
		int docId;
		String className = "";
		int totalDocLength = 0;
		try {
			data = new Instances(reader);
			reader.close();
			reader = null;
			data.setClassIndex(data.numAttributes() - 1);
			System.out.println("Start to build index based on read ARFF file.");
			int classI = 1;
			
			for (int i = 1; i < data.numInstances(); i++) {
				Instance instance = data.instance(i);
				docId = index.addDocument(data.attribute(1).value(i));
				if (i == 1 || (i % 400) == 0) {
					className = data.attribute(0).value(classI);
					classI++;
				}
				
				if (data.attribute(2).value(i) != null){
					int curDocLength = Integer.parseInt(data.attribute(2).value(i));
					index.getDocLengths().put(docId, curDocLength);
					totalDocLength += curDocLength;
				} // TODO why null for higher i values?
				index.addClassName(docId, className);
				for (int j = 3; j < data.numAttributes(); j++) {
					if (instance.value(j) > 0) {
						index.addToken(data.attribute(j).name(), docId, instance.value(j));
					}
				}
				
				instance = null;
				
				if ((i % 500) == 0) {
					System.out.println(i);
				}
			}
			index.setTotalDocLength(totalDocLength);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		data = null;
		
		return index;
	}
}
