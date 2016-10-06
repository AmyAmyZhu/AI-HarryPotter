package setence.generator;

import static org.junit.Assert.*;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WordSearchUtilTest {

	final String pathToTestInputFile = "tst/inputTest.txt";
	WordSearchUtil sharedUtil;
	
	@Before
	public void setUp() throws Exception {
		sharedUtil = new WordSearchUtil(Paths.get(pathToTestInputFile));	
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNoExistingInitialWord() {
		assertNull(sharedUtil.find("non-existant", "EX", "EX"));
	}
	
	@Test
	public void testNoExistingFirstWordType() {
		assertNull(sharedUtil.find("there", "GG", "VBD"));
	}
	
	@Test
	public void testNoExistingSecondWordType() {
		assertNull(sharedUtil.find("there", "EX", "GG"));
	}
	
	@Test
	public void testMultiplePairsExistForCombination() {
		List<ResultPair> res = sharedUtil.find("there", "EX", "VBD");
		assertEquals(res.size(), 2);
		List<ResultPair> expected = new ArrayList<>();
		expected.add(new ResultPair("was", 0.21311475409836064));
		expected.add(new ResultPair("is", 0.21311475409836064));
		assertEquals(res, expected);
	}
	
	@Test
	public void testHueristicOnePossibleEndWord() {
		Double expectedValue = 0.0078125;
		assertEquals(sharedUtil.getHeuristicValue("each", "DT"), expectedValue);
	}
	
	@Test
	public void testHueristicMultiplePossibleEndWords() {
		Double expectedValue = (0.21311475409836064 *2 + 0.11311475409836064 *2)/5;
		assertEquals(sharedUtil.getHeuristicValue("was", "VBD"), expectedValue);
	}
	

}
