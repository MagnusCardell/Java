package spider;


/**
 * Downloads web pages by following http links located
 * in the html of BEGINNING_URL.  Recursively repeats
 * the process.
 * 
 * @author shilad
 *
 */
public class RunSpider {
  private static final String BEGINNING_URL = "http://www.macalester.edu";
  
  /**
   * Run the spider program.
   * @param args Command-line arguments (unused).
   */
  public static void main(String [] args) {
    Spider spider = new Spider(10);
    spider.crawl(BEGINNING_URL);
    for (WordCount urlCount : spider.getUrlCounts()) {
      System.out.println("url " + urlCount.getWord() + " is " + 
			 urlCount.getCount());
    }
  }
  
}
