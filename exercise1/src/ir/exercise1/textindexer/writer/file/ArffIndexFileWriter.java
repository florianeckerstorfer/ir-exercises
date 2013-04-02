package ir.exercise1.textindexer.writer.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * ArffFileWriter
 *
 * @author hmiao87@gmail.com (Haichao Miao)
 */
public class ArffIndexFileWriter {
	
	//  TODO  seperate arff logic from filesystem writer
	String path;
	FileOutputStream fos;
	GZIPOutputStream gzipos;
	BufferedOutputStream bos;
	PrintStream ps;
	
	public void setOutputFile(File file) throws IOException {
		fos = new FileOutputStream(file);
		gzipos = new GZIPOutputStream(fos);
		bos = new BufferedOutputStream(gzipos); //  TODO  change argument to gzipos
		ps = new PrintStream(bos);
	}
	
	public void createIndexFile(ArrayList<String> docNames, ArrayList<String> terms, Double[][] dictionary) {
		
		//header
		ps.println("% 1. Title: 20_newsgroups_subset Index");
		ps.println("% 2. Sources:");
		ps.println("% \t Creator: Haichao Miao & Florian Eckerstorfer");
		ps.println("% \t Date: " + new java.util.Date());
		ps.println("% allow_stemming: "); // TODO 
		ps.println("% upper_bound: "); // TODO 
		ps.println("% lower_bound: "); // TODO 
		ps.print("@RELATION ");
		ps.println("20_newsgroups_subset"); // TODO 
		ps.println();
		ps.println("@ATTRIBUTE className STRING");
		ps.println("@ATTRIBUTE docID STRING");
		
		for(String term : terms) {
			ps.print("@ATTRIBUTE ");
			ps.print(term);
			ps.println(" NUMERIC");
		}
		
		
		ps.println("@DATA");
		
		for(int i = 0; i < dictionary.length; i++) {
			ps.print("classname, "); // TODO
			ps.print(docNames.get(i) + ", ");
			for(int j = 0; j < dictionary[i].length; j++) {
				if(dictionary[i][j] != null) {
					ps.print(dictionary[i][j]);
				} else {
					ps.print(0);
				}
				
				ps.print(", ");
			}
			ps.println();
		}
		
		/*
		Iterator<Map.Entry<String, Hashtable<String, Double>>> iterator = weightedIndex.entrySet().iterator();
		
		while(iterator.hasNext()) {
			Map.Entry<String, Hashtable<String, Double>> docs = iterator.next();
			String curDoc = docs.getKey();
			
			Iterator<Map.Entry<String, Double>> iterator2 = docs.getValue().entrySet().iterator();

			while(iterator2.hasNext()) {
				Map.Entry<String, Double> terms = iterator2.next();
				String curTerm = terms.getKey();
				double curWeight = terms.getValue();
				
				//  TODO  add document class
				ps.println("classname" + ", " + curDoc + ", " + curTerm + ", " + curWeight);
			}
		}
		*/
		
		ps.close();
	}
	
	
	
	
	
}
