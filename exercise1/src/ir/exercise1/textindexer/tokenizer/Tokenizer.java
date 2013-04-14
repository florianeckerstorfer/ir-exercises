package ir.exercise1.textindexer.tokenizer;

import ir.exercise1.textindexer.collection.CollectionInterface;
import ir.exercise1.textindexer.document.ClassDocument;
import ir.exercise1.textindexer.model.InvertedIndex;
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
	private InvertedIndex index;

	/** Flag, if TRUE terms will be stemmed */
	private boolean stemming;

	/**
	 * Constructor.
	 * 
	 * @param stemming
	 */
	public Tokenizer(boolean stemming)
	{
		this.stemming = stemming;
	}
	
	/**
	 * Sets the inverted index.
	 * 
	 * @param index
	 */
	public void setInvertedIndex(InvertedIndex index)
	{
		this.index = index;
	}
	
	/**
	 * Returns the inverted index.
	 * 
	 * @return
	 */
	public InvertedIndex getInvertedIndex()
	{
		return index;
	}
	
	/**
	 * Tokenizes the given document.
	 * 
	 * @param document
	 * @param documentId
	 */
	public void tokenize(ClassDocument document, int documentId)
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
					index.addToken(token, documentId);
				}
			}
		}

		textScanner.close();
	}
}
