package ir.exercise1.textindexer.document;

/**
 * ClassDocument
 *
 * @author Florian Eckerstorfer <florian@eckerstorfer.co>
 */
public class ClassDocument extends Document
{
    protected String className;

    /**
     * Sets the name of the class.
     *
     * @param  className
     * @return
     */
    public DocumentInterface setClassName(String className)
    {
        this.className = className;
        return this;
    }

    /**
     * Returns the class name.
     *
     * @return
     */
    public String getClassName()
    {
        return className;
    }
}
