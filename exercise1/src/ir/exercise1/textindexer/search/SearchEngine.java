package ir.exercise1.textindexer.search;

import ir.exercise1.textindexer.reader.file.FilesystemGzipReader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * SearchEngine
 *
 * @author hmiao87@gmail.com (Haichao Miao)
 */
public class SearchEngine {
	// TODO separate search logic from filesystem writer
	
	String index;
	FileOutputStream fos;
	BufferedOutputStream bos;
	PrintStream pos;
	
	public SearchEngine(File indexFile, File resultFile) {
		
		FilesystemGzipReader fr = new FilesystemGzipReader();
		
		try {
			fos = new FileOutputStream(resultFile);
			bos = new BufferedOutputStream(fos);
			pos = new PrintStream(bos);
			
		} catch (FileNotFoundException e) {
			//   TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		index = fr.read(indexFile);
		
	}
	
	public void search(String query) {
		
		pos.print(index);
		
		pos.close();
		
	}
	
}
