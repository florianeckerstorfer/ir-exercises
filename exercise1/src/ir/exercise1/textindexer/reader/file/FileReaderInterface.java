package ir.exercise1.textindexer.reader.file;

import java.io.File;

/**
 * FilesystemReader
 *
 * @author florian@eckerstorfer.co (Florian Eckerstorfer)
 */
public interface FileReaderInterface
{
    /**
     * Reads the given file.
     *
     * @param  file
     * @return
     */
    public String read(File file);
}
