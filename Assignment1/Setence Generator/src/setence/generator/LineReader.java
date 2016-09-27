package setence.generator;

public class LineReader {

	private final String mFirstWord;
	private final String mSecondWord;
	private final String mFirstWordType;
	private final String mSecondWordType;
	private final Double mProbability;

	/**
	 * Convenience initializer for reading a line from an input text file.
	 * @param line A string for the following format: 
	 * 		  firstWord/typeOfWord//secondWord/typeOfWord//probability
	 * @return LineReader where the individual components can be accessed
	 * 		   conveniently.
	 */
	public static LineReader readLine(String line) {
		final int posFirstWordSeperator = line.indexOf("/");
		final String firstWord = line.substring(0, posFirstWordSeperator);
		line = line.substring(posFirstWordSeperator+1);
		final int posFirstWordTypeSeperator = line.indexOf("/");
		final String firstWordType = line.substring(0, posFirstWordTypeSeperator);
		line = line.substring(posFirstWordTypeSeperator+2);
		
		final int posSecondWordSeperator = line.indexOf("/");
		final String secondWord = line.substring(0, posSecondWordSeperator);
		line = line.substring(posSecondWordSeperator+1);
		final int posSecondWordTypeSeperator = line.indexOf("/");
		final String secondWordType = line.substring(0, posSecondWordTypeSeperator);
		line = line.substring(posSecondWordTypeSeperator+2);
		
		double probability = Double.parseDouble(line);
		
		return new LineReader(firstWord, firstWordType, secondWord,
				secondWordType, probability);
	}
		
	public String getFirstWord() {
		return mFirstWord;
	}

	public String getSecondWord() {
		return mSecondWord;
	}

	public String getFirstWordType() {
		return mFirstWordType;
	}

	public String getSecondWordType() {
		return mSecondWordType;
	}

	public Double getProbability() {
		return mProbability;
	}

	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mFirstWord == null) ? 0 : mFirstWord.hashCode());
		result = prime * result + ((mFirstWordType == null) ? 0 : mFirstWordType.hashCode());
		result = prime * result + ((mProbability == null) ? 0 : mProbability.hashCode());
		result = prime * result + ((mSecondWord == null) ? 0 : mSecondWord.hashCode());
		result = prime * result + ((mSecondWordType == null) ? 0 : mSecondWordType.hashCode());
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
		if (!(obj instanceof LineReader)) {
			return false;
		}
		LineReader other = (LineReader) obj;
		if (mFirstWord == null) {
			if (other.mFirstWord != null) {
				return false;
			}
		} else if (!mFirstWord.equals(other.mFirstWord)) {
			return false;
		}
		if (mFirstWordType == null) {
			if (other.mFirstWordType != null) {
				return false;
			}
		} else if (!mFirstWordType.equals(other.mFirstWordType)) {
			return false;
		}
		if (mProbability == null) {
			if (other.mProbability != null) {
				return false;
			}
		} else if (!mProbability.equals(other.mProbability)) {
			return false;
		}
		if (mSecondWord == null) {
			if (other.mSecondWord != null) {
				return false;
			}
		} else if (!mSecondWord.equals(other.mSecondWord)) {
			return false;
		}
		if (mSecondWordType == null) {
			if (other.mSecondWordType != null) {
				return false;
			}
		} else if (!mSecondWordType.equals(other.mSecondWordType)) {
			return false;
		}
		return true;
	}
	
	/* private -> default : testing */
	LineReader(String firstWord, String firstWordType,
			String secondWord, String secondWordType, Double probability) {
		mFirstWord = firstWord;
		mSecondWord = secondWord;
		mFirstWordType = firstWordType;
		mSecondWordType = secondWordType;
		mProbability = probability;
	}

}
