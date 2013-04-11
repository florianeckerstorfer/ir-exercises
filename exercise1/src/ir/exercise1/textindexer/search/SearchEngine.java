package ir.exercise1.textindexer.search;

import ir.exercise1.textindexer.stemmer.PorterStemmer;
import ir.exercise1.textindexer.stemmer.StemmerInterface;
import ir.exercise1.textindexer.Tools.TextTools;
import ir.exercise1.textindexer.reader.file.FileReaderInterface;

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
 * @author florian@eckerstorfer.co (Florian Eckerstorfer)
 */
public class SearchEngine
{
	public enum Measure {
		OVERLAPSCORE,
		COSINE
	}

	// TODO separate search logic from filesystem writer

	private String index;
	private PrintStream printStream;

	private Double[][] dictionary;
	private ArrayList<String> allTerms;
	private ArrayList<String> allDocs;
	private Measure measure = Measure.OVERLAPSCORE;

	public SearchEngine(FileReaderInterface reader, File indexFile, File resultFile)
	{
		try {
			printStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(resultFile)));
		} catch (FileNotFoundException e) {
			//   TODO Auto-generated catch block
			e.printStackTrace();
		}

		index = reader.read(indexFile);
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

		printStream.print(toPrintForm(1, result, "C", "small"));

		printStream.close();

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
