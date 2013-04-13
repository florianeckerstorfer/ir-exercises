package ir.exercise1.textindexer.indexer;

import ir.exercise1.textindexer.stemmer.StemmerInterface;
import ir.exercise1.textindexer.stemmer.PorterStemmer;
import ir.exercise1.textindexer.tokenizer.Tokenizer;
import ir.exercise1.textindexer.tools.TextTools;
import ir.exercise1.textindexer.collection.CollectionInterface;
import ir.exercise1.textindexer.document.ClassDocument;
import ir.exercise1.textindexer.model.Term;
import ir.exercise1.textindexer.writer.file.ArffIndexFileWriter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TextIndexer implements IndexerInterface
{
	/**
	 * The lower bound of the frequency threshold.
	 */
	private double lowerThreshold = 0;

	/**
	 * The upper bound of the frequency threshold.
	 */
	private double upperThreshold = 1000;

	/**
	 * If TRUE stemming is used.
	 */
	private boolean stemming = true;

	// TODO add document class name to arff file
	// TODO change to more efficient data structures

	// TODO to save the docNames and the terms in separate lists is clumsy...
	Double[][] dictionary; // TODO -> sparse Matrix

	private CollectionInterface collection;
	private Tokenizer tokenizer;

	int docsCount = 0;
	long avgTokensPerDoc = 0;
	int termsCount = 0;
	long tokensCount = 0;

	/**
	 * Constructor.
	 *
	 * @param collection
	 *
	 * @return
	 */
	public TextIndexer(CollectionInterface collection, Tokenizer tokenizer)
	{
		this.collection = collection;
		this.tokenizer  = tokenizer;

		termsCount = 0; //  TODO calculate when terms list is finished
	}

	/**
	 * Sets the lower bound for frequency thresholding.
	 *
	 * @param lowerThreshold The lower threshold
	 */
	public void setLowerThreshold(double lowerThreshold)
	{
		this.lowerThreshold = lowerThreshold;
	}

	/**
	 * Returns the lower bound for frequency thresholding.
	 *
	 * @return The lower threshold
	 */
	public double getLowerThreshold()
	{
		return lowerThreshold;
	}

	/**
	 * Sets the upper bound for frequency thresholding.
	 *
	 * @param upperThreshold The upper threshold
	 */
	public void setUpperThreshold(double upperThreshold)
	{
		this.upperThreshold = upperThreshold;
	}

	/**
	 * Returns the upper bound for frequency thresholding.
	 *
	 * @return The upper threshold
	 */
	public double getUpperThreshold()
	{
		return upperThreshold;
	}

	/**
	 * Sets if stemming should be used.
	 *
	 * @param stemming TRUE if stemming should be used, FALSE otherwise
	 */
	public void setStemming(boolean stemming)
	{
		this.stemming = stemming;
	}

	/**
	 * Returns if stemming should be used.
	 *
	 * @return TRUE if stemming should be used, FALSE otherwise
	 */
	public boolean getStemming()
	{
		return stemming;
	}

	public void buildIndex(ArffIndexFileWriter arffWriter)
	{
		tokenizer.tokenize(collection);

		termsCount = tokenizer.getTermsList().size();
		docsCount = tokenizer.getDocNamesList().size();

		dictionary = new Double[docsCount][termsCount];

		System.out.println("# of terms: " + termsCount);

		System.out.println("computation of tf-idf weight started");
		computeWeights();

		System.out.println("arff index writing started");
		writeIndexToArff(arffWriter);


		avgTokensPerDoc = tokensCount / docsCount;

		System.out.println(docsCount + " documents");
		System.out.println(tokensCount + " tokens");
		System.out.println(termsCount + " terms");
		System.out.println(avgTokensPerDoc + " avg. # tokens per document");
	}

	private void writeIndexToArff(ArffIndexFileWriter arffWriter)
	{
		arffWriter.createIndexFile(tokenizer.getDocNamesList(), tokenizer.getTermsList(), dictionary);
	}

	private void computeWeights()
	{
		//df = # of docs in the collection that contains the term
		//idf = log(collectionSize / df)
		//tf = # of occurance of the term in document
		//tf-idf = tf x idf

		for(int row = 0; row < tokenizer.getTokens().size(); row++) {
			Term curTerm = tokenizer.getTokens().get(row);

			int df = curTerm.getDocFreq().size();

			//System.out.println("df of " + curTerm.getName() + " is " + df);
			double idf = Math.log(collection.getDocumentCount()/df);

			Iterator<Map.Entry<String, Integer>> iterator = curTerm.getDocFreq().entrySet().iterator();

			while(iterator.hasNext()) {
				Map.Entry<String, Integer> curDocs = iterator.next();
				double tf = 1+Math.log(curDocs.getValue());

				int column = tokenizer.getDocNamesList().indexOf(curDocs.getKey());

				if(tf >= lowerThreshold && tf <= upperThreshold) {
					addToDictionary(column, curTerm.getName(), tf*idf);
				}
			}
		}
	}

	private void addToDictionary(int column, String term, double weight)
	{
		int rowOfTerm = tokenizer.getTermsList().indexOf(term);
		dictionary[column][rowOfTerm] = weight;

	}

	public Double[][] getDictionaryPrototype()
	{
		return dictionary;
	}

	public List<String> getAllDocNamesPrototype()
	{
		return tokenizer.getDocNamesList();

	}

	public List<String> getAllTermsPrototype()
	{
		return tokenizer.getTermsList();
	}
}
