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
	
	private boolean stemming = false;
	
	// args
	private double lowerThreshold;
	private double upperThreshold;
	
	private HashMap<String, Set<Integer>> invertedIndex;
	
	public TextInput() {
		invertedIndex = new HashMap<String, Set<Integer>>();
	}
	
	public void buildIndex() {
		
		CollectionReaderInterface reader = new ClassCollectionReader("./data/20_newsgroups_subset", new TextDocumentReader(new ClassDocumentFactory(), new FilesystemReader()));
        CollectionInterface collection = reader.read();
        
        while (collection.hasNext()) {
        	
        	ClassDocument doc = (ClassDocument)collection.next();
            System.out.println(doc.getClassName() + ": " + doc.getName());
            
            ArrayList<String> tokens = TextTools.tokenize(doc.getContent());
            
            for (String token : tokens) {
            	
            }
            
            break; //todo: delete it
        }
        
	}
	
	
}
