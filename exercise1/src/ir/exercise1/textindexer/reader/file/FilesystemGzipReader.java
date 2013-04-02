package ir.exercise1.textindexer.reader.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

/**
 * FilesystemGzipReader
 *
 * @author hmiao87@gmail.com (Haichao Miao)
 */
public class FilesystemGzipReader implements FileReaderInterface {

	/* (non-Javadoc)
	 * @see ir.exercise1.textindexer.reader.file.FileReaderInterface#read(java.io.File)
	 */
	@Override
	public String read(File file) {
		
		FileInputStream stream;
		GZIPInputStream gzis;
		InputStreamReader reader;
		BufferedReader in;
		String readed = "";
		
		try {
			stream = new FileInputStream(file);
	        gzis = new GZIPInputStream(stream);
	        reader = new InputStreamReader(gzis);
	        in = new BufferedReader(reader);
	        
	        String line = null;
	        
	        while ((line = in.readLine()) != null){
	        	
	        	readed += line + "\n";
	    	}
	        
	        in.close();
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return readed;
        
	}
	
	

}
