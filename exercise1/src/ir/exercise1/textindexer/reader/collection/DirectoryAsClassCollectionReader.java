package ir.exercise1.textindexer.reader.collection;

import java.io.File;

import ir.exercise1.textindexer.reader.document.DocumentReaderInterface;
import ir.exercise1.textindexer.collection.CollectionInterface;
import ir.exercise1.textindexer.collection.ClassCollection;

/**
 * DirectoryAsClassCollectionReader
 *
 * Reads each subdirectory of the given baseDirectory as class and creates a collection for each
 * class.
 *
 * @author Florian Eckerstorfer <florian@eckerstorfer.co>
 */
public class DirectoryAsClassCollectionReader implements CollectionReaderInterface
{
    protected String baseDirectory;
    protected DocumentReaderInterface documentReader;

    /**
     * Constructor.
     *
     * @param  baseDirectory
     * @param  documentReader
     * @return
     */
    public DirectoryAsClassCollectionReader(String baseDirectory, DocumentReaderInterface documentReader)
    {
        this.baseDirectory      = baseDirectory;
        this.documentReader     = documentReader;
    }

    /**
     * Read the entire collection.
     *
     * @return
     */
    public CollectionInterface read()
    {
        return null;
    }

    /**
     * Read the collection of the given class.
     *
     * @param  className
     * @return
     */
    public CollectionInterface readClass(String className)
    {
        CollectionInterface collection = new ClassCollection(className);
        File directory = new File(baseDirectory + "/" + className);
        for (final File entry : directory.listFiles()) {
            if (entry.isFile()) {
                collection.addDocument(documentReader.read(entry));
            }
        }
        return collection;
    }
}
