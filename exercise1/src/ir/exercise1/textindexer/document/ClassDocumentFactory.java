package ir.exercise1.textindexer.document;

/**
 * ClassDocumentFactory
 *
 * @author Florian Eckerstorfer <florian@eckerstorfer.co>
 */
public class ClassDocumentFactory implements DocumentFactoryInterface
{
    /**
     * Returns a new class document.
     *
     * @return
     */
    public DocumentInterface newDocument()
    {
        return new ClassDocument();
    }
}