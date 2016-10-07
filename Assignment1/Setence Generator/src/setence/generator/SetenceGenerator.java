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

/*	BFS
 * "benjamin saw the apple"with probability 0.001211240310
Total nodes considered: 996

"a son thanked god"with probability 0.000132052425
Total nodes considered: 358

"benjamin saw the youngest son"with probability 0.000127053179379
Total nodes considered: 82

"a son thanked god for the apple"with probability 0.000000070885699762341
Total nodes considered: 19246

*	DFS
"benjamin saw the water"with probability 0.001211240310
Total nodes considered: 996

"a son thanked god"with probability 0.000132052425
Total nodes considered: 358

"benjamin saw the youngest son"with probability 0.000127053179379
Total nodes considered: 65

"a son thanked god for the water"with probability 0.000000070885699762341
Total nodes considered: 12454

*	HEURISTIC
"benjamin saw the child"with probability 0.000512447823
Total nodes considered: 996

"a man said jack"with probability 0.000082286682
Total nodes considered: 234

"benjamin saw the youngest son"with probability 0.000127053179379
Total nodes considered: 64

"a son thanked god for a man"with probability 0.000000046145882557744
Total nodes considered: 17150

*/
