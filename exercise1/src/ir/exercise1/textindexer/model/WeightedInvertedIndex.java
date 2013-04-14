package ir.exercise1.textindexer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeightedInvertedIndex
{
	private List<String> documentNames = new ArrayList<String>();
	
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
