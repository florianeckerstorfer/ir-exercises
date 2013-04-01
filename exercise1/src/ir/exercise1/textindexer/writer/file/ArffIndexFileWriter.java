package ir.exercise1.textindexer.writer.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
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
	
	// TODO seperate arff logic from filesystem writer
	String path;
	FileOutputStream fos;
	GZIPOutputStream gzipos;
	BufferedOutputStream bos;
	PrintStream ps;
	
	public void setOutputFile(File file) throws IOException {
		fos = new FileOutputStream(file);
		gzipos = new GZIPOutputStream(fos);
		bos = new BufferedOutputStream(gzipos); // TODO change argument to gzipos
		ps = new PrintStream(bos);
	}
	
	public void createIndexFile(Hashtable<String, Hashtable<String, Double>> weightedIndex) {
		
		//header
		ps.println("% 1. Title: 20_newsgroups_subset Index");
		ps.println("% 2. Sources:");
		ps.println("% \t Creator: Haichao Miao & Florian Eckerstorfer");
		ps.println("% \t Date: " + new java.util.Date());
		ps.println("% allow_stemming: "); //todo
		ps.println("% upper_bound: "); //todo
		ps.println("% lower_bound: "); //todo
		ps.print("@RELATION ");
		ps.println("20_newsgroups_subset"); //todo
		ps.println();
		ps.println("@ATTRIBUTE class STRING");
		ps.println("@ATTRIBUTE docID STRING");
		ps.println("@ATTRIBUTE term STRING");
		ps.println("@ATTRIBUTE tf-idf-weight NUMERIC");
		
		ps.println("@DATA");
		
		Iterator<Map.Entry<String, Hashtable<String, Double>>> iterator = weightedIndex.entrySet().iterator();
		
		while(iterator.hasNext()) {
			Map.Entry<String, Hashtable<String, Double>> terms = iterator.next();
			String curTerm = terms.getKey();
			
			Iterator<Map.Entry<String, Double>> iterator2 = terms.getValue().entrySet().iterator();

			while(iterator2.hasNext()) {
				Map.Entry<String, Double> docs = iterator2.next();
				String curDoc = docs.getKey();
				double curWeight = docs.getValue();
				
				// TODO add document class
				ps.println("classname" + ", " + curDoc + ", " +curTerm + ", " + curWeight);
			}
		}
		
		ps.close();
	}
	
	
	
	
	
}
