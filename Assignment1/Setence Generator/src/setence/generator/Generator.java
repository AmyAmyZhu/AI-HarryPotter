package setence.generator;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author justinhu
 */
public class Generator {
	public static String resultPattern = "\"%s\"with probability %f\nTotal nodes considered: %d\n";
    
    public String generate(String startingWord, String[] sentenceSpec, String searchStrategy, WordSearchUtil input) {
    	
    	resultPattern = "\"%s\"with probability %."+ sentenceSpec.length*3 + "f\nTotal nodes considered: %d\n";
    	Node resultNode = null;
        double maxProbability = 0.0;
        int visitedNode = 0;
        Collection<Node> c;
        boolean useQueue;
        if(searchStrategy == "BREADTH_FIRST" || searchStrategy == "HEURISTIC"){
        	c = new LinkedList<>();
        	useQueue = true;
        }else{
        	c = new Stack<Node>();
        	useQueue = false;
        }
        Node root = new Node(startingWord, 1, 1, null);
        c.add(root);

        while (!c.isEmpty()) {
            Node current = useQueue? ((Queue<Node>) c).poll() : ((Stack<Node>) c).pop();
            visitedNode++;
            if (resultNode != null) {                             // we have a candidate for best sentence
                if (current.getCProbability() <= maxProbability) { // p is less than candidate at mid level, and each future p is <= 1, 
                    continue;                                   // so we won't achieve better result, just skip
                }
            }

            int currentLevel = current.getLevel();
            List<ResultPair> possibles = input.find(current.getWord(), sentenceSpec[currentLevel - 1], sentenceSpec[currentLevel]);
            
            if(possibles == null){
            	continue;
            }
            
            for (ResultPair rp : possibles) {
                Node child = new Node(rp.getWord(), current.getCProbability() * rp.getProbility(), currentLevel + 1, current);

                if (currentLevel + 1 == sentenceSpec.length) {// at end level, no child
                	visitedNode++;
                    if (child.getCProbability() >= maxProbability) {
                        resultNode = child;
                        maxProbability = child.getCProbability();
                    }
                } else {
                    c.add(child);
                }
            }
        }
        
        String result = String.format(resultPattern,constructSetence(resultNode),maxProbability, visitedNode);
    	
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
