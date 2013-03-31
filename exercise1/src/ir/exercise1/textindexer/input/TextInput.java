package ir.exercise1.textindexer.input;

import ir.exercise1.textindexer.Tools.Stemmer;
import ir.exercise1.textindexer.Tools.TextTools;
import ir.exercise1.textindexer.collection.CollectionInterface;
import ir.exercise1.textindexer.document.ClassDocument;
import ir.exercise1.textindexer.writer.ArffIndexFileWriter;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class TextInput implements InputInterface {

	// todo: cli parser
	private boolean allowStemming;

	private double lowerThreshold;
	private double upperThreshold;

	//todo: maybe build the weightedIndex without intermediate steps?
	
	//terms -> docs -> frequency (zero values won't be safed)
	Hashtable<String, Hashtable<String, Integer>> termFrequencyList;
	//terms -> docs -> weight
	Hashtable<String, Hashtable<String, Double>> weightedIndex;
	
	private CollectionInterface collection;

	long docsCount;
	long avgTokensPerDoc;
	long termsCount;
	long tokensCount;

	public TextInput(CollectionInterface collection) {

		this.collection = collection;
		termFrequencyList = new Hashtable<String, Hashtable<String, Integer>>();
		weightedIndex = new Hashtable<String, Hashtable<String, Double>>();
		
		allowStemming = true; // needs to be parsed from cli

		docsCount = collection.getDocumentCount();
		tokensCount = 0;
		termsCount = 0; // todo: calculate when terms list is finished
		avgTokensPerDoc = 0;
	}

	public void buildIndex() {
		
		System.out.println("tokenization started");
		documentTokenization();
		
		System.out.println("computation of tf-idf weight started");
		computeWeights();
		
		System.out.println("arff index writing started");
		writeIndexToArff();
		
		tokensCount = termFrequencyList.size();
		
		avgTokensPerDoc = tokensCount / docsCount;

		System.out.println(docsCount + " documents");
		System.out.println(tokensCount + " tokens");
		System.out.println(termsCount + " terms");
		System.out.println(avgTokensPerDoc + " avg. # tokens per document");
	}
	
	private void writeIndexToArff() {
		
		ArffIndexFileWriter arffWriter = new ArffIndexFileWriter();
		
		File file = new File("./arff/newgroup_index.arff.gz"); //todo: add .gz
		
		try {
			arffWriter.setOutputFile(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		arffWriter.createIndexFile(weightedIndex);
	}
	
	/**
	 * tokenization chop on every whitespace and non-alphanumeric character
	 * stopWords will be ingored
	 */
	private void documentTokenization() {
		
		int loopBreaker = 0; //todo: 
		
		while (collection.hasNext()) {
			loopBreaker++; //todo: 
			
			ClassDocument doc = (ClassDocument) collection.next();
			System.out.println(doc.getClassName() + ": " + doc.getName());

			
			Stemmer porterStemmer = new Stemmer();

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
						
						addToTermFrequencyList(token, doc.getName());
					}
				}
				compoundScanner.close();
			}

			textScanner.close();
			
			if (loopBreaker == 333) break; // todo: 
		}
		
		
		System.out.println("***** let's take a break here, it's easier to test (don't forget to delete the break or set variable to 0) *****");

	}
	
	private void computeWeights() {
		//df = # of docs in the collection that contains the term
		//idf = log(collectionSize / df)
		//tf = # of occurance of the term in document
		//tf-idf = tf x idf
		
		
		Iterator<Map.Entry<String, Hashtable<String, Integer>>> iterator = termFrequencyList.entrySet().iterator();
		
		while(iterator.hasNext()) {
			Map.Entry<String, Hashtable<String, Integer>> terms = iterator.next();
			String curTerm = terms.getKey();
			
			int df = terms.getValue().size();

			//System.out.println(curTerm + ": df=" + df);
			
			double idf = Math.log(collection.getDocumentCount()/df);
			
			Iterator<Map.Entry<String, Integer>> iterator2 = terms.getValue().entrySet().iterator();

			while(iterator2.hasNext()) {
				Map.Entry<String, Integer> docs = iterator2.next();
				String curDoc = docs.getKey();
				
				int tf = docs.getValue();
				
				addToWeightedIndex(curTerm, curDoc, tf*idf);
			}
		}
		
		
	}
	
	private void addToWeightedIndex(String term, String docName, double weight) {
		
		if(!weightedIndex.containsKey(term)) {
			Hashtable<String, Double> newDocWeightedIndex = new Hashtable<String, Double>();
			newDocWeightedIndex.put(docName, weight);
			
			weightedIndex.put(term, newDocWeightedIndex);
			
		} else {
			Hashtable<String, Double> currentDocWeightedIndex = weightedIndex.get(term);
			
			if(!currentDocWeightedIndex.containsKey(docName)) {
				currentDocWeightedIndex.put(docName, weight);
			}
		}
	}
	
	private void addToTermFrequencyList(String token, String docName) {
		
		if(!termFrequencyList.containsKey(token)) {
			
			Hashtable<String, Integer> newTermDocFrequencyList = new Hashtable<String, Integer>();
			newTermDocFrequencyList.put(docName, 1);
			
			termFrequencyList.put(token, newTermDocFrequencyList);
			
		} else {
			
			Hashtable<String, Integer> currentTermDocFrequencyList = termFrequencyList.get(token);
			
			if(!currentTermDocFrequencyList.containsKey(docName)) {
				currentTermDocFrequencyList.put(docName, 1);
			} else {
				int currentTermFrequency = currentTermDocFrequencyList.get(docName);
				currentTermDocFrequencyList.put(docName, currentTermFrequency + 1);
			}
		}
		
	}

}
