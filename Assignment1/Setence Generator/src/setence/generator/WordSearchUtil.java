package setence.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

   
public class WordSearchUtil {
	
	private HashMap<String, List<ResultPair>> mapToResult;
	private HashMap<String, Double> mapToHueristicProb;
	double sum;
	
    public List<ResultPair> find(String word, String firstWordType,String secondWordType){
    	String key = word + firstWordType + secondWordType;
    	if (mapToResult.containsKey(key)) {
    		return (ArrayList<ResultPair>)mapToResult.get(key);
		}
    	return null;
    }
    
    public Double getHeuristicValue(String secondWord, String secondWordType){
    	String key = secondWord + secondWordType;
    	if (mapToHueristicProb.containsKey(key)) {
    		return (double)mapToHueristicProb.get(key);
		}
    	return 0.0;
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
    			
    			// Add value to heuristic map
    			String keyHueristicMap = parsedLine.getSecondWord()
    					+ parsedLine.getSecondWordType();
    			if (!mapToHueristicProb.containsKey(keyHueristicMap)) {
    				mapToHueristicProb.put(keyHueristicMap, 0.0);
    			}
    			mapToHueristicProb.put(keyHueristicMap,
    					mapToHueristicProb.get(keyHueristicMap) + parsedLine.getProbability());
    		});
    		long numberOfEnteries = stream.count();
    		mapToHueristicProb.entrySet().stream()
		    			.forEach(e -> {
		    				e.setValue(e.getValue()/numberOfEnteries);
		    			});
    	}
    }
    
}
