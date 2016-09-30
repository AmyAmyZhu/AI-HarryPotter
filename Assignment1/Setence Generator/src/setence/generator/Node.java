/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package setence.generator;

import java.util.ArrayList;

/**
 *
 * @author justinhu
 */
public class Node{
        private String mWord;
        private double mCProbability;
        private int mLevel;
        private Node mParent;
        private ArrayList<Node> mChildren;
        
        public Node(String word, double probability, int level, Node parent, ArrayList<Node> children){
            mWord = word;
            mCProbability = probability;
            mLevel = level;
            mParent = parent;
            mChildren = children;      
        }

    public String getWord() {
        return mWord;
    }

    public void setWord(String mWord) {
        this.mWord = mWord;
    }

    public double getCProbability() {
        return mCProbability;
    }

    public void setCProbability(double mCProbability) {
        this.mCProbability = mCProbability;
    }

    public int getLevel() {
        return mLevel;
    }

    public void setLevel(int mLevel) {
        this.mLevel = mLevel;
    }

    public Node getParent() {
        return mParent;
    }

    public void setParent(Node mParent) {
        this.mParent = mParent;
    }

    public ArrayList<Node> getChildren() {
        return mChildren;
    }

    public void setChildren(ArrayList<Node> mChildren) {
        this.mChildren = mChildren;
    }
}
