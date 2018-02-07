package spider;

import java.util.Arrays;


/**
 * Keeps track of counts for all words in a corpus.
 * @author shilad
 */
public class AllWordsCounter {
	private static int MAX_WORDS = 100000;
	
	private WordCount counters[] = new WordCount[MAX_WORDS];
	private int numDistinctWords = 0;
	private int numTotalWords = 0;
	
	public AllWordsCounter() {		
	}
	
	/**
	 * Increments the count for all words in a phrase.
	 * @param phrase The phrase whose words we should count.
	 */
	public void countPhrase(String phrase) {
		for (String word : phrase.split("\\s+")) {
			countWord(word);
		}
	}
	
	/**
	 * Increments the count a particular word.
	 * @param word The word to be counted.
	 */
	public void countWord(String word) {
		numTotalWords++;
		getOrCreateCounter(word).increment();
	}
	
	/**
	 * @param word A word to be counted.
	 * @return The number of times the word was counted.
	 */
	public int getCount(String word) {
		return getOrCreateCounter(word).getCount();
	}
	
	/** 
	 * @return An array of all WordCounter objects.  The array should
	 * have length equal to the number of WordCounter objects.
	 */
	public WordCount[] getCounts() {
		WordCount trimmed[] = new WordCount[numDistinctWords];
		for (int i = 0; i < numDistinctWords; i++) {
			trimmed[i] = counters[i];
		}
		Arrays.sort(trimmed);
		return trimmed;
	}
	
	/**
	 * @return The total number of counted instances of words 
	 * including duplicates.
	 */
	public int getNumTotalWords() {
		return numTotalWords;
	}
	
	/**
	 * @return The number of distinct words.  Repeated words are
	 * only counted once.
	 */
	public int getNumDistinctWords() {
		return numDistinctWords;
	}
	
	/**
	 * Gets the WordCounter associated with a particular word.
	 * If none exists, a new word is created.
	 * @param word The word whose counter should be found.
	 * @return The WordCounter associated with the word.
	 */
	private WordCount getOrCreateCounter(String word) {
		String lowerWord = word.toLowerCase();
		for (WordCount counter : counters) {
			if (counter == null) {
				break;
			} else if (counter.getWord().equals(lowerWord)) {
				// word was found.  return the counter.
				return counter;
			}
		}
		
		// word was not found.  create a new counter for it
		WordCount counter = new WordCount(lowerWord);
		counters[numDistinctWords] = counter;
		numDistinctWords++;
		return counter;
	}
}
