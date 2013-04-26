package ir.exercise1.similarityretrieval.reader.file;

import ir.exercise1.textindexer.model.WeightedInvertedIndex;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;

public class TextIndexFileReader implements IndexFileReaderInterface
{
	@Override
	public WeightedInvertedIndex readIndex(BufferedReader reader)
	{
		WeightedInvertedIndex index = new WeightedInvertedIndex();
		
		boolean stats = false;
		boolean metadata = false;
		boolean postinglists = false;
		
		String line;
		try {
			line = reader.readLine();
			while (null != line) {
				if (">>>STATS<<<".equals(line)) {
					stats = true;
					metadata = false;
					postinglists = false;
				} else if (">>>METADATA<<<".equals(line)) {
					metadata = true;
					postinglists = false;
					stats = false;
				} else if (">>>POSTINGLISTS<<<".equals(line)) {
					postinglists = true;
					metadata = false;
					stats = false;
				} else if (stats) {
					Scanner scanner = new Scanner(line).useDelimiter(",");
					Integer documentCount = Integer.parseInt(scanner.next());
					index.setDocumentCount(documentCount);
				} else if (metadata) {
					Scanner scanner = new Scanner(line).useDelimiter(",");
					
					Integer documentId = Integer.parseInt(scanner.next());
					String className = scanner.next();
					String documentName = scanner.next();
					Integer documentLength = Integer.parseInt(scanner.next());
					
					index.setDocument(documentId, documentName);
					index.addClassName(documentId, className);
					index.addDocumentLength(documentId, documentLength);
				} else if (postinglists) {
					Scanner scanner = new Scanner(line).useDelimiter(",");
					String token = scanner.next();
					while (scanner.hasNext()) {
						String sub = scanner.next();
						Scanner subScanner = new Scanner(sub).useDelimiter(":");
						Integer documentId = Integer.parseInt(subScanner.next());
						Double tfIdf = Double.parseDouble(subScanner.next());
						index.addToken(token, documentId, tfIdf);
					}
				}
				line = reader.readLine();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	
		return index;
	}
}
