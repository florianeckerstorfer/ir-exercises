package ir.exercise1.textindexer.model;

import java.util.HashMap;
import java.util.Map;

/**
 * PostingList
 * 
 * @author florian@eckerstorfer.co (Florian Eckerstorfer)
 */
public class PostingList
{
	/**
	 * List of documents.
	 * 
	 * key=DocumentId
	 * value=Posting
	 */
	private Map<Integer, Posting> documents = new HashMap<Integer, Posting>();
	
	/**
	 * Adds a document to the postings list.
	 * 
	 * Increments the counter for the document if it is already in the list.
	 * 
	 * @param documentId
	 */
	public void addDocument(Integer documentId)
	{
		Posting posting;
		
		if (documents.containsKey(documentId)) {
			posting = documents.get(documentId);
			posting.incrementCount();
		} else {
			posting = new Posting(1);
		}
		
		documents.put(documentId, posting);
	}
	
	public Map<Integer, Posting> getDocuments()
	{
		return documents;
	}
	
	public Posting getPosting(Integer documentId)
	{
		return documents.get(documentId);
	}
	
	public int getDocumentFrequency()
	{
		return documents.size();
	}
	
	public class Posting
	{
		int count = 0;
		double tfIdf = 0.0;
		
		public Posting(int count)
		{
			this.count = count;
		}
		
		public void incrementCount()
		{
			count++;
		}
		
		public int getCount()
		{
			return count;
		}
		
		public void setTfIdf(Double tfIdf)
		{
			this.tfIdf = tfIdf;  
		}
		
		public double getTfIdf()
		{
			return tfIdf;
		}
	}
}
