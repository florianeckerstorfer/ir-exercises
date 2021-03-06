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
     * Returns the number of documents in the collection.
     *
     * @return
     */
    public int size()
    {
        return documents.size();
    }

    /**
     * Returns if the document has another document.
     *
     * @return
     */
    public boolean hasNext()
    {
        return ((current+1) <= documents.size());
    }

    /**
     * Returns the next document from the collection.
     *
     * @return
     */
    public DocumentInterface next()
    {
        if (!hasNext()) {
            return null;
        }
        DocumentInterface document = documents.get(current);
        current += 1;
        return document;
    }

    /**
     * @throws UnsupportedOperationException
     */
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

	/* (non-Javadoc)
	 * @see ir.exercise1.textindexer.collection.CollectionInterface#getDocumentCnt()
	 */
	@Override
	public long getDocumentCount() {
		// TODO Auto-generated method stub
		return documents.size();
	}
}
