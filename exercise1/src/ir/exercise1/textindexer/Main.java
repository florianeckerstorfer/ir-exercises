package ir.exercise1.textindexer;

import java.io.File;

import ir.exercise1.textindexer.reader.file.FilesystemGzipReader;
import ir.exercise1.textindexer.reader.collection.CollectionReaderInterface;
import ir.exercise1.textindexer.reader.collection.ClassCollectionReader;
import ir.exercise1.textindexer.reader.document.TextDocumentReader;
import ir.exercise1.textindexer.reader.file.FilesystemReader;
import ir.exercise1.textindexer.search.SearchEngine;
import ir.exercise1.textindexer.document.ClassDocumentFactory;
import ir.exercise1.textindexer.input.TextInput;
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
    	//to calculate the running time for performance reasons
    	long startTime = System.currentTimeMillis();

    	runTextIndexer();

    	long endTime = System.currentTimeMillis();

    	long totalTime = endTime - startTime;

    	System.out.println("Running time: " + totalTime + "ms");
    	System.out.println("Available processors (cores): " +
    	        Runtime.getRuntime().availableProcessors());
    	System.out.println("Total memory (bytes): " +
    	        Runtime.getRuntime().totalMemory()); //memory used by the jvm. not quite what i was looking for

    	System.out.println();
    }

    public static void runTextIndexer() {
    	CollectionReaderInterface reader = new ClassCollectionReader(
            "./data/20_newsgroups_subset",
            new TextDocumentReader(new ClassDocumentFactory(), new FilesystemReader())
        );
    	//CollectionReaderInterface reader = new ClassCollectionReader("./test/data/testdata", new TextDocumentReader(new ClassDocumentFactory(), new FilesystemReader()));

    	CollectionInterface collection = reader.read();

        System.out.println("Let's start by reading files from the file system.");

        TextInput textInput = new TextInput(collection);
        textInput.buildIndex();

        String resultFileName = "<postingListSize>_topic<XX>_groupC<YY>";

        SearchEngine searchEngine = new SearchEngine(
            new FilesystemGzipReader(),
            new File("./arff/newgroup_index.arff.gz"),
            new File("./output/" + resultFileName + ".txt")
        );
        //searchEngine.search("test");
        searchEngine.searchPrototype(
            "talk.politics.mideast/76261",
            textInput.getDictionaryPrototype(),
            textInput.getAllTermsPrototype(),
            textInput.getAllDocNamesPrototype()
        );
        //searchEngine.searchPrototype("word", textInput.getDictionaryPrototype(), textInput.getAllTermsPrototype(), textInput.getAllDocNamesPrototype());
    }
}
