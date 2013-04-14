package ir.exercise1.textindexer.document;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link ClassDocumentFactory}.
 *
 * @author florian@eckerstorfer.co (Florian Eckerstorfer)
 */
@RunWith(JUnit4.class)
public class ClassDocumentFactoryTest
{
    private ClassDocumentFactory factory;

    @Before
    public void setUp()
    {
        factory = new ClassDocumentFactory();
    }

    @Test
    public void newDocument()
    {
        assertThat(factory.newDocument(), is(instanceOf(ClassDocument.class)));
    }
}
