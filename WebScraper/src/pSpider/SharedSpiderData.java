package pSpider;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SharedSpiderData {
	private static final int QUEUE_SIZE = 8000;  // ARBITRARY NUMBER FOR NOW- 
	//if it's too small, things stop when it fills. However, if it is too large, 
	//the corresponding structure holding the links encountered gets very large.

	/**
	 * Urls waiting to be scraped.  The "work" left to do.
	 */
	private ArrayBlockingQueue<String> work = new ArrayBlockingQueue<String>(QUEUE_SIZE);
	/**
	 * URLs that have already been retrieved.
	 */
	private ConcurrentLinkedQueue<String> finished = new ConcurrentLinkedQueue<String>();
	/**
	 * Contains the count of each URL that was encountered
	 */
	private ParallelAllWordsCounter urlCounter = new ParallelAllWordsCounter();
	
	/**
	 * Getter for part of the shared data.
	 * @return the java.util.concurrent.ArrayBlockingQueue used for holding the URLS to work on
	 */
	public ArrayBlockingQueue<String> getWork() {
		return work;
	}
	/**
	 * Getter for part of the shared data.
	 * @return the java.util.concurrent.ConcurrentLinkedQueue that holds those links that have been scraped.
	 */
	public ConcurrentLinkedQueue<String> getFinished() {
		return finished;
	}
	/**
	 * Getter for the word counting helper class, which contains shared data.
	 * @return the ParallelAllWordsCounter object where we keep counts of URLs
	 */
	public ParallelAllWordsCounter getUrlCounter() {
		return urlCounter;
	}


}
