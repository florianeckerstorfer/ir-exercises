package ir.exercise1.textindexer.collection;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ir.exercise1.textindexer.document.DocumentInterface;

/**
 * Tests for {@link ClassCollection}.
 *
 * @author florian@eckerstorfer.co (Florian Eckerstorfer)
 */
@RunWith(JUnit4.class)
public class ClassCollectionTest
{
    private ClassCollection collection;
    private DocumentInterface document;

    @Before
    public void setUp()
    {
        document = createMock(DocumentInterface.class);
        collection = new ClassCollection();
    }

    @Test
    public void addDocument_and_size()
    {
        assertEquals(0, collection.size());
        collection.addDocument(document);
        assertEquals(1, collection.size());
    }

    @Test
    public void hasNext_and_next()
    {
        // At first the collection is empty
        assertFalse(collection.hasNext());
        assertNull(collection.next());

        // Add a document to the collection
        collection.addDocument(document);

        // Now there is a document in the collection
        assertTrue(collection.hasNext());
        assertEquals(document, collection.next());

        // Iterator moved forwared, no more documents in the collection
        assertFalse(collection.hasNext());
    }
}
