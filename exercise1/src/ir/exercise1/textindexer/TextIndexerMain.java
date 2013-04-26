package ir.exercise1.textindexer;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.zip.GZIPOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import ir.exercise1.common.timer.Timer;

import ir.exercise1.textindexer.reader.collection.CollectionReaderInterface;
import ir.exercise1.textindexer.reader.collection.ClassCollectionReader;
import ir.exercise1.textindexer.reader.document.TextDocumentReader;
import ir.exercise1.textindexer.reader.file.FilesystemReader;
import ir.exercise1.textindexer.tokenizer.Tokenizer;
import ir.exercise1.textindexer.writer.file.ArffIndexFileWriter;
import ir.exercise1.textindexer.writer.file.IndexFileWriterInterface;
import ir.exercise1.textindexer.writer.file.TextIndexFileWriter;
import ir.exercise1.textindexer.document.ClassDocumentFactory;
import ir.exercise1.textindexer.indexer.TextIndexer;
import ir.exercise1.textindexer.collection.CollectionInterface;

/**
 * Main
 *
 * @author Florian Eckerstorfer <florian@eckerstorfer.co>
 */
public class TextIndexerMain
{
    /**
     * Main
     *
     * @param args
     *
     * @return
     */
    public static void main(String[] args)
    {
        TextIndexerMain main = new TextIndexerMain(args);

        try {
            main.run();
        } catch (RuntimeException e) {
            System.out.println("Something bad has happened during runtime:");
            e.printStackTrace();

            return;
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

    private Timer timer = new Timer();

    @Argument
    private List<String> arguments = new ArrayList<String>();

    @Option(name="-directory", usage="Directory of the collection to index")
    private String collectionDirectory;

    @Option(name="-output", usage="File to write the GZ compressed index to")
    private String outputFile;

    @Option(name="-lower-threshold", usage="Lower bound for frequency thresholding")
    private double lowerThreshold;

    @Option(name="-upper-threshold", usage="Upper bound for frequency thresholding")
    private double upperThreshold;

    @Option(name="-stemming", usage="Flag if stemming should be used")
    private boolean stemming;
    
    @Option(name="-format", usage="Index format, either arff or text")
    private String indexFormat;
    
    @Option(name="-gzip", usage="Enable GZIP compression for index")
    private boolean enableGzip;

    public TextIndexerMain(String[] args)
    {
        CmdLineParser parser = new CmdLineParser(this);

        try {
            parser.parseArgument(args);
        } catch(CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java -jar myprogram.jar [options...] arguments...");
            parser.printUsage(System.err);

            System.exit(1);
        }
    }

    /**
     * Runs the TextIndexer.
     *
     * @param collectionDirectory
     */
    public void run()
    {
        timer.reset().start();
        
        System.out.println("Collection directory: " + collectionDirectory);
        System.out.println("Index file: " + outputFile);
        System.out.println("Index format: " + indexFormat);
        System.out.print("Enable GZIP? ");
        if (enableGzip) System.out.println("yes");
        else System.out.println("no");
        System.out.println("Lower threshold: " + lowerThreshold);
        System.out.println("Upper threshold: " + upperThreshold);
        System.out.print("Stemming? ");
        if (stemming) System.out.println("yes");
        else System.out.println("no");

        // First we need to read the documents from the filesystem
        CollectionReaderInterface reader = new ClassCollectionReader(
            collectionDirectory,
            new TextDocumentReader(new ClassDocumentFactory(), new FilesystemReader())
        );
        CollectionInterface collection = reader.read();
        
        Tokenizer tokenizer = new Tokenizer(stemming);

        TextIndexer indexer = new TextIndexer(collection, tokenizer);
        indexer.setLowerThreshold(lowerThreshold);
        indexer.setUpperThreshold(upperThreshold);
        indexer.setStemming(stemming);

        indexer.buildIndex(createIndexFileWriter(outputFile));

        timer.stop();

        System.out.println("Running time: " + timer.getTime() + "ms");
    }

    private IndexFileWriterInterface createIndexFileWriter(String outputFile)
    {
        try {
        	String filename = outputFile;
        	if ("arff".equals(indexFormat)) {
            	filename += ".arff";
            } else if ("text".equals(indexFormat)) {
            	filename += ".txt"; 
            } else {
            	System.out.println("Invalid index format " + indexFormat + ".");
                System.exit(1);
            }
        	
        	if (enableGzip) {
        		filename += ".gz";
        	}
        	
        	OutputStream outputStream = new FileOutputStream(new File(filename));
        	PrintStream printStream;
        	if (enableGzip) {
        		printStream = new PrintStream(
    				new BufferedOutputStream(new GZIPOutputStream(outputStream))
        		);
        	} else {
        		printStream = new PrintStream(new BufferedOutputStream(outputStream));
        	}
            
            if ("arff".equals(indexFormat)) {
            	return new ArffIndexFileWriter(printStream);
            } else {
            	return new TextIndexFileWriter(printStream);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Could not find file " + outputFile + ".");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("IO Error: " + e.getMessage());
            System.exit(1);
        }

        return null;
    }
}
