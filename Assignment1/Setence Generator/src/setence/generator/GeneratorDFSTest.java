package setence.generator;

import java.nio.file.Paths;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GeneratorDFSTest {

	final String resultPattern = 
			"\"%s\"with probability %f\nTotal nodes considered: %d\n";
	final String pathToTestInputFile = "tst/GeneratorTest.txt";
	WordSearchUtil input;
	Generator generator;
	
	@Before
	public void setUp() throws Exception {
		input = new WordSearchUtil(Paths.get(pathToTestInputFile));
		generator = new Generator();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNoStartingWord(){
		String [] spec = new String[]{"EX","VBD","JJ"};
		String t = generator.generate("here", spec,"DEPTH_FIRST", input);
		String s = String.format(resultPattern,"",0.0,1);
		assert(s.equals(t));
	}
	
	@Test
	public void testNoPairFound1(){
		String [] spec = new String[]{"EX","VBP","JJ"};
		String t = generator.generate("there", spec,"DEPTH_FIRST", input);
		String s = String.format(resultPattern,"",0.0,1);
		assert(s.equals(t));
	}
	
	@Test
	public void testNoPairFound2(){
		String [] spec = new String[]{"EX","VBD","NN"};
		String t = generator.generate("there", spec, "DEPTH_FIRST",input);
		String s = String.format(resultPattern,"",0.0,4);
		assert(s.equals(t));
	}
	
	@Test
	public void testSetenceFound(){
		String [] spec = new String[]{"EX","VBD","JJ"};
		String t = generator.generate("there", spec, "DEPTH_FIRST",input);
		String s = String.format(resultPattern,"there was good",0.0014,5);
		assert(s.equals(t));
	}
	
	@Test
	public void testBestSetenceFound(){
		String [] spec = new String[]{"EX","VBZ","NN","IN","DT","NN"};
		String t = generator.generate("there", spec,"DEPTH_FIRST", input);
		String s = String.format(resultPattern,"there is apple in each bin",
				0.0000004375, 12);
		assert(s.equals(t));
	}
	
	@Test
	public void testVisitAllNode(){
		String [] spec = new String[]{"EX","VBD","TA"};
		String t = generator.generate("there", spec,"DEPTH_FIRST", input);
		String s = String.format(resultPattern,"there were t6", 0.012, 10);
		assert(s.equals(t));
	}
	
	@Test
	public void testVisitPartialNode(){
		String [] spec = new String[]{"EX","VBD","TC"};
		String t = generator.generate("there", spec, "DEPTH_FIRST", input);
		String s = String.format(resultPattern,"there were t6", 0.2, 6);
		assert(s.equals(t));
	}

}
