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
		
		String result = score(queryTerms);
		
		pos.print(result);
		
		pos.close();
		
	}
	
	private String score(ArrayList<String> queryTerms) {
		
		String result = "";
		
		for(String queryTerm : queryTerms) {
			int indexOfQueryTerm = allTerms.indexOf(queryTerm);
					
		}
		
		return result;
	}
	
}
