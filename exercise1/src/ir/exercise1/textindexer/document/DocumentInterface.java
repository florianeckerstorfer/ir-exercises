package ir.exercise1.textindexer.document;

/**
 * DocumentInterface
 *
 * @author Florian Eckerstorfer <florian@eckerstorfer.co>
 */
public interface DocumentInterface
{
    /**
     * Sets the name of the document.
     *
     * @param  name
     * @return
     */
    public DocumentInterface setName(String name);

    /**
     * Returns the name of the document.
     *
     * @return
     */
    public String getName();

    /**
     * Sets the name of the document.
     *
     * @param  content
     * @return
     */
    public DocumentInterface setContent(String content);

    /**
     * Returns the content of the document.
     *
     * @return
     */
    public String getContent();
}
