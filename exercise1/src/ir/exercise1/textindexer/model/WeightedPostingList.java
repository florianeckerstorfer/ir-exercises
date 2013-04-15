package ir.exercise1.textindexer.model;

import java.util.HashMap;
import java.util.Map;

/**
 * PostingList
 * 
 * @author florian@eckerstorfer.co (Florian Eckerstorfer)
 */
public class WeightedPostingList
{
	/**
	 * List of documents.
	 * 
	 * key=DocumentId
	 * value=Posting
	 */
	private Map<Integer, WeightedPosting> documents = new HashMap<Integer, WeightedPosting>();
	
	public void setPosting(Integer documentId, WeightedPosting weightedPosting)
	{
		documents.put(documentId, weightedPosting);
	}
	
	public WeightedPosting getPosting(Integer documentId)
	{
		return documents.get(documentId);
	}
	
	public Map<Integer, WeightedPosting> getDocuments()
	{
		return documents;
	}
	
	public int getSize()
	{
		return documents.size();
	}
}
