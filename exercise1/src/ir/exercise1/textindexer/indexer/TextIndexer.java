package ir.exercise1.textindexer.indexer;

import ir.exercise1.textindexer.stemmer.StemmerInterface;
import ir.exercise1.textindexer.stemmer.PorterStemmer;
import ir.exercise1.textindexer.Tools.TextTools;
import ir.exercise1.textindexer.collection.CollectionInterface;
import ir.exercise1.textindexer.document.ClassDocument;
import ir.exercise1.textindexer.model.Term;
import ir.exercise1.textindexer.writer.file.ArffIndexFileWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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

	/**
	 * List of all docName in the collection
	 */
	ArrayList<String> docNamesList = new ArrayList<String>();

	/**
	 * List of all terms in the collection
	 */
	ArrayList<String> termsList = new ArrayList<String>();

	//contains a list of Term objects. Term objects themself contains a list of docNames, where they are contained
	ArrayList<Term> tokens = new ArrayList<Term>();

	private CollectionInterface collection;

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
	public TextIndexer(CollectionInterface collection)
	{
		this.collection = collection;

		termsCount = 0; //  TODO calculate when terms list is finished
	}

	/**
	 * Sets the lower bound for frequency tresholding.
	 *
	 * @param lowerThreshold The lower threshold
	 */
	public void setLowerThreshold(double lowerThreshold)
	{
		this.lowerThreshold = lowerThreshold;
	}

	/**
	 * Returns the lower bound for frequency tresholding.
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
	 * Sets if stemming should be usedf.
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
		documentTokenization();

		termsCount = termsList.size();
		docsCount = docNamesList.size();

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
		arffWriter.createIndexFile(docNamesList, termsList, dictionary);
	}

	/**
	 * tokenization chop on every whitespace and non-alphanumeric character
	 * stopWords will be ingored
	 */
	private void documentTokenization()
	{
		int loopBreaker = 0; // TODO
		StemmerInterface porterStemmer;

		while (collection.hasNext()) {
			loopBreaker++; // TODO

			ClassDocument doc = (ClassDocument) collection.next();
			System.out.println(doc.getClassName() + ": " + doc.getName());

			docNamesList.add(doc.getName());

			porterStemmer = new PorterStemmer();

			Scanner textScanner = new Scanner(doc.getContent());

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
						addToTokensList(doc.getName(), token);
					}
				}
				compoundScanner.close();
			}

			textScanner.close();

			if (loopBreaker == 33) break; //  TODO
		}

		System.out.println("***** let's take a break here, it's easier to test (don't forget to delete the break or set variable to 0) *****");

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

	private void computeWeights()
	{
		//df = # of docs in the collection that contains the term
		//idf = log(collectionSize / df)
		//tf = # of occurance of the term in document
		//tf-idf = tf x idf

		for(int row = 0; row < tokens.size(); row++) {
			Term curTerm = tokens.get(row);

			int df = curTerm.getDocFreq().size();

			//System.out.println("df of " + curTerm.getName() + " is " + df);
			double idf = Math.log(collection.getDocumentCount()/df);

			Iterator<Map.Entry<String, Integer>> iterator = curTerm.getDocFreq().entrySet().iterator();

			while(iterator.hasNext()) {
				Map.Entry<String, Integer> curDocs = iterator.next();
				double tf = 1+Math.log(curDocs.getValue());

				int column = docNamesList.indexOf(curDocs.getKey());

				if(tf >= lowerThreshold && tf <= upperThreshold) {
					addToDictionary(column, curTerm.getName(), tf*idf);
				}
			}
		}
	}

	private void addToDictionary(int column, String term, double weight)
	{

		int rowOfTerm = termsList.indexOf(term);

		dictionary[column][rowOfTerm] = weight;
		//System.out.println("column: " + column + ", " + "row: " + rowOfTerm + ", tf: " + weight);

	}

	public Double[][] getDictionaryPrototype()
	{
		return dictionary;
	}

	public ArrayList<String> getAllDocNamesPrototype()
	{
		return docNamesList;

	}

	public ArrayList<String> getAllTermsPrototype()
	{
		return termsList;
	}
}
