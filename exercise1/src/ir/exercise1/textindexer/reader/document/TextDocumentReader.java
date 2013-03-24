package ir.exercise1.textindexer.reader.document;

import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.nio.MappedByteBuffer;
import java.nio.charset.Charset;
import java.io.FileNotFoundException;
import java.io.IOException;

import ir.exercise1.textindexer.document.DocumentInterface;
import ir.exercise1.textindexer.document.Document;

/**
 * TextDocumentReader
 *
 * @author Florian Eckerstorfer <florian@eckerstorfer.co>
 */
public class TextDocumentReader implements DocumentReaderInterface
{
    /**
     * Reads the given document.
     *
     * @param  file
     * @return
     */
    public DocumentInterface read(File file)
    {
        DocumentInterface document = new Document();
        document.setName(file.getName());
        try {
            document.setContent(readFile(file));
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }

        return document;
    }

    /**
     * Reads the given file and returns the content as string.
     * #
     * @param  file
     * @return
     * @throws FileNotFoundException if the file doesn't exist.
     * @throws IOException           if the file can't be read.
     */
    protected String readFile(File file)
        throws FileNotFoundException, IOException
    {
        FileInputStream stream = new FileInputStream(file);
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            /* Instead of using default, pass in a decoder. */
            return Charset.defaultCharset().decode(bb).toString();
        }
        finally {
            stream.close();
        }
    }
}
