package ir.exercise1.textindexer.indexer;

import ir.exercise1.textindexer.tokenizer.Tokenizer;
import ir.exercise1.textindexer.collection.CollectionInterface;
import ir.exercise1.textindexer.document.ClassDocument;
import ir.exercise1.textindexer.model.InvertedIndex;
import ir.exercise1.textindexer.model.PostingList;
import ir.exercise1.textindexer.writer.file.ArffIndexFileWriter;

import java.util.Iterator;
import java.util.Map;

public class TextIndexer implements IndexerInterface
{
	/**
	 * The lower bound of the frequency threshold.
	 */
	private double lowerThreshold = 0;

	/**
	 * The upper bound of the frequency threshold.
	 */
	private double upperThreshold = 1000;

	/**
	 * If TRUE stemming is used.
	 */
	private boolean stemming = true;

	// TODO add document class name to arff file
	// TODO change to more efficient data structures

	// TODO to save the docNames and the terms in separate lists is clumsy...
	Double[][] dictionary; // TODO -> sparse Matrix

	private CollectionInterface collection;
	private Tokenizer tokenizer;

	int docsCount = 0;
	long avgTokensPerDoc = 0;
	int termsCount = 0;
	long tokensCount = 0;

	/**
	 * Constructor.
	 *
	 * @param collection
	 *
	 * @return
	 */
	public TextIndexer(CollectionInterface collection, Tokenizer tokenizer)
	{
		this.collection = collection;
		this.tokenizer  = tokenizer;

		termsCount = 0; //  TODO calculate when terms list is finished
	}

	/**
	 * Sets the lower bound for frequency thresholding.
	 *
	 * @param lowerThreshold The lower threshold
	 */
	public void setLowerThreshold(double lowerThreshold)
	{
		this.lowerThreshold = lowerThreshold;
	}

	/**
	 * Returns the lower bound for frequency thresholding.
	 *
	 * @return The lower threshold
	 */
	public double getLowerThreshold()
	{
		return lowerThreshold;
	}

	/**
	 * Sets the upper bound for frequency thresholding.
	 *
	 * @param upperThreshold The upper threshold
	 */
	public void setUpperThreshold(double upperThreshold)
	{
		this.upperThreshold = upperThreshold;
	}

	/**
	 * Returns the upper bound for frequency thresholding.
	 *
	 * @return The upper threshold
	 */
	public double getUpperThreshold()
	{
		return upperThreshold;
	}

	/**
	 * Sets if stemming should be used.
	 *
	 * @param stemming TRUE if stemming should be used, FALSE otherwise
	 */
	public void setStemming(boolean stemming)
	{
		this.stemming = stemming;
	}

	/**
	 * Returns if stemming should be used.
	 *
	 * @return TRUE if stemming should be used, FALSE otherwise
	 */
	public boolean getStemming()
	{
		return stemming;
	}

	public void buildIndex(ArffIndexFileWriter arffWriter)
	{
		InvertedIndex index = new InvertedIndex();
		tokenizer.setInvertedIndex(index);
		
		int loopBreaker = 0; // TODO
		while (collection.hasNext()) {
			loopBreaker++; // TODO

			ClassDocument document = (ClassDocument) collection.next();
			
			System.out.println(document.getClassName() + ": " + document.getName());

			int documentId = index.addDocument(document.getName());
			tokenizer.tokenize(document, documentId);
			
			if (loopBreaker == 33) {
				System.out.println("ATTENTION! We stop indexing after 33 documents!");
				break; //  TODO
			}
		}

		termsCount = index.getIndexSize();
		docsCount = index.getDocumentCount();

		dictionary = new Double[docsCount][termsCount];

		System.out.println("# of terms: " + termsCount);

		System.out.println("computation of tf-idf weight started");
		computeWeights(index);

		System.out.println("arff index writing started");
		writeIndexToArff(index, arffWriter);


		avgTokensPerDoc = tokensCount / docsCount;

		System.out.println(docsCount + " documents");
		System.out.println(tokensCount + " tokens");
		System.out.println(termsCount + " terms");
		System.out.println(avgTokensPerDoc + " avg. # tokens per document");
	}

	private void writeIndexToArff(InvertedIndex index, ArffIndexFileWriter arffWriter)
	{
		arffWriter.createIndexFile(index, dictionary);
	}

	private void computeWeights(InvertedIndex index)
	{
		//df = # of docs in the collection that contains the term
		//idf = log(collectionSize / df)
		//tf = # of occurance of the term in document
		//tf-idf = tf x idf

		for (int row = 0; row < index.getTokens().size(); row++) {
			String token = index.getTokens().get(row);
			PostingList postingList = index.getPostingList(token);

			int df = postingList.getDocumentFrequency();

			//System.out.println("df of " + curTerm.getName() + " is " + df);
			double idf = Math.log(collection.getDocumentCount()/df);

			Iterator<Map.Entry<Integer, PostingList.Posting>> iterator = postingList.getDocuments().entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<Integer, PostingList.Posting> curDocs = iterator.next();
				PostingList.Posting posting = curDocs.getValue();
				double tf = 1+Math.log(posting.getCount());

				if (tf >= lowerThreshold && tf <= upperThreshold) {
					posting.setTfIdf(tf*idf);
					addToDictionary(curDocs.getKey(), row, tf*idf);
				}
			}
		}
	}

	private void addToDictionary(int column, int row, double weight)
	{
		 dictionary[column][row] = weight;
	}
//
//	public Double[][] getDictionaryPrototype()
//	{
//		return dictionary;
//	}
//
//	public List<String> getAllDocNamesPrototype()
//	{
//		return tokenizer.getDocumentNames();
//
//	}
//
//	public List<String> getAllTermsPrototype()
//	{
//		return tokenizer.getTerms();
//	}
}
