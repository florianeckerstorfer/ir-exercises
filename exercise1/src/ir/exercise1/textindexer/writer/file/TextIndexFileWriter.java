package ir.exercise1.textindexer.writer.file;

import ir.exercise1.textindexer.model.WeightedInvertedIndex;
import ir.exercise1.textindexer.model.WeightedPosting;
import ir.exercise1.textindexer.model.WeightedPostingList;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;

/**
 * ArffFileWriter
 *
 * @author hmiao87@gmail.com (Haichao Miao)
 * @author florian@eckerstorfer.co (Florian Eckerstorfer)
 */
public class TextIndexFileWriter implements IndexFileWriterInterface
{
	PrintStream outputStream;

	public TextIndexFileWriter(PrintStream outputStream)
	{
		this.outputStream = outputStream;
	}
	
	public void buildIndexFile(WeightedInvertedIndex index, boolean stemming, double lowerThreshold, double upperThreshold)
	{
		WeightedPostingList postingList;
		
		// Store some main statistics that are required to build the index when reading this file later
		outputStream.println(">>>STATS<<<");
		outputStream.print(index.getDocumentCount());
		outputStream.print("\n");
		
		// Iterate through all documents to store some meta data
		outputStream.println(">>>METADATA<<<");
		for (int documentId = 0; documentId < index.getDocumentCount(); documentId++) {
			outputStream.println(
					documentId + ","+
					index.getClassName(documentId) + "," + 
					index.getDocumentName(documentId) + "," + 
					index.getDocumentLength(documentId));
		}
		
		// Iterate through all tokens to store the posting lists
		outputStream.println(">>>POSTINGLISTS<<<");
		for (int tokenId = 0; tokenId < index.getTokenCount(); tokenId++) {
			String token = index.getToken(tokenId);
			if (index.hasPostingList(token)) {
				outputStream.print(token);
				postingList = index.getPostingList(token);
				Iterator<Map.Entry<Integer, WeightedPosting>> iterator = postingList.getDocuments().entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<Integer, WeightedPosting> document = iterator.next();
					outputStream.print("," + document.getKey() + ":" + document.getValue().getTfIdf());
				}
				outputStream.print("\n");
			}
			
		}
		
		outputStream.close();
	}
}