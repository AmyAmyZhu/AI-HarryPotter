package setence.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

   
/**
 * Reads through the file provided and generates helper data stores
 * to make searching easier - without needing to read the file multiple
 * times.
 */
public class WordSearchUtil {
	
	private HashMap<String, List<ResultPair>> mapToResult;
	private HashMap<String, Double> mapToHueristicProb;
	private HashMap<String, Integer> mapToTotalSecondWordType;

	
    /**
     * @param word - First word
     * @param firstWordType
     * @param secondWordType
     * @return A list of possible word combinations that fit the syntax provided.
     */
    public List<ResultPair> find(String word, String firstWordType,String secondWordType){
    	String key = word + firstWordType + secondWordType;
    	if (mapToResult.containsKey(key)) {
    		return (ArrayList<ResultPair>)mapToResult.get(key);
		}
    	return null;
    }
    
    /**
     * Uses the Heuristic function to return the heuristic probability of the
     * provided second word and word type
     * @param secondWord
     * @param secondWordType
     * @return Heuristic value of the given second word and word type.
     */
    public Double getHeuristicValue(String secondWord, String secondWordType){
    	String key = secondWord + secondWordType;
    	if (mapToHueristicProb.containsKey(key)) {
    		return (double)mapToHueristicProb.get(key)
    				/mapToTotalSecondWordType.get(secondWordType);
		}
    	return 0.0;
    }
    
    public WordSearchUtil(Path filePath) throws IOException {
    	mapToHueristicProb = new HashMap<>();
    	mapToResult = new HashMap<>();
    	mapToTotalSecondWordType = new HashMap<>();
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
    			
    			// Add value to heuristic map
    			String keyHueristicMap = parsedLine.getSecondWord()
    					+ parsedLine.getSecondWordType();
    			if (!mapToHueristicProb.containsKey(keyHueristicMap)) {
    				mapToHueristicProb.put(keyHueristicMap, 0.0);
    			}
    			mapToHueristicProb.put(keyHueristicMap,
    					mapToHueristicProb.get(keyHueristicMap) + parsedLine.getProbability());
    			
    			// Keep track of total number of second word types
    			if (!this.mapToTotalSecondWordType
    					.containsKey(parsedLine.getSecondWordType())) {
    				mapToTotalSecondWordType.put(parsedLine.getSecondWordType(), 1);
    			}
    			else {
    				mapToTotalSecondWordType.put(parsedLine.getSecondWordType(),
    						mapToTotalSecondWordType.get(parsedLine.getSecondWordType()) + 1);
    			}
    			
    			
    		});
    	}
    }
    
}
