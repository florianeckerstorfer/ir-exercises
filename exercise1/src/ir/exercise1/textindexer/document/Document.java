package ir.exercise1.textindexer.document;

/**
 * Document
 *
 * @author Florian Eckerstorfer <florian@eckerstorfer.co>
 */
public class Document implements DocumentInterface
{
    protected String name;
    protected String content;

    /**
     * Constructor.
     *
     * @return
     */
    public Document()
    {
    }

    /**
     * Constructor.
     *
     * @param  name
     * @return
     */
    public Document(String name)
    {
        setName(name);
    }

    /**
     * Constructor.
     *
     * @param  name
     * @param  content
     * @return
     */
    public Document(String name, String content)
    {
        setName(name);
        setContent(content);
    }

    /**
     * Sets the name of the document.
     *
     * @param  name
     * @return
     */
    public DocumentInterface setName(String name)
    {
        this.name = name;
        return this;
    }

    /**
     * Returns the name of the document.
     *
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the content of the document.
     *
     * @param  content
     * @return
     */
    public DocumentInterface setContent(String content)
    {
        this.content = content;
        return this;
    }

    /**
     * Returns the content of the document.
     *
     * @return
     */
    public String getContent()
    {
        return content;
    }
}
