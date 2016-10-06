/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package setence.generator;

import java.io.IOException;
import java.nio.file.Paths;

/**
 *
 * @author justinhu
 */
public class SetenceGenerator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    	final String pathToTestInputFile = "src/input.txt";
    	WordSearchUtil input;
    	Generator generator;
    	
    	try {
			input = new WordSearchUtil(Paths.get(pathToTestInputFile));
			generator = new Generator();
			System.out.println(generator.generate("benjamin", new String[]{"NNP","VBD","DT","NN"}, "BREADTH_FIRST",input));
			System.out.println(generator.generate("a", new String[]{"DT","NN","VBD","NNP"}, "BREADTH_FIRST",input));
			System.out.println(generator.generate("benjamin", new String[]{"NNP","VBD","DT","JJS","NN"},"BREADTH_FIRST", input));
			System.out.println(generator.generate("a", new String[]{"DT","NN","VBD","NNP","IN","DT","NN"}, "BREADTH_FIRST",input));
			System.out.println("********");
			System.out.println(generator.generate("benjamin", new String[]{"NNP","VBD","DT","NN"}, "DEPTH_FIRST",input));
			System.out.println(generator.generate("a", new String[]{"DT","NN","VBD","NNP"}, "DEPTH_FIRST",input));
			System.out.println(generator.generate("benjamin", new String[]{"NNP","VBD","DT","JJS","NN"},"DEPTH_FIRST", input));
			System.out.println(generator.generate("a", new String[]{"DT","NN","VBD","NNP","IN","DT","NN"}, "DEPTH_FIRST",input));
			System.out.println("********");
			
			System.out.println(generator.generate("benjamin", new String[]{"NNP","VBD","DT","NN"}, "HEURISTIC",input));
			System.out.println(generator.generate("a", new String[]{"DT","NN","VBD","NNP"}, "HEURISTIC",input));
			System.out.println(generator.generate("benjamin", new String[]{"NNP","VBD","DT","JJS","NN"},"HEURISTIC", input));
			System.out.println(generator.generate("a", new String[]{"DT","NN","VBD","NNP","IN","DT","NN"}, "HEURISTIC",input));
			
			System.out.println(generator.generate("benjamin", new String[]{"NNP","VBD","DT","NN"}, "HEURISTIC",input));
			System.out.println(generator.generate("a", new String[]{"DT","NN","VBD","NNP"}, "HEURISTIC",input));
			System.out.println(generator.generate("benjamin", new String[]{"NNP","VBD","DT","JJS","NN"},"HEURISTIC", input));
			System.out.println(generator.generate("a", new String[]{"DT","NN","VBD","NNP","IN","DT","NN"}, "HEURISTIC",input));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    
}
