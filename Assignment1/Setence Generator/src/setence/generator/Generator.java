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

    Node resultNode = null;
    double maxProbability = 0;
    int visitedNode = 0;
    Queue<Node> queue = new LinkedList<>(); //BFS
    Stack<Node> stack;                      //DFS

    public String generate(String startingWord, String[] sentenceSpec, WordSearchUtil input) {
        Node root = new Node(startingWord, 1, 1, null, new ArrayList<Node>());
        visitedNode++;
        queue.offer(root);

        while (!queue.isEmpty()) {
            Node parent = queue.poll();
<<<<<<< 9bb40a2f57fda7a919b01fdd7262dcbfd25eb09e
            List<ResultPair> possibles = input.find(parent.word, sentenceSpec[parent.level-1],sentenceSpec[parent.level]);
            for(ResultPair rp : possibles){
                Node child = new Node();
                child.level = parent.level+1;
                child.cProbility = parent.cProbility * rp.getProbility();
                child.parent = parent;
                child.word = rp.getWord();
                parent.children.add(child);
                visitedNode ++;
                if(child.level == sentenceSpec.length){//at leaf, no child
                    child.children = null;
                    if(child.cProbility >= maxProbility ){
=======

            if (resultNode != null) {                             // we have a candidate for best sentence
                if (parent.getCProbability() <= maxProbability) { // p is less than candidate at mid level, and each future p is <= 1, 
                    continue;                                   // so we won't achieve better result, just skip
                }
            }

            int parentLevel = parent.getLevel();
            List<ResultPair> possibles = input.find(parent.getWord(), sentenceSpec[parentLevel - 1], sentenceSpec[parentLevel]);

            for (ResultPair rp : possibles) {
                Node child = new Node(rp.word, parent.getCProbability() * rp.probility, parentLevel + 1, parent, null);
                parent.getChildren().add(child);        //TODO: put back use setter ?
                visitedNode++;

                if (parentLevel + 1 == sentenceSpec.length) {// at end level, no child
                    if (child.getCProbability() >= maxProbability) {
>>>>>>> Improve Generator
                        resultNode = child;
                        maxProbability = child.getCProbability();
                    }
                } else {
                    child.setChildren(new ArrayList<Node>());
                    queue.offer(child);
                }
            }
        }

        return constructSetence(resultNode);
    }

    private String constructSetence(Node leaf) {
        if (leaf.getParent() == null) {
            return leaf.getWord();
        }
        return constructSetence(leaf.getParent()) + " " + leaf.getWord();
    }
}
