package ir.exercise1.textindexer.reader.file;

import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.nio.MappedByteBuffer;
import java.nio.charset.Charset;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * FilesystemReader
 *
 * @author florian@eckerstorfer.co (Florian Eckerstorfer)
 */
public class FilesystemReader implements FileReaderInterface
{
    /**
     * Reads the given file.
     *
     * @param  file
     * @return
     */
    public String read(File file)
    {
        try {
            return readFile(file);
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Reads the given file from the file system.
     *
     * @param  file
     * @return
     * @throws FileNotFoundException [description]
     * @throws IOException           [description]
     */
    protected String readFile(File file)
        throws FileNotFoundException, IOException
    {
        FileInputStream stream = new FileInputStream(file);
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            return Charset.defaultCharset().decode(bb).toString();
        }
        finally {
            stream.close();
        }
    }
}
