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
public class ClassDocumentTest
{
    private ClassDocument document;

    @Before
    public void setUp()
    {
        document = new ClassDocument();
    }

    @Test
    public void setClassName_and_getClassName()
    {
        document.setClassName("Foobar");
        assertEquals("Foobar", document.getClassName());
    }
}
