package ir.exercise1.textindexer;

import java.io.File;

import ir.exercise1.common.timer.Timer;

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
class TextIndexer
{
    /**
     * Main
     * @param  args
     * @return
     */
    public static void main(String[] args)
    {
        if (args.length < 4) {
            usage();
            System.exit(1);
        }

        String collectionDirectory = args[0];
        double lowerThreshold = Double.parseDouble(args[1]);
        double upperThreshold = Double.parseDouble(args[2]);
        boolean stemming = false;
        if (args[3].equals("true")) {
            stemming = true;
        }

        try {
            new TextIndexer().run(
                collectionDirectory,
                lowerThreshold,
                upperThreshold,
                stemming
            );
        } catch (RuntimeException e) {
            System.out.println("Something bad has happened during runtime:");
            System.out.println(e.getMessage());
        }
    }

    /**
     * Displays usage instructions.
     *
     * @return
     */
    protected static void usage()
    {
        System.out.println("Usage:");
        System.out.println("java -jar TextIndexer.jar path/to/documents lowerThreshold upperThreshold stemming");
    }

    public void runTextIndexer()
    {
        // String resultFileName = "<postingListSize>_topic<XX>_groupC<YY>";

        // SearchEngine searchEngine = new SearchEngine(
        //     new FilesystemGzipReader(),
        //     new File("./arff/newgroup_index.arff.gz"),
        //     new File("./output/" + resultFileName + ".txt")
        // );
        // //searchEngine.search("test");
        // searchEngine.searchPrototype(
        //     "talk.politics.mideast/76261",
        //     textInput.getDictionaryPrototype(),
        //     textInput.getAllTermsPrototype(),
        //     textInput.getAllDocNamesPrototype()
        // );
        // //searchEngine.searchPrototype("word", textInput.getDictionaryPrototype(), textInput.getAllTermsPrototype(), textInput.getAllDocNamesPrototype());
    }

    private Timer timer = new Timer();

    /**
     * Runs the TextIndexer.
     *
     * @param collectionDirectory
     */
    public void run(String collectionDirectory, double lowerThreshold, double upperThreshold, boolean stemming)
    {
        timer.reset().start();

        // First we need to read the documents from the filesystem
        CollectionReaderInterface reader = new ClassCollectionReader(
            collectionDirectory,
            new TextDocumentReader(new ClassDocumentFactory(), new FilesystemReader())
        );
        CollectionInterface collection = reader.read();

        // Based on the read collection, we can now build the index
        TextInput textInput = new TextInput(collection);
        textInput.setLowerThreshold(lowerThreshold);
        textInput.setUpperThreshold(upperThreshold);
        textInput.setStemming(stemming);
        textInput.buildIndex();

        timer.stop();

        System.out.println("Running time: " + timer.getTime() + "ms");
    }
}
