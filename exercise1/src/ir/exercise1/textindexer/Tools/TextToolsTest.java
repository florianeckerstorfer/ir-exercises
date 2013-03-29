package ir.exercise1.textindexer.Tools;

import static org.junit.Assert.assertEquals;
import ir.exercise1.textindexer.document.Document;

import java.util.ArrayList;

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
	public void testTokenize_chop_on_whitespace() {
		Document document = new Document("whitespaces", "foo bar");
		
		ArrayList<String> tokens = TextTools.tokenize(document.getContent());
		
		assertEquals(2, tokens.size());
	}
	
	@Test
	public void testTokenize_chopping() {
		Document document = new Document("do the right chop", "foo   !%/bar¤¤$    ");
		
		ArrayList<String> tokens = TextTools.tokenize(document.getContent());
		
		assertEquals("bar", tokens.get(1));
	}
	
	@Test
	public void testTokenize_chop_on_non_alphanumeric_character() {
		Document document = new Document("non-alphanumeric", "foo.bar@foo!bar,foo,=bar");
		ArrayList<String> tokens = TextTools.tokenize(document.getContent());
		
		assertEquals(6, tokens.size());
	}
	
	@Test
	public void testIgnoreStopWords() {
		Document document = new Document("stop words", "foo is bar again");
		ArrayList<String> tokens = TextTools.tokenize(document.getContent());
		
		assertEquals(2, tokens.size());
	}
	
	@Test
	public void testAll_stopWords() {
		Document document = new Document("stop words", "as we may...");
		ArrayList<String> tokens = TextTools.tokenize(document.getContent());
		
		assertEquals(0, tokens.size());
	}
	
	@Test
	public void testDoStemming_Suffix_stripping() {
		ArrayList<String> tokens = new ArrayList<String>();
		tokens.add("university");
		
		tokens = TextTools.doStemming(tokens);
		
		assertEquals("univers", tokens.get(0));
	}
	

}
