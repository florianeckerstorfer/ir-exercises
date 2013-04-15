package ir.exercise1.similarityretrieval.search;

import ir.exercise1.common.array.ArrayIndexComparator;
import ir.exercise1.textindexer.stemmer.PorterStemmer;
import ir.exercise1.textindexer.stemmer.StemmerInterface;
import ir.exercise1.textindexer.tokenizer.Tokenizer;
import ir.exercise1.textindexer.tools.TextTools;
import ir.exercise1.textindexer.document.ClassDocument;
import ir.exercise1.textindexer.document.DocumentInterface;
import ir.exercise1.textindexer.indexer.TextIndexer;
import ir.exercise1.textindexer.model.InvertedIndex;
import ir.exercise1.textindexer.model.WeightedInvertedIndex;
import ir.exercise1.textindexer.model.WeightedPosting;
import ir.exercise1.textindexer.model.WeightedPostingList;
import ir.exercise1.textindexer.reader.file.FileReaderInterface;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

/**
 * SearchEngine
 *
 * @author hmiao87@gmail.com (Haichao Miao)
 * @author florian@eckerstorfer.co (Florian Eckerstorfer)
 */
public class SearchEngine
{
	public enum Measure {
		OVERLAPSCORE,
		COSINE
	}

	private WeightedInvertedIndex index;
	private boolean stemming;

	private Double[][] dictionary;
	private ArrayList<String> allTerms;
	private ArrayList<String> allDocs;
	private Measure measure = Measure.OVERLAPSCORE;

	public SearchEngine(WeightedInvertedIndex index, boolean stemming)
	{
		this.index = index;
		this.stemming = stemming;
	}
	
	public String search(String query, int topicId, String groupName)
	{
		WeightedPostingList postingList;
		Map<Integer, WeightedPosting> posting;
		Iterator<Entry<Integer, WeightedPosting>> iterator;
		Entry<Integer, WeightedPosting> document;
		List<String> queryTokens = tokenizeQuery(query);
		
		Double[] scores = new Double[index.getDocumentCount()];
		
		for (int i = 0; i < scores.length; i++) {
			scores[i] = 0.0;
		}
		
		// TODO: Cosine scoring!!
		
		for (String queryToken : queryTokens) {
			postingList = index.getPostingList(queryToken);
			if (null != postingList) {
				posting = postingList.getDocuments();
				iterator = posting.entrySet().iterator();
				while (iterator.hasNext()) {
					document = iterator.next();
					scores[document.getKey()] += document.getValue().getTfIdf();
				}
			}
		}
		
//		Iterator<String> it = index.getClassNames().values().iterator();
//		while (it.hasNext()) {
//			System.out.println(it.next());
//		}
		
//		for (int i = 0; i < scores.length; i++) {
//			System.out.println(index.getClassName(i) + "/" + index.getDocumentName(i) + ": " + scores[i]);
//		}
		
		ArrayIndexComparator comparator = new ArrayIndexComparator(scores);
		Integer[] indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);
		
		String output = "";
		
		for (int rank = 1; rank <= 10; rank++) {
			int k = indexes.length - rank;
			output += "topic" + topicId + " Q0 " + index.getClassName(indexes[k]) + "/"; 
			output += index.getDocumentName(indexes[k]) + " ";
			output += rank + " " + (double)Math.round(scores[indexes[k]]*100)/100 + " " + groupName;
			output += "\n";
		}
		
		return output;
	}
	
	private List<String> tokenizeQuery(String query)
	{
		StemmerInterface porterStemmer = new PorterStemmer();
		List<String> queryTokens = new ArrayList<String>();
		
		Scanner textScanner = new Scanner(query.toLowerCase()).useDelimiter("[^a-z]");

		while (textScanner.hasNext()) {
			String token = textScanner.next().trim();
			if (token.length() > 0) {
				if (!TextTools.isStopWord(token)) {
					if (stemming) {
						token = TextTools.doStemming(token, porterStemmer);
					}
					queryTokens.add(token);
				}
			}
		}

		textScanner.close();
		
		return queryTokens;
	}
	
	private double calcWeighting(String token, int documentCount)
	{
		double tf = 0.0;
		double df = 0.0;
		return Math.log(1 + tf) * Math.log(documentCount/df);
	}

	public void searchPrototype(String query, Double[][] dictionary, ArrayList<String> termsList, ArrayList<String> docsList)
	{
		this.dictionary = dictionary;
		allTerms = termsList;
		allDocs = docsList;

		boolean allowStemming = true; // TODO cli parser

		ArrayList<String> queryTerms = new ArrayList<String>();

		StemmerInterface porterStemmer = new PorterStemmer();

		Scanner textScanner = new Scanner(query);

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

					queryTerms.add(token);
				}
			}
			compoundScanner.close();
		}

		textScanner.close();

		Double result[] = score(queryTerms);

		toPrintForm(1, result, "C", "small");

	}

	private Double[] score(ArrayList<String> queryTerms)
	{
		String result = "";

		Double[] scores = new Double[queryTerms.size()];

		for(int i = 0; i < queryTerms.size(); i++) {
			double score = 0.0;
			int indexOfQueryTerm = allTerms.indexOf(queryTerms.get(i));

			if (indexOfQueryTerm != -1) {
				for (int j = 0; j < allDocs.size(); j++) {
					if(dictionary[j][indexOfQueryTerm] != null) {
						//System.out.println("query term: " + queryTerms.get(i) + ", j: " + j + ", indexOfQueryTerm: " + indexOfQueryTerm);
						score += dictionary[j][indexOfQueryTerm];
						//System.out.println("score: " + score);
					}
				}
			}

			scores[i] = score;
		}

		Arrays.sort(scores);

		return scores;
	}

	private String toPrintForm(int topic, Double[] result, String group, String postingListSize)
	{
		String output = "";
		int inverse_i = result.length-1;

		for (int i = 0; i < result.length; i++) {

			output += "topic"+topic+ " Q0" + " " + "docID" + " " + (i+1) + " " + result[inverse_i] + " " + "group" +group + " " + postingListSize + "\n";
			inverse_i--;
		}
		System.out.println(output);
		return output;

	}
}
