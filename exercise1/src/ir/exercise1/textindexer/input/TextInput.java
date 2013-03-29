package ir.exercise1.textindexer.input;

import ir.exercise1.textindexer.Tools.TextTools;
import ir.exercise1.textindexer.collection.CollectionInterface;
import ir.exercise1.textindexer.document.ClassDocument;
import ir.exercise1.textindexer.document.ClassDocumentFactory;
import ir.exercise1.textindexer.reader.collection.ClassCollectionReader;
import ir.exercise1.textindexer.reader.collection.CollectionReaderInterface;
import ir.exercise1.textindexer.reader.document.TextDocumentReader;
import ir.exercise1.textindexer.reader.file.FilesystemReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Scanner;

public class TextInput implements InputInterface {
	
	//need cli parser
	private boolean allowStemming = true;
	
	private double lowerThreshold;
	private double upperThreshold;
	
	private HashMap<String, Set<Integer>> invertedIndex;
	
	public TextInput() {
		invertedIndex = new HashMap<String, Set<Integer>>();
	}
	
	public void buildIndex(CollectionInterface collection) {
		
        while (collection.hasNext()) {
        	
        	ClassDocument doc = (ClassDocument)collection.next();
            System.out.println(doc.getClassName() + ": " + doc.getName());
            
            ArrayList<String> tokens = TextTools.tokenize(doc.getContent());
            
            if (allowStemming) {
            	tokens = TextTools.doStemming(tokens);
            }
            
            System.out.println("***** let's take a break here, it's easier to test *****");
            break; //todo: delete it
        }
	}
	
	
}
