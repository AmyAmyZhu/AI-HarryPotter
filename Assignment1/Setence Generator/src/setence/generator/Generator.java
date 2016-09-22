
import java.util.ArrayList;

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
        Node parent;
        ArrayList<Node> children;
    }
    
    public String generate(String startingWord, String[] sentenceSpec, Graph input){
        Node baseNode = new Node();
        baseNode.cProbility = 1;
        baseNode.parent = null;
        baseNode.word = startingWord;
        
        for each 
        
        return null;
    }
}
