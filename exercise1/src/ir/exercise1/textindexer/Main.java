package ir.exercise1.textindexer;

import ir.exercise1.textindexer.reader.collection.CollectionReaderInterface;
import ir.exercise1.textindexer.reader.collection.DirectoryAsClassCollectionReader;
import ir.exercise1.textindexer.reader.document.TextDocumentReader;
import ir.exercise1.textindexer.document.DocumentInterface;
import ir.exercise1.textindexer.collection.CollectionInterface;


class Main
{

    public static void main(String[] args)
    {
        System.out.println("Let's start by reading files from the file system.");

        CollectionReaderInterface reader = new DirectoryAsClassCollectionReader("./data/20_newsgroups_subset", new TextDocumentReader());
        CollectionInterface collection = reader.readClass("alt.atheism");
        while (collection.hasNext()) {
            DocumentInterface doc = (DocumentInterface)collection.next();
            System.out.println(doc.getName());
            // System.out.println(doc.getContent());
        }
    }
}
