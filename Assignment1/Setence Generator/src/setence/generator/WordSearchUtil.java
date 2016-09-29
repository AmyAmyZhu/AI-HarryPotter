package setence.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

   
public class WordSearchUtil {
	
	/*
	 * HHH
	 * 		word1
	 * 				AAA		
	 * 						word5, probability
	 * 						word6
	 * 				CCC
	 * 						word1
	 * 		word2
	 * AAA
	 * 		word1
	 * 		word3
	 * First word type -> map(First word -> Second word type -> list of second words)
	 */
	
	private HashMap<String, List<ResultPair>> mapToResult;
	
    public List<ResultPair> find(String word, String firstWordType,String secondWordType){
    	String key = word + firstWordType + secondWordType;
    	if (mapToResult.containsKey(key)) {
    		return (ArrayList<ResultPair>)mapToResult.get(key);
		}
    	return null;
    }
    
    public WordSearchUtil(Path filePath) throws IOException {
    	mapToResult = new HashMap<>();
    	if (!Files.exists(filePath)) {
    		throw new IOException("No File found!");
    	}
    	try (Stream<String> stream = Files.lines(filePath)) {
    		stream.forEach(line -> {
    			LineReader parsedLine = LineReader.readLine(line);
    			String key = parsedLine.getFirstWord() + parsedLine.getFirstWordType()
    						+ parsedLine.getSecondWordType();
    			ResultPair value = new ResultPair(parsedLine.getSecondWord(),
    					parsedLine.getProbability());
    			if (!mapToResult.containsKey(key)) {
    				mapToResult.put(key, new ArrayList<ResultPair>());
    			}
    			List<ResultPair> currVal = (ArrayList<ResultPair>)mapToResult.get(key);
    			currVal.add(value);
    		});
    	}
    }
    
}
