package ir.exercise1.textindexer;

import java.util.ArrayList;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import ir.exercise1.textindexer.reader.collection.CollectionReaderInterface;
import ir.exercise1.textindexer.reader.collection.ClassCollectionReader;
import ir.exercise1.textindexer.reader.document.TextDocumentReader;
import ir.exercise1.textindexer.reader.file.FilesystemReader;
import ir.exercise1.textindexer.document.DocumentInterface;
import ir.exercise1.textindexer.document.ClassDocument;
import ir.exercise1.textindexer.document.ClassDocumentFactory;
import ir.exercise1.textindexer.input.TextInput;
import ir.exercise1.textindexer.Tools.TextTools;
import ir.exercise1.textindexer.collection.CollectionInterface;

/**
 * Main
 *
 * @author Florian Eckerstorfer <florian@eckerstorfer.co>
 */
class Main
{
	
	
    public static void main(String[] args)
    {
    	
        System.out.println("Let's start by reading files from the file system.");

        TextInput textInput = new TextInput();
        textInput.buildIndex();
        
        
        
    }
}
