package ir.exercise2.probmodel.search;

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
		COSINE,
		BM25
	}

	private WeightedInvertedIndex index;
	private boolean stemming;

	private Double[][] dictionary;
	private ArrayList<String> allTerms;
	private ArrayList<String> allDocs;
	public Measure measure = Measure.BM25;

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
		
		if (measure == Measure.OVERLAPSCORE) {
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
		}
		
		if (measure == Measure.BM25) {
			
			for (String queryToken : queryTokens) {
				postingList = index.getPostingList(queryToken);
				if (null != postingList) {
					posting = postingList.getDocuments();
					iterator = posting.entrySet().iterator();
					while (iterator.hasNext()) {
						document = iterator.next();
						scores[document.getKey()] += scoreBM25(document.getKey(), queryToken, document.getValue().getTfIdf());
					}
				}
			}
		}
		
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
	
	private double scoreBM25(int docId, String queryToken, double tfIdf) {
		
		double score = 0.0;
		
		/**
		 * parameter k1 calibrates document term frequency scaling. k1 = 0 -> corresponds to a binary model = no term frequency. 
		 * increasing k1 give more boost to rare words
		 */
		double k1 = 3.0; 
		
		/**
		 * parameter b calibrates the scaling by document length. takes values from 0 to 1. 
		 * 0 -> no length normalization
		 * 1 -> fully scalling the term weight by document length
		 */
		double b = 0.0;
		
		int N = index.getDocumentCount();
		int df = 1;
		
		if (index.getPostingList(queryToken) != null) {
			df = index.getPostingList(queryToken).getSize();
		}
		
		int tf = (int) Math.pow(Math.E, tfIdf / Math.log(N/df)) - 1;
		
		int docLength;
		
		if (index.getDocLengths().get(docId) != null) {
			docLength = index.getDocLengths().get(docId);
		} else {
			docLength = 1;
		}
		
		double avgLength = index.getTotalDocLength() / N;
		
		score = Math.log(N / df) * (k1+1) * tf / (k1*((1-b)+b*(docLength/avgLength))+tf);
		
		/*
		System.out.println("N: " + N);
		System.out.println("df: " + df);
		System.out.println("tf: " + tf);
		System.out.println("docLength: " + docLength);
		System.out.println("avgLength: " + avgLength);
		*/
		return score;
	}
	

	private List<String> tokenizeQuery(String query)
	{
		StemmerInterface porterStemmer = new PorterStemmer();
		List<String> queryTokens = new ArrayList<String>();
		
		String query2 = query.toLowerCase();
		Scanner textScanner = new Scanner(query2).useDelimiter("[^a-z]");

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
	
}