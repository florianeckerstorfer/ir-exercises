package ir.exercise1.textindexer.reader.document;

import java.io.File;

import ir.exercise1.textindexer.document.DocumentInterface;
import ir.exercise1.textindexer.document.DocumentFactoryInterface;
import ir.exercise1.textindexer.reader.file.FileReaderInterface;

/**
 * TextDocumentReader
 *
 * @author Florian Eckerstorfer <florian@eckerstorfer.co>
 */
public class TextDocumentReader implements DocumentReaderInterface
{
    protected DocumentFactoryInterface documentFactory;
    protected FileReaderInterface fileReader;

    public TextDocumentReader(DocumentFactoryInterface documentFactory, FileReaderInterface fileReader)
    {
        this.documentFactory    = documentFactory;
        this.fileReader         = fileReader;
    }

    /**
     * Reads the given document.
     *
     * @param  file
     * @return
     */
    public DocumentInterface read(File file)
    {
        DocumentInterface document = documentFactory.newDocument();
        document.setName(file.getName());
        document.setContent(fileReader.read(file));

        return document;
    }
}
