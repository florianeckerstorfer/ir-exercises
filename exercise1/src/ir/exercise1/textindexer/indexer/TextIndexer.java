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
	private boolean allowStemming = true;
	private double lowerThreshold = 0;
	private double upperThreshold = 1000;

	// TODO add document class name to arff file
	// TODO change to more efficient data structures

	// TODO to save the docNames and the terms in separate lists is clumsy...
	Double[][] dictionary; // TODO -> sparse Matrix
	//list of all docName in the collection
	ArrayList<String> docNamesList = new ArrayList<String>();
	//list of all terms in the collection
	ArrayList<String> termsList = new ArrayList<String>();

	//contains a list of Term objects. Term objects themself contains a list of docNames, where they are contained
	ArrayList<Term> tokens = new ArrayList<Term>();

	private CollectionInterface collection;

	int docsCount;
	long avgTokensPerDoc;
	int termsCount;
	long tokensCount;

	public TextIndexer(CollectionInterface collection)
	{
		this.collection = collection;

		docsCount = 0;
		tokensCount = 0;
		termsCount = 0; //  TODO calculate when terms list is finished
		avgTokensPerDoc = 0;
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
		this.allowStemming = stemming;
	}

	/**
	 * Returns if stemming should be used.
	 *
	 * @return TRUE if stemming should be used, FALSE otherwise
	 */
	public boolean getStemming()
	{
		return allowStemming;
	}

	public void buildIndex()
	{
		documentTokenization();

		termsCount = termsList.size();
		docsCount = docNamesList.size();

		dictionary = new Double[docsCount][termsCount];

		System.out.println("# of terms: " + termsCount);

		System.out.println("computation of tf-idf weight started");
		computeWeights();

		/*
		for(int i = 0; i < dictionary.length; i++) {
			System.out.println();
			for(int j = 0; j < dictionary[i].length; j++) {
				System.out.print(dictionary[i][j] + " ");
			}
		}
		*/

		System.out.println("arff index writing started");
		writeIndexToArff();


		avgTokensPerDoc = tokensCount / docsCount;

		System.out.println(docsCount + " documents");
		System.out.println(tokensCount + " tokens");
		System.out.println(termsCount + " terms");
		System.out.println(avgTokensPerDoc + " avg. # tokens per document");
	}

	private void writeIndexToArff()
	{
		ArffIndexFileWriter arffWriter = new ArffIndexFileWriter();

		File file = new File("./arff/newgroup_index.arff.gz"); // TODO add .gz

		try {
			arffWriter.setOutputFile(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		arffWriter.createIndexFile(docNamesList, termsList, dictionary);
	}

	/**
	 * tokenization chop on every whitespace and non-alphanumeric character
	 * stopWords will be ingored
	 */
	private void documentTokenization()
	{
		int loopBreaker = 0; // TODO

		while (collection.hasNext()) {
			loopBreaker++; // TODO

			ClassDocument doc = (ClassDocument) collection.next();
			System.out.println(doc.getClassName() + ": " + doc.getName());

			docNamesList.add(doc.getName());

			StemmerInterface porterStemmer = new PorterStemmer();

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

						if (allowStemming) {
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
