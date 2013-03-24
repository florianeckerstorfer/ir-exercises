package ir.exercise1.textindexer.reader.document;

import java.io.File;

import ir.exercise1.textindexer.document.DocumentInterface;

/**
 * DocumentReaderInterface
 *
 * @author Florian Eckerstorfer <florian@eckerstorfer.co>
 */
public interface DocumentReaderInterface
{
    /**
     * Reads the given document.
     *
     * @param  file
     * @return
     */
    public DocumentInterface read(File file);
}
