package ir.exercise1.textindexer.stemmer;

public interface StemmerInterface
{
    public void add(char ch);
    public void add(char[] w, int wLen);
    public String toString();
    public void stem();
}