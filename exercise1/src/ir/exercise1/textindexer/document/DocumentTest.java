package ir.exercise1.textindexer.document;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link ClassCollection}.
 *
 * @author florian@eckerstorfer.co (Florian Eckerstorfer)
 */
@RunWith(JUnit4.class)
public class DocumentTest
{
    private Document document;

    @Before
    public void setUp()
    {
        document = new Document();
    }

    @Test
    public void Document_withName()
    {
        document = new Document("Foo");
        assertEquals("Foo", document.getName());
    }

    @Test
    public void Document_withNameAndContent()
    {
        document = new Document("Foo", "Lorem ipsum");
        assertEquals("Foo", document.getName());
        assertEquals("Lorem ipsum", document.getContent());
    }

    @Test
    public void setName_and_getName()
    {
        document.setName("Foo");
        assertEquals("Foo", document.getName());
    }

    @Test
    public void setContent_and_getContent()
    {
        document.setContent("Lorem ipsum");
        assertEquals("Lorem ipsum", document.getContent());
    }
}
