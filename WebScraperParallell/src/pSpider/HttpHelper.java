package pSpider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpHelper {
	
	/**
	 * Returns the contents of a url as a string.
	 * @param urlStr A URL to be retrieved.
	 * @return String contents or null in case of an error.
	 */
	public String retrieve(String urlStr) {
		try {
			System.err.println("retrieving " + urlStr);
			URL url = new URL(urlStr);
			InputStream in = (InputStream) url.openConnection().getContent();
			String result = "";
			while (true) {
				int c = in.read();
				if (c < 0) {
					break;
				}
				result += (char)c;
			}
			return result;
		} catch (IOException e) {
			System.err.println("http fetch of '" + urlStr + "' failed");
			e.printStackTrace();
			return null;
		}
	}
	
	private static final Pattern MATCH_HREF = Pattern.compile(
			"<a.*?href\\s*=\\s*['\"](.*?)['\"].*?>(.*?)</a>");
	
	/**
	 * Returns the web links contained in the html content.
	 * @param baseUrl The base URL for a web page.
	 * @param html Content of a web page.
	 * @return All web links contained in the argument <code>html</code>.
	 */
	public List<String> extractLinks(String baseUrl, String html) {
		if (!baseUrl.endsWith("/")) {
			baseUrl += "/";
		}
		List<String> links = new ArrayList<String>();
		Matcher m = MATCH_HREF.matcher(html.toLowerCase());
		while (m.find()) {
			String url = m.group(1);
			if (!url.startsWith("http://")) {
				url = baseUrl + url;
			}
			links.add(url);
		}
		return links;
	}
	
	/**
	 * Returns true iff the URL corresponds to an image.
	 * @param url A URL.
	 * @return True if <code>url</code> appears to be an image.
	 */
	public boolean isImage(String url) {
		url = url.toLowerCase();
		return (
				url.endsWith(".png") ||
				url.endsWith(".jpg") ||
				url.endsWith(".gif") ||
				url.endsWith(".jpeg")
			);
	}
}
