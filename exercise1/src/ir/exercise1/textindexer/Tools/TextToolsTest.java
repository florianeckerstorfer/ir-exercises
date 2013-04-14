package ir.exercise1.textindexer.tools;

import static org.junit.Assert.assertEquals;
import ir.exercise1.textindexer.stemmer.*;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * TextToolsTest
 *
 * @author hmiao87@gmail.com (Haichao Miao)
 */

@RunWith(JUnit4.class)
public class TextToolsTest {

	@Before
    public void setUp()
	{

	}

	@Test
	public void testAll_stopWords() {

		boolean b = TextTools.isStopWord("be");

		assertEquals(true, b);
	}


	@Test
	public void testDoStemming_Suffix_stripping() {
		StemmerInterface stemmer = new PorterStemmer();

		String s = TextTools.doStemming("university", stemmer);

		assertEquals("univers", s);
	}


}
