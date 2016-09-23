package setence.generator;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
    class Node{
        String word;
        double cProbility;
        int level;
        Node parent;
        ArrayList<Node> children;
    }
        
    Node resultNode = null;
    double maxProbility = 0;
    int visitedNode = 0;
    Queue<Node> queue = new LinkedList<>();

    public String generate(String startingWord, String[] sentenceSpec, WordSearchUtil input){
        Node root = new Node();
        root.level = 1;
        root.cProbility = 1;
        root.parent = null;
        root.word = startingWord;
        root.children = new ArrayList<>();
        visitedNode ++;
        queue.offer(root);
        
        while(!queue.isEmpty()){
            Node parent = queue.poll();
            List<ResultPair> possibles = input.find(parent.word, sentenceSpec[parent.level-1],sentenceSpec[parent.level]);
            for(ResultPair rp : possibles){
                Node child = new Node();
                child.level = parent.level+1;
                child.cProbility = parent.cProbility * rp.probility;
                child.parent = parent;
                child.word = rp.word;
                parent.children.add(child);
                visitedNode ++;
                if(child.level == sentenceSpec.length){//at leaf, no child
                    child.children = null;
                    if(child.cProbility >= maxProbility ){
                        resultNode = child;
                    }
                }else{
                    child.children = new ArrayList<>();
                    queue.offer(child);
                }
            }
        }
        return constructSetence(resultNode);
    }
    
    private String constructSetence(Node leaf){
        if(leaf.parent == null){
            return leaf.word;
        }
        return constructSetence(leaf.parent) + " " + leaf.word;
    }
}
