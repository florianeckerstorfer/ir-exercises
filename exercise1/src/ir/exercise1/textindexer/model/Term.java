package ir.exercise1.textindexer.model;

import java.util.Hashtable;

/**
 * Term
 *
 * @author hmiao87@gmail.com (Haichao Miao)
 */
public class Term {
	
	private String name = "";
	private Hashtable<String, Integer> docFreq = new Hashtable<String, Integer>();
	
	public Term (String name) {
		this.name = name;
	}
	
	public void addDoc(String docName) {
		if(!docFreq.containsKey(docName)) {
			docFreq.put(docName, 1);
		} else {
			docFreq.put(docName, docFreq.get(docName) + 1);
		}
	}
	
	/**
	 * @return the termName
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param termName the termName to set
	 */
	public void setName(String termName) {
		this.name = termName;
	}

	/**
	 * @return the docFreq
	 */
	public Hashtable<String, Integer> getDocFreq() {
		return docFreq;
	}

	/**
	 * @param docFreq the docFreq to set
	 */
	public void setDocFreq(Hashtable<String, Integer> docFreq) {
		this.docFreq = docFreq;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Term [name=" + name + ", docFreq=" + docFreq + "]";
	}
	
	
}
