package ir.exercise1.textindexer.reader.document;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

import ir.exercise1.textindexer.document.DocumentFactoryInterface;
import ir.exercise1.textindexer.document.DocumentInterface;
import ir.exercise1.textindexer.reader.file.FileReaderInterface;

/**
 * Tests for {@link ClassDocument}.
 *
 * @author florian@eckerstorfer.co (Florian Eckerstorfer)
 */
@RunWith(JUnit4.class)
public class TextDocumentReaderTest
{
    private DocumentFactoryInterface documentFactory;
    private FileReaderInterface fileReader;
    private TextDocumentReader reader;

    @Before
    public void setUp()
    {
        documentFactory = createMock(DocumentFactoryInterface.class);
        fileReader = createMock(FileReaderInterface.class);
        reader = new TextDocumentReader(documentFactory, fileReader);
    }

    @Test
    public void read()
    {
        File file = createMock(File.class);
        expect(file.getName()).andReturn("foo");
        replay(file);

        expect(fileReader.read(file)).andReturn("Lorem ipsum");
        replay(fileReader);

        DocumentInterface document = createMock(DocumentInterface.class);
        expect(document.setName("foo")).andReturn(document);
        expect(document.setContent("Lorem ipsum")).andReturn(document);
        replay(document);

        expect(documentFactory.newDocument()).andReturn(document);
        replay(documentFactory);

        // Call read() method and check if the result is a document.
        assertThat(reader.read(file), is(instanceOf(DocumentInterface.class)));

        verify(documentFactory);
        verify(fileReader);
        verify(document);
        verify(file);
    }
}
