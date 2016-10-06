package setence.generator;

/**
 * Used as return object for
 * @see WordSearchUtil#find(String, String, String)
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
	public String getWord() {
		return mWord;
	}

	/**
	 * @return the Probility
	 */
	public Double getProbility() {
		return mProbility;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mProbility == null) ? 0 : mProbility.hashCode());
		result = prime * result + ((mWord == null) ? 0 : mWord.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ResultPair)) {
			return false;
		}
		ResultPair other = (ResultPair) obj;
		if (mProbility == null) {
			if (other.mProbility != null) {
				return false;
			}
		} else if (!mProbility.equals(other.mProbility)) {
			return false;
		}
		if (mWord == null) {
			if (other.mWord != null) {
				return false;
			}
		} else if (!mWord.equals(other.mWord)) {
			return false;
		}
		return true;
	}
    
    
    
}
