package ir.exercise1.textindexer.reader.collection;

import java.io.File;

import ir.exercise1.textindexer.reader.document.DocumentReaderInterface;
import ir.exercise1.textindexer.collection.CollectionInterface;
import ir.exercise1.textindexer.collection.ClassCollection;
import ir.exercise1.textindexer.document.ClassDocument;

/**
 * DirectoryAsClassCollectionReader
 *
 * Reads each subdirectory of the given baseDirectory as class and creates a collection for each
 * class.
 *
 * @author Florian Eckerstorfer <florian@eckerstorfer.co>
 */
public class ClassCollectionReader implements CollectionReaderInterface
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
    public ClassCollectionReader(String baseDirectory, DocumentReaderInterface documentReader)
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
        CollectionInterface collection = new ClassCollection();
        File directory = new File(baseDirectory);

        // Iterate through all classes
        for (final File classEntry : directory.listFiles()) {
            // Ignore files, we just look at directories
            if (classEntry.isDirectory()) {
                // Now let's iterate through all documents in the class.
                for (final File documentEntry : classEntry.listFiles()) {
                    // If the entry is a document, add it to the collection
                    if (documentEntry.isFile()) {
                        ClassDocument document = (ClassDocument)documentReader.read(documentEntry);
                        document.setClassName(classEntry.getName());
                        collection.addDocument(document);
                    }
                }
            }
        }

        return collection;
    }
}
