package setence.generator;

import java.util.ArrayList;
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
    Node resultNode = null;
    double maxProbability = 0.0;
    int visitedNode = 0;
    Queue<Node> queue = new LinkedList<>(); //BFS
    Stack<Node> stack;                      //DFS

    public String generate(String startingWord, String[] sentenceSpec, WordSearchUtil input) {
    	
    	resultPattern = "\"%s\"with probability %."+ sentenceSpec.length*3 + "f\nTotal nodes considered: %d\n";
    	
        Node root = new Node(startingWord, 1, 1, null, new ArrayList<Node>());
        queue.offer(root);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            visitedNode++;
            if (resultNode != null) {                             // we have a candidate for best sentence
                if (current.getCProbability() <= maxProbability) { // p is less than candidate at mid level, and each future p is <= 1, 
                    continue;                                   // so we won't achieve better result, just skip
                }
            }

            int currentLevel = current.getLevel();
            List<ResultPair> possibles = input.find(current.getWord(), sentenceSpec[currentLevel - 1], sentenceSpec[currentLevel]);
            
            if(possibles == null){
            	//if(current.getParent() != null){
            	//	current.getParent().getChildren().remove(current);
            	//}
            	continue;
            }
            
            for (ResultPair rp : possibles) {
                Node child = new Node(rp.getWord(), current.getCProbability() * rp.getProbility(), currentLevel + 1, current, null);
                //current.getChildren().add(child);        //TODO: put back use setter ?

                if (currentLevel + 1 == sentenceSpec.length) {// at end level, no child
                	visitedNode++;
                    if (child.getCProbability() >= maxProbability) {
                        resultNode = child;
                        maxProbability = child.getCProbability();
                    }
                } else {
                    //child.setChildren(new ArrayList<Node>());
                    queue.offer(child);
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
