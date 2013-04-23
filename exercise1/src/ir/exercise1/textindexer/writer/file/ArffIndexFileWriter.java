package ir.exercise1.textindexer.writer.file;

import ir.exercise1.textindexer.model.WeightedInvertedIndex;
import ir.exercise1.textindexer.model.WeightedPosting;
import ir.exercise1.textindexer.model.WeightedPostingList;

import java.io.PrintStream;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * ArffFileWriter
 *
 * @author hmiao87@gmail.com (Haichao Miao)
 * @author florian@eckerstorfer.co (Florian Eckerstorfer)
 */
public class ArffIndexFileWriter
{
	PrintStream outputStream;

	public ArffIndexFileWriter(PrintStream outputStream)
	{
		this.outputStream = outputStream;
	}
	
	public void buildIndexFile(WeightedInvertedIndex index, boolean stemming, double lowerThreshold, double upperThreshold)
	{
		//header
		outputStream.println("% 1. Title: 20_newsgroups_subset Index");
		outputStream.println("% 2. Sources:");
		outputStream.println("% \t Creator: Haichao Miao & Florian Eckerstorfer");
		outputStream.println("% \t Date: " + new java.util.Date());
		outputStream.println("% stemming: " + (stemming ? "true" : "false"));
		outputStream.println("% lowerThreshold: " + lowerThreshold);
		outputStream.println("% upperThreshold: " + upperThreshold);
		
		FastVector atts = new FastVector();
		double[] vals;
		
		atts.addElement(new Attribute("className", (FastVector) null));
		atts.addElement(new Attribute("docID", (FastVector) null));
		atts.addElement(new Attribute("docLength", (FastVector) null));
		
		Instances data = new Instances("20_newsgroups_subset", atts, 0);
		
		for(String token : index.getTokens()) {
			if (index.hasPostingList(token)) {
				atts.addElement(new Attribute(token));
			}
		}
		
		WeightedPostingList postingList;
		WeightedPosting posting;
		
		int attId;
		
		// Iterate through all documents
		for (int documentId = 0; documentId < index.getDocumentCount(); documentId++) {
			vals = new double[data.numAttributes()];
			vals[0] = data.attribute(0).addStringValue(index.getClassName(documentId));
			vals[1] = data.attribute(1).addStringValue(index.getDocumentName(documentId));
			vals[2] = data.attribute(2).addStringValue(index.getDocLengths().get(documentId).toString()); 
			attId = 3;
			
			// Iterate through all tokens
			for(String token : index.getTokens()) {
				// Only add token to the ARFF file if it has a posting list
				if (index.hasPostingList(token)) {
					postingList = index.getPostingList(token);
					posting = postingList.getPosting(documentId);
				
					// We need to check if the posting exists, otherwise TF.IDF is 0
					if (null != posting) {
						vals[attId] = posting.getTfIdf();
					} else {
						vals[attId] = 0;
					}
				
					attId++;
				}
			}
			data.add(new Instance(1.0, vals));
			
			if ((documentId % 100) == 0) {
				System.out.println(documentId);
			}
		}
		
		outputStream.print(data);
		outputStream.close();
	}
}
