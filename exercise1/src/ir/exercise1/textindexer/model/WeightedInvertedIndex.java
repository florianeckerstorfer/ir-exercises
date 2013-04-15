package ir.exercise1.textindexer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeightedInvertedIndex
{
	private List<String> documentNames = new ArrayList<String>();
	private Map<Integer, String> classNames = new HashMap<Integer, String>();
	
	private Map<String, WeightedPostingList> index = new HashMap<String, WeightedPostingList>();
	private List<String> tokens = new ArrayList<String>();
	
	public int addDocument(String documentName)
	{
		documentNames.add(documentName);
		
		return documentNames.size()-1;
	}
	
	public String getDocumentName(int index)
	{
		return documentNames.get(index);
	}
	
	public int getDocumentCount()
	{
		return documentNames.size();
	}
	
	public void addClassName(Integer documentId, String className)
	{
		classNames.put(documentId, className);
	}
	
	public String getClassName(Integer documentId)
	{
		return classNames.get(documentId);
	}
	
	public Map<Integer, String> getClassNames()
	{
		return classNames;
	}
	
	public void addToken(String token, int documentId, double tfIdf)
	{
		WeightedPostingList postingList;
		
		if (!index.containsKey(token)) {
			postingList = new WeightedPostingList();
			index.put(token, postingList);
			tokens.add(token);
		} else {
			postingList = index.get(token);
		}
		
		postingList.setPosting(documentId, new WeightedPosting(tfIdf));
	}
	
	public void setPostingList(String token, WeightedPostingList postingList)
	{
		tokens.add(token);
		index.put(token, postingList);
	}
	
	public boolean hasPostingList(String token)
	{
		if (index.containsKey(token) && index.get(token).getSize() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public WeightedPostingList getPostingList(String token)
	{
		return index.get(token);
	}
	
	public String getToken(int index)
	{
		return tokens.get(index);
	}
	
	public List<String> getTokens()
	{
		return tokens;
	}
	
	public int getIndexSize()
	{
		return index.size();
	}
}
