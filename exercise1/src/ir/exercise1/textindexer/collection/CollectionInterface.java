package ir.exercise1.textindexer.collection;

import java.util.Iterator;
import java.util.List;

import ir.exercise1.textindexer.document.DocumentInterface;

/**
 * CollectionInterface
 *
 * @author Florian Eckerstorfer <florian@eckerstorfer.co>
 */
public interface CollectionInterface extends Iterator
{
    /**
     * Adds a document to the collection.
     *
     * @param  document
     * @return
     */
    public CollectionInterface addDocument(DocumentInterface document);
    
    public long getDocumentCount();
    
 }
