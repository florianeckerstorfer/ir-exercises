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
	List<String> docNamesList = new ArrayList<String>();

	/**
	 * List of all terms in the collection
	 */
	List<String> termsList = new ArrayList<String>();

	/** Flag, if TRUE terms will be stemmed */
	private boolean stemming;

	public Tokenizer(boolean stemming)
	{
		this.stemming = stemming;
	}
	
	public List<Term> getTokens()
	{
		return tokens;
	}
	
	public List<String> getDocNamesList()
	{
		return docNamesList;
	}
	
	public List<String> getTermsList()
	{
		return termsList;
	}
	
	public void tokenize(CollectionInterface collection)
	{
		int loopBreaker = 0; // TODO
		while (collection.hasNext()) {
			loopBreaker++; // TODO

			ClassDocument doc = (ClassDocument) collection.next();
			
			System.out.println(doc.getClassName() + ": " + doc.getName());

			docNamesList.add(doc.getName());
			
			if (loopBreaker == 33) {
				System.out.println("ATTENTION! We stop indexing after 33 documents!");
				break; //  TODO
			}
		}
	}
	
	public void tokenizeDocument(ClassDocument document)
	{
		StemmerInterface porterStemmer = new PorterStemmer();

		Scanner textScanner = new Scanner(document.getContent());

		while (textScanner.hasNext()) {
			String compound = textScanner.next().toLowerCase();

			// replace everything that is not a letter with a white space
			// for the word scanner (since the standard delimiter uses
			// whitespace)
			// not very efficient to run through the string twice, but it
			// works
			compound = compound.replaceAll("[^a-z\\s]", " ");

			compound = compound.trim();

			Scanner compoundScanner = new Scanner(compound);

			while (compoundScanner.hasNext()) {
				String token = compoundScanner.next();

				if (!TextTools.isStopWord(token)) {
					if (stemming) {
						token = TextTools.doStemming(token, porterStemmer);
					}
					addToTokensList(document.getName(), token);
				}
			}
			compoundScanner.close();
		}

		textScanner.close();
	}
	
	/**
	 * @param docName
	 * @param token
	 */
	private void addToTokensList(String docName, String token)
	{
		boolean tokenExists = false;

		for(Term t : tokens) {
			if (t.getName().equals(token)) {
				t.addDoc(docName);
				tokenExists = true;
			}
		}

		if(tokenExists == false) {
			Term curTerm = new Term(token);
			curTerm.addDoc(docName);

			tokens.add(curTerm);
			termsList.add(token);
		}
	}
}
