package setence.generator;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

public class Generator {
    
    public String generate(String startingWord, String[] sentenceSpec,
    		String searchStrategy, WordSearchUtil input) {
    	
    	final String resultPattern = "\"%s\"with probability %."
    			+ sentenceSpec.length*3 + "f\nTotal nodes considered: %d\n";
    	Node resultNode = null;
        int visitedNode = 0;
        Collection<Node> c;
        if(searchStrategy == "BREADTH_FIRST"){
        	c = new LinkedList<>();
        }else if(searchStrategy == "DEPTH_FIRST"){
        	c = new Stack<Node>();
        }else{
        	c = new PriorityQueue<Node>();
        }
        Node root = new Node(startingWord, 1, 1, null);
        c.add(root);

        while (!c.isEmpty()) {
            Node current;
            if(searchStrategy == "BREADTH_FIRST"){
            	current = ((Queue<Node>) c).poll() ;
            }else if(searchStrategy == "DEPTH_FIRST"){
            	current = ((Stack<Node>) c).pop();
            }else{
            	current = ((PriorityQueue<Node>) c).poll();
            }
            visitedNode++;
            // We have a candidate for best sentence.
            if (resultNode != null && (Double.compare(current.getCProbability()*current.getHeuristicValue() ,
            		resultNode.getCProbability()*resultNode.getHeuristicValue()) <= 0) ) {                            
            	continue;                            
            }
            int currentLevel = current.getLevel();
            // At end level, no child.
            if (currentLevel == sentenceSpec.length) {
                if (resultNode == null || Double.compare(current.getCProbability()*current.getHeuristicValue() ,
                		resultNode.getCProbability()*resultNode.getHeuristicValue()) > 0) {
                	resultNode = current;
                }
                continue;
            } 
            
            List<ResultPair> possibles = input.find(current.getWord(),
            		sentenceSpec[currentLevel - 1], sentenceSpec[currentLevel]);
            
            if(possibles == null){
            	continue;
            }
            
            for (ResultPair rp : possibles) {
                Node child = new Node(rp.getWord(), current.getCProbability()
                		* rp.getProbility(), currentLevel + 1, current);
                if(searchStrategy == "HEURISTIC"){
                	child.setHeuristicValue(input.getHeuristicValue(rp.getWord(),
                			sentenceSpec[currentLevel]));
                }
                c.add(child);
            }
        }
        
        String result =
        		String.format(resultPattern,constructSetence(resultNode),
        				resultNode.getCProbability(), visitedNode);
    	
        return result;
    }

    private String constructSetence(Node leaf) {
    	String result;
    	if(leaf == null){
    		result =  "";
    	}else if (leaf.getParent() == null) {
    		result = leaf.getWord();
        }else{
        	result =  constructSetence(leaf.getParent()) + " " + leaf.getWord();
        }
    	return result;
    	
    }
}
