package ir.exercise1.textindexer.writer.file;

import ir.exercise1.textindexer.model.InvertedIndex;
import ir.exercise1.textindexer.model.PostingList;

import java.io.PrintStream;

/**
 * ArffFileWriter
 *
 * @author hmiao87@gmail.com (Haichao Miao)
 * @author florian@eckerstorfer.co (Florian Eckerstorfer)
 */
public class ArffIndexFileWriter
{
	PrintStream outputStream;

	public ArffIndexFileWriter(PrintStream outputStream)
	{
		this.outputStream = outputStream;
	}

	public void createIndexFile(InvertedIndex index)
	{
		//header
		outputStream.println("% 1. Title: 20_newsgroups_subset Index");
		outputStream.println("% 2. Sources:");
		outputStream.println("% \t Creator: Haichao Miao & Florian Eckerstorfer");
		outputStream.println("% \t Date: " + new java.util.Date());
		outputStream.println("% allow_stemming: "); // TODO
		outputStream.println("% upper_bound: "); // TODO
		outputStream.println("% lower_bound: "); // TODO
		outputStream.print("@RELATION ");
		outputStream.println("20_newsgroups_subset"); // TODO
		outputStream.println();
		outputStream.println("@ATTRIBUTE className STRING");
		outputStream.println("@ATTRIBUTE docID STRING");

		
		for(String token : index.getTokens()) {
			outputStream.print("@ATTRIBUTE ");
			outputStream.print(token);
			outputStream.println(" NUMERIC");
		}

		outputStream.println("@DATA");
		
		PostingList postingList;
		PostingList.Posting posting;
		
		for (int documentId = 0; documentId < index.getDocumentCount(); documentId++) {
			outputStream.print("className, "); // TODO
			outputStream.print(index.getDocumentName(documentId));
			
			for(String token : index.getTokens()) {
				postingList = index.getPostingList(token);
				posting = postingList.getPosting(documentId);
				
				if (null != posting) {
					outputStream.print(posting.getTfIdf());
				} else {
					outputStream.print(0);
				}
				
				outputStream.print(", ");
			}
			outputStream.print("\n");
		}

		outputStream.close();

	}





}
