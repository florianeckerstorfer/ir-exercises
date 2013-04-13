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

import weka.core.Attribute;

/**
 * ArffFileWriter
 *
 * @author hmiao87@gmail.com (Haichao Miao)
 */
public class ArffIndexFileWriter
{
	PrintStream outputStream;

	public ArffIndexFileWriter(PrintStream outputStream)
	{
		this.outputStream = outputStream;
	}

	public void createIndexFile(ArrayList<String> docNames, ArrayList<String> terms, Double[][] dictionary)
	{
		//header
		outputStream.println("% 1. Title: 20_newsgroups_subset Index");
		outputStream.println("% 2. Sources:");
		outputStream.println("% \t Creator: Haichao Miao & Florian Eckerstorfer");
		outputStream.println("% \t Date: " + new java.util.Date());
		outputStream.println("% allow_stemming: "); // TODO
		outputStream.println("% upper_bound: "); // TODO
		outputStream.println("% lower_bound: "); // TODO
		outputStream.print("@RELATION ");
		outputStream.println("20_newsgroups_subset"); // TODO
		outputStream.println();
		outputStream.println("@ATTRIBUTE className STRING");
		outputStream.println("@ATTRIBUTE docID STRING");

		for(String term : terms) {
			outputStream.print("@ATTRIBUTE ");
			outputStream.print(term);
			outputStream.println(" NUMERIC");
		}

		outputStream.println("@DATA");

		for(int i = 0; i < dictionary.length; i++) {
			outputStream.print("classname, "); // TODO
			outputStream.print(docNames.get(i) + ", ");
			for(int j = 0; j < dictionary[i].length; j++) {
				if(dictionary[i][j] != null) {
					outputStream.print(dictionary[i][j]);
				} else {
					outputStream.print(0);
				}

				outputStream.print(", ");
			}
			outputStream.println();
		}

		outputStream.close();

	}





}
