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
	/**
	 * Stores the names of the documents in the index.
	 */
	private List<String> documentNames = new ArrayList<String>();
	
	/**
	 * Stores the lengths of the documents in the index.
	 */
	private Map<Integer, Integer> documentLengths = new HashMap<Integer, Integer>();
	
	/**
	 * The index. A contains token/posting list pairs.
	 */
	private Map<String, PostingList> index = new HashMap<String, PostingList>();
	
	/**
	 * The list of all tokens in the index.
	 */
	private List<String> tokens = new ArrayList<String>();
	
	/**
	 * The length of all documents.
	 */
	private Integer totalDocumentLength = 0;
	
	/**
	 * Adds a document with the given name to the index and returns the document ID.
	 * 
	 * @param documentName The name of the document
	 * 
	 * @return The document ID
	 */
	public int addDocument(String documentName)
	{
		documentNames.add(documentName);
		
		return documentNames.size()-1;
	}
	
	/**
	 * Returns the name of the document with the given document ID.
	 * 
	 * @param index The document ID
	 * 
	 * @return The name of the document
	 */
	public String getDocumentName(int index)
	{
		return documentNames.get(index);
	}
	
	/**
	 * Returns the number of documents in the index.
	 * 
	 * @return The number of documents in the index
	 */
	public int getDocumentCount()
	{
		return documentNames.size();
	}

	/**
	 * Adds a token/document pair to the index.
	 * 
	 * If the token is already in the index it adds the document to the posting list,
	 * otherwise it creates a new posting list for this token.
	 * 
	 * @param token      The token
	 * @param documentId The document ID
	 */
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
	
	/**
	 * Returns the posting list for the given token
	 * 
	 * @param token The token
	 * 
	 * @return The posting list for the given token
	 */
	public PostingList getPostingList(String token)
	{
		return index.get(token);
	}
	
	/**
	 * Returns the token with the given index.
	 * 
	 * @param index The index of the token
	 * 
	 * @return The token
	 */
	public String getToken(int index)
	{
		return tokens.get(index);
	}
	
	/**
	 * Returns a list of all tokens.
	 * 
	 * @return The list of all tokens
	 */
	public List<String> getTokens()
	{
		return tokens;
	}
	
	/**
	 * Returns the number of tokens in the index.
	 * 
	 * @return The number of tokens in the index.
	 */
	public int getIndexSize()
	{
		return index.size();
	}

	/**
	 * Returns a hash map with document ID/document length pairs.
	 * 
	 * @return Hash map with document ID/document length pairs
	 */
	public Map<Integer, Integer> getDocumentLengths()
	{
		return documentLengths;
	}
	
	/**
	 * Returns the length of the given document.
	 * 
	 * @param documentId The ID of the document
	 * 
	 * @return The length of the document
	 */
	public Integer getDocumentLength(Integer documentId)
	{
		return documentLengths.get(documentId);
	}

	public void setDocumentLengths(Map<Integer, Integer> docLengths)
	{
		this.documentLengths = docLengths;
	}
	
	/**
	 * Adds the length of the given document to the index.
	 *
	 * @param documentId     The ID of the document
	 * @param documentLength The length of the document
	 */
	public void addDocumentLength(Integer documentId, Integer documentLength)
	{
		documentLengths.put(documentId, documentLength);
		totalDocumentLength += documentLength;
	}
}