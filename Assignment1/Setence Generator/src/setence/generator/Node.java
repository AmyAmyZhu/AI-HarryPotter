/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package setence.generator;

/**
 *
 * @author justinhu
 */
public class Node implements Comparable<Node>{
        private String mWord;
        private double mCProbability;
        private double mHeuristicValue;
        private int mLevel;
        private Node mParent;
        public Node(String word, double probability, int level, Node parent){
            mWord = word;
            mCProbability = probability;
            mLevel = level;
            mParent = parent;  
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

	@Override
	public int compareTo(Node o) {
		return Double.compare(this.mCProbability+this.mHeuristicValue, o.mCProbability+o.mHeuristicValue);
	}

	public double getHeuristicValue() {
		return mHeuristicValue;
	}

	public void setHeuristicValue(double mHeuristicValue) {
		this.mHeuristicValue = mHeuristicValue;
	}
}