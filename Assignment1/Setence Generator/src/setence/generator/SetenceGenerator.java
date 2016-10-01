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
			System.out.println(generator.generate("benjamin", new String[]{"NNP","VBD","DT","NN"}, input));
			System.out.println(generator.generate("a", new String[]{"DT","NN","VBD","NNP"}, input));
			System.out.println(generator.generate("benjamin", new String[]{"NNP","VBD","DT","JJS","NN"}, input));
			System.out.println(generator.generate("a", new String[]{"DT","NN","VBD","NNP","IN","DT","NN"}, input));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    
}
