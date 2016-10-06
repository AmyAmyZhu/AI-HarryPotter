package setence.generator;

import java.io.IOException;
import java.nio.file.Paths;

public class SetenceGenerator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	final String pathToTestInputFile = "src/input.txt";
    	WordSearchUtil input;
    	Generator generator;
    	
    	try {
			input = new WordSearchUtil(Paths.get(pathToTestInputFile));
			generator = new Generator();
			System.out.println(generator.generate("benjamin", new String[]{
					"NNP","VBD","DT","NN"}, "BREADTH_FIRST",input));
			System.out.println(generator.generate("a", new String[]{
					"DT","NN","VBD","NNP"}, "BREADTH_FIRST",input));
			System.out.println(generator.generate("benjamin", new String[]{
					"NNP","VBD","DT","JJS","NN"},"BREADTH_FIRST", input));
			System.out.println(generator.generate("a", new String[]{
					"DT","NN","VBD","NNP","IN","DT","NN"}, "BREADTH_FIRST",input));
			System.out.println("********");
			System.out.println(generator.generate("benjamin", new String[]{
					"NNP","VBD","DT","NN"}, "DEPTH_FIRST",input));
			System.out.println(generator.generate("a", new String[]{
					"DT","NN","VBD","NNP"}, "DEPTH_FIRST",input));
			System.out.println(generator.generate("benjamin", new String[]{
					"NNP","VBD","DT","JJS","NN"},"DEPTH_FIRST", input));
			System.out.println(generator.generate("a", new String[]{
					"DT","NN","VBD","NNP","IN","DT","NN"}, "DEPTH_FIRST",input));
			System.out.println("********");
			System.out.println(generator.generate("benjamin", new String[]{
					"NNP","VBD","DT","NN"}, "HEURISTIC",input));
			System.out.println(generator.generate("a", new String[]{
					"DT","NN","VBD","NNP"}, "HEURISTIC",input));
			System.out.println(generator.generate("benjamin", new String[]{
					"NNP","VBD","DT","JJS","NN"},"HEURISTIC", input));
			System.out.println(generator.generate("a", new String[]{
					"DT","NN","VBD","NNP","IN","DT","NN"}, "HEURISTIC",input));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    }
    
}
