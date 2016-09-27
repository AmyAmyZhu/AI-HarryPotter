package setence.generator;

import static org.junit.Assert.*;

import org.junit.Test;

public class LineReaderTest {

	@Test
	public void testSimpleLineRead() {
		String line = "there/EX//was/VBD//0.21311475409836064";
		LineReader lr = LineReader.readLine(line);
		
		LineReader expectedLine = new LineReader("there", "EX",
				"was", "VBD", 0.21311475409836064);
		
		assertEquals(lr, expectedLine);
	}

}
