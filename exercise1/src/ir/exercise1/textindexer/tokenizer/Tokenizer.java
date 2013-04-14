package ir.exercise1.textindexer.tokenizer;

import ir.exercise1.textindexer.collection.CollectionInterface;
import ir.exercise1.textindexer.document.ClassDocument;
import ir.exercise1.textindexer.model.Term;
import ir.exercise1.textindexer.stemmer.PorterStemmer;
import ir.exercise1.textindexer.stemmer.StemmerInterface;
import ir.exercise1.textindexer.tools.TextTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Tokenizer.
 * 
 * @author florian@eckerstorfer.co (Florian Eckerstorfer)
 */
public class Tokenizer
{
	/**
	 * Contains a list of Term objects.
	 * 
	 * Term objects itself contains a list of docNames, where they are contained
	 */
	List<Term> tokens = new ArrayList<Term>();
	
	/**
	 * List of all docName in the collection
	 */
	List<String> documentNames = new ArrayList<String>();

	/**
	 * List of all terms in the collection
	 */
	List<String> terms = new ArrayList<String>();

	/** Flag, if TRUE terms will be stemmed */
	private boolean stemming;

	public Tokenizer(boolean stemming)
	{
		this.stemming = stemming;
	}
	
	/**
	 * Returns the tokens.
	 * 
	 * @return
	 */
	public List<Term> getTokens()
	{
		return tokens;
	}
	
	/**
	 * Returns the document names.
	 * 
	 * @return
	 */
	public List<String> getDocumentNames()
	{
		return documentNames;
	}
	
	/**
	 * Returns the terms.
	 * 
	 * @return
	 */
	public List<String> getTerms()
	{
		return terms;
	}
	
	/**
	 * Tokenizes the given collection.
	 * 
	 * @param collection
	 */
	public void tokenize(CollectionInterface collection)
	{
		int loopBreaker = 0; // TODO
		while (collection.hasNext()) {
			loopBreaker++; // TODO

			ClassDocument document = (ClassDocument) collection.next();
			
			System.out.println(document.getClassName() + ": " + document.getName());

			documentNames.add(document.getName());
			
			tokenizeDocument(document);
			
			if (loopBreaker == 33) {
				System.out.println("ATTENTION! We stop indexing after 33 documents!");
				break; //  TODO
			}
		}
	}
	
	/**
	 * Tokenizes the given document.
	 * 
	 * @param document
	 */
	public void tokenizeDocument(ClassDocument document)
	{
		StemmerInterface porterStemmer = new PorterStemmer();

		Scanner textScanner = new Scanner(document.getContent().toLowerCase()).useDelimiter("[^a-z]");

		while (textScanner.hasNext()) {
			String token = textScanner.next().trim();
			if (token.length() > 0) {
				if (!TextTools.isStopWord(token)) {
					if (stemming) {
						token = TextTools.doStemming(token, porterStemmer);
					}
					addToTokensList(document.getName(), token);
				}
			}
		}

		textScanner.close();
	}
	
	/**
	 * @param documentName
	 * @param token
	 */
	private void addToTokensList(String documentName, String token)
	{
		boolean tokenExists = false;

		for(Term t : tokens) {
			if (t.getName().equals(token)) {
				t.addDoc(documentName);
				tokenExists = true;
			}
		}

		if(tokenExists == false) {
			Term curTerm = new Term(token);
			curTerm.addDoc(documentName);

			tokens.add(curTerm);
			terms.add(token);
		}
	}
}
