package ir.exercise1.textindexer.search;

import ir.exercise1.textindexer.Tools.Stemmer;
import ir.exercise1.textindexer.Tools.TextTools;
import ir.exercise1.textindexer.reader.file.FilesystemGzipReader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * SearchEngine
 *
 * @author hmiao87@gmail.com (Haichao Miao)
 */
public class SearchEngine {
	public enum Measure {
		OVERLAPSCORE,
		COSINE
	}
	
	// TODO separate search logic from filesystem writer
	
	private String index;
	private FileOutputStream fos;
	private BufferedOutputStream bos;
	private PrintStream pos;
	
	private Double[][] dictionary;
	private ArrayList<String> allTerms;
	private ArrayList<String> allDocs;
	private Measure measure = Measure.OVERLAPSCORE;
	
	public SearchEngine(File indexFile, File resultFile) {
		
		FilesystemGzipReader fr = new FilesystemGzipReader();
		
		try {
			fos = new FileOutputStream(resultFile);
			bos = new BufferedOutputStream(fos);
			pos = new PrintStream(bos);
		} catch (FileNotFoundException e) {
			//   TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		index = fr.read(indexFile);
		
	}
	
	public void searchPrototype(String query, Double[][] dictionary, ArrayList<String> termsList, ArrayList<String> docsList) {
		this.dictionary = dictionary;
		allTerms = termsList;
		allDocs = docsList;
		
		boolean allowStemming = true; // TODO cli parser
		
		ArrayList<String> queryTerms = new ArrayList<String>();
		
		Stemmer porterStemmer = new Stemmer();
		
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
		
		pos.print(toPrintForm(1, result, "C", "small"));
		
		pos.close();
		
	}
	
	private Double[] score(ArrayList<String> queryTerms) {
		
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
	
	private String toPrintForm(int topic, Double[] result, String group, String postingListSize) {
		
		String output = "";
		for (int i = 0; i < result.length; i++) {
			output += "topic"+topic+ " Q0" + " " + "docID" + " " + (i+1) + " " + result[i%result.length] + " " + "\n";
		}
		System.out.println(output);
		return output;
		
	}
	
}
