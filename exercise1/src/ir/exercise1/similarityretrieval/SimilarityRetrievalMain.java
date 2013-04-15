package ir.exercise1.similarityretrieval;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import ir.exercise1.common.timer.Timer;
import ir.exercise1.similarityretrieval.reader.file.ArffIndexFileReader;
import ir.exercise1.similarityretrieval.search.SearchEngine;
import ir.exercise1.textindexer.document.ClassDocumentFactory;
import ir.exercise1.textindexer.document.DocumentInterface;
import ir.exercise1.textindexer.model.WeightedInvertedIndex;
import ir.exercise1.textindexer.reader.document.DocumentReaderInterface;
import ir.exercise1.textindexer.reader.document.TextDocumentReader;
import ir.exercise1.textindexer.reader.file.FilesystemReader;
import ir.exercise1.textindexer.tokenizer.Tokenizer;

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
    
    @Option(name="-stemming", usage="If the index has been cretaed with stemming enabled, queries must also be stemmed")
    private boolean stemming;
    
    @Option(name="-thresholdSize", usage="Used for the output")
    private String thresholdSize;

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
        arffReader = null;
        
        System.out.println("Read index from " + indexFile + ".");
        System.out.println(index.getDocumentCount() + " documents");
        System.out.println(index.getIndexSize() + " tokens");
        
        DocumentReaderInterface queryReader = new TextDocumentReader(new ClassDocumentFactory(), new FilesystemReader());
        DocumentInterface queryDocument;
        String query;
        
        SearchEngine engine = new SearchEngine(index, stemming);
        
        List<String> queryFiles = new ArrayList<String>();
        queryFiles.add("misc.forsale/76057");
        queryFiles.add("talk.religion.misc/83561");
        queryFiles.add("talk.politics.mideast/75422");
        queryFiles.add("sci.electronics/53720");
        queryFiles.add("sci.crypt/15725");
        queryFiles.add("misc.forsale/76165");
        queryFiles.add("talk.politics.mideast/76261");
        queryFiles.add("alt.atheism/53358");
        queryFiles.add("sci.electronics/54340");
        queryFiles.add("rec.motorcycles/104389");
        queryFiles.add("talk.politics.guns/54328");
        queryFiles.add("misc.forsale/76468");
        queryFiles.add("sci.crypt/15469");
        queryFiles.add("rec.sport.hockey/54171");
        queryFiles.add("talk.religion.misc/84177");
        queryFiles.add("rec.motorcycles/104727");
        queryFiles.add("comp.sys.mac.hardware/52165");
        queryFiles.add("sci.crypt/15379");
        queryFiles.add("sci.space/60779");
        queryFiles.add("sci.med/59456");
        
        String result;

        int i = 1;
        for (String queryFile : queryFiles) {
        	queryDocument = queryReader.read(new File("./data/20_newsgroups_subset/" + queryFile));
        	query = queryDocument.getContent();
        
        	result = engine.search(query, i, "groupC_" + thresholdSize);
        	
        	try {
				PrintStream outputStream = new PrintStream(new BufferedOutputStream(
				   new FileOutputStream(new File("./output/query/"+thresholdSize+"_topic"+i+"_groupC"))
				));
				outputStream.println(result);
				outputStream.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	i++;
        }
        
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
