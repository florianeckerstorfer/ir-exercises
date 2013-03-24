package ir.exercise1.textindexer.collection;

import java.util.List;
import java.util.ArrayList;

import ir.exercise1.textindexer.document.DocumentInterface;

/**
 * ClassCollection
 *
 * @author Florian Eckerstorfer <florian@eckerstorfer.co>
 */
public class ClassCollection implements CollectionInterface
{
    protected List<DocumentInterface> documents = new ArrayList<DocumentInterface>();
    protected int current = 0;

    /**
     * Adds a new document to the collection.
     *
     * @param  document
     * @return
     */
    public CollectionInterface addDocument(DocumentInterface document)
    {
        documents.add(document);
        return this;
    }

    /**
     * Returns if the document has another document.
     *
     * @return
     */
    public boolean hasNext()
    {
        return ((current+1) < documents.size());
    }

    /**
     * Returns the next document from the collection.
     *
     * @return
     */
    public DocumentInterface next()
    {
        current += 1;
        return documents.get(current);
    }

    /**
     * @throws UnsupportedOperationException
     */
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
