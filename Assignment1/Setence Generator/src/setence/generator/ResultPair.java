package setence.generator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author justinhu
 */
class ResultPair {
    private final String mWord;
    private final Double mProbility;
    
    public ResultPair(String word, double prob) {
    	mWord = word;
    	mProbility = prob;
    }

	/**
	 * @return the Word
	 */
	public String getmWord() {
		return mWord;
	}

	/**
	 * @return the Probility
	 */
	public Double getmProbility() {
		return mProbility;
	}
    
    
    
}
