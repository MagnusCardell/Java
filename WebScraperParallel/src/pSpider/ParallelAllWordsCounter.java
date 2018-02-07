package pSpider;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;


public class ParallelAllWordsCounter {
	ConcurrentHashMap<String, Integer> wordMap;
	
	public ParallelAllWordsCounter() {
		wordMap = new ConcurrentHashMap<String, Integer>(100);     //initially 100 entries
	}
	
	/**
	 * Increments the count a particular word.
	 * @param word The word to be counted.
	 */
	public void countWord(String word) {
		Integer currentCount = wordMap.putIfAbsent(word, new Integer(1));
		if (currentCount != null) {
			currentCount++;
			wordMap.replace(word, currentCount);  // Java 1.5 'unboxing' and 'boxing' of an Integer
		}

	}
	
	/** 
	 * @return An array of all WordCounter objects.  The array should
	 * have length equal to the number of WordCounter objects.
	 */
	public WordCount[] getCounts() {
		WordCount trimmed[] = new WordCount[wordMap.size()];
		// work here to get each key, val into a WordCount object in this array
		int idx = 0;
		Enumeration<String> keys = wordMap.keys();
		while (keys.hasMoreElements()) {
			String nextKey = keys.nextElement();
			int nextVal = wordMap.get(nextKey);
			WordCount nextWordCount = new WordCount(nextKey, nextVal);
			trimmed[idx] = nextWordCount;
			idx++;
		}
		Arrays.sort(trimmed);
		return trimmed;
	}

}
