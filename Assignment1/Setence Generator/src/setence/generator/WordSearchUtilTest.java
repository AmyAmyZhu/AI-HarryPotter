package setence.generator;

import static org.junit.Assert.*;

import java.nio.file.Paths;

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
		assertEquals(sharedUtil.find("there", "EX", "VBD").size(), 2);
	}

}
