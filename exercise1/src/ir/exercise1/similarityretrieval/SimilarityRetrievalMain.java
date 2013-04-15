package ir.exercise1.similarityretrieval;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import ir.exercise1.common.timer.Timer;
import ir.exercise1.similarityretrieval.reader.file.ArffIndexFileReader;
import ir.exercise1.textindexer.model.WeightedInvertedIndex;

public class SimilarityRetrievalMain
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
    	SimilarityRetrievalMain main = new SimilarityRetrievalMain(args);

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
        System.out.println("java -jar SimilarityRetrieval.jar path/to/index");
    }

    private Timer timer = new Timer();

    @Argument
    private List<String> arguments = new ArrayList<String>();

    @Option(name="-index", usage="The index in ARFF format")
    private String indexFile;

    public SimilarityRetrievalMain(String[] args)
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
     * Runs the similarity retrieval.
     *
     * @param collectionDirectory
     */
    public void run()
    {
        timer.reset().start();
        
        ArffIndexFileReader arffReader = new ArffIndexFileReader();
        WeightedInvertedIndex index = arffReader.readIndex(getReader(indexFile));
        
        System.out.println("Read index from " + indexFile + ".");
        System.out.println(index.getDocumentCount() + " documents");
        System.out.println(index.getIndexSize() + " tokens");
        
        timer.stop();

        System.out.println("Running time: " + timer.getTime() + "ms");
    }
    
    private BufferedReader getReader(String indexFile)
    {
        InputStream fileStream;
		try {
			fileStream = new FileInputStream(indexFile);
			InputStream gzipStream = new GZIPInputStream(fileStream);
			Reader decoder = new InputStreamReader(gzipStream, "UTF-8");
	        BufferedReader buffered = new BufferedReader(decoder);
	        
	        return buffered;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			
			return null;
		}
    }
}
