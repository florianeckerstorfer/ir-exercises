package ir.exercise1.textindexer.indexer;

import ir.exercise1.textindexer.writer.file.IndexFileWriterInterface;

/**
 * IndexerInterface
 *
 * @author Florian Eckerstorfer (florian@eckerstorfer.co)
 */
public interface IndexerInterface
{
    public void buildIndex(IndexFileWriterInterface arffWriter);
}
