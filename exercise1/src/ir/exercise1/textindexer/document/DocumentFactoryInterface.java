package ir.exercise1.textindexer.document;

/**
 * DocumentFactoryInterface
 *
 * @author Florian Eckerstorfer <florian@eckerstorfer.co>
 */
public interface DocumentFactoryInterface
{
    /**
     * Returns a new document.
     *
     * @return
     */
    public DocumentInterface newDocument();
}
