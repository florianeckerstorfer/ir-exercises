package ir.exercise1.textindexer.reader.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

/**
 * FilesystemGzipReader
 *
 * @author hmiao87@gmail.com (Haichao Miao)
 * @author florian@eckerstorfer.co (Florian Eckerstorfer)
 */
public class FilesystemGzipReader implements FileReaderInterface
{
	/* (non-Javadoc)
	 * @see ir.exercise1.textindexer.reader.file.FileReaderInterface#read(java.io.File)
	 */
	@Override
	public String read(File file)
	{
		String content = "";

		try {
	        BufferedReader reader = new BufferedReader(
	        	new InputStreamReader(new GZIPInputStream(new FileInputStream(file)))
	        );
	        String line;

	        while ((line = reader.readLine()) != null) {
	        	content += line + "\n";
	    	}

	        reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return content;
	}
}
