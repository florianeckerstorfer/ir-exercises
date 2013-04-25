package ir.exercise1.textindexer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * InvertedIndex
 * 
 * @author florian@eckerstorfer (Florian Eckerstorfer)
 */
public class InvertedIndex
{
	private List<String> documentNames = new ArrayList<String>();
	
	private Map<Integer, Integer> docLengths = new HashMap<Integer, Integer>();
	
	private Map<String, PostingList> index = new HashMap<String, PostingList>();
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

	public void addToken(String token, int documentId)
	{
		PostingList postingList;
		
		if (!index.containsKey(token)) {
			postingList = new PostingList();
			index.put(token, postingList);
			tokens.add(token);
		} else {
			postingList = index.get(token);
		}
		
		postingList.addDocument(documentId);
	}
	
	public PostingList getPostingList(String token)
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

	public Map<Integer, Integer> getDocLengths() {
		return docLengths;
	}

	public void setDocLengths(Map<Integer, Integer> docLengths) {
		this.docLengths = docLengths;
	}
}