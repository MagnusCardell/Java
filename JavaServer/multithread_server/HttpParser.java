/**
Copyright (C) 2004  Juho Vh-Herttua

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/


import java.io.*;
import java.util.*;
import java.text.*;
import java.net.URLDecoder;
import java.nio.charset.*;

/** Class to parse an incoming HTTP request at a server */

public class HttpParser {
  /** array of all HTTP reply codes and titles */
  private static final String[][] HttpReplies = 
  {{"100", "Continue"},
   {"101", "Switching Protocols"},
   {"200", "OK"},
   {"201", "Created"},
   {"202", "Accepted"},
   {"203", "Non-Authoritative Information"},
   {"204", "No Content"},
   {"205", "Reset Content"},
   {"206", "Partial Content"},
   {"300", "Multiple Choices"},
   {"301", "Moved Permanently"},
   {"302", "Found"},
   {"303", "See Other"},
   {"304", "Not Modified"},
   {"305", "Use Proxy"},
   {"306", "(Unused)"},
   {"307", "Temporary Redirect"},
   {"400", "Bad Request"},
   {"401", "Unauthorized"},
   {"402", "Payment Required"},
   {"403", "Forbidden"},
   {"404", "Not Found"},
   {"405", "Method Not Allowed"},
   {"406", "Not Acceptable"},
   {"407", "Proxy Authentication Required"},
   {"408", "Request Timeout"},
   {"409", "Conflict"},
   {"410", "Gone"},
   {"411", "Length Required"},
   {"412", "Precondition Failed"},
   {"413", "Request Entity Too Large"},
   {"414", "Request-URI Too Long"},
   {"415", "Unsupported Media Type"},
   {"416", "Requested Range Not Satisfiable"},
   {"417", "Expectation Failed"},
   {"500", "Internal Server Error"},
   {"501", "Not Implemented"},
   {"502", "Bad Gateway"},
   {"503", "Service Unavailable"},
   {"504", "Gateway Timeout"},
   {"505", "HTTP Version Not Supported"}};

  /** Default headers for HTTP replies, after first line */
  public static final String [][] defaultReplyHeaders = {
    {"X-Powered-By", "St Olaf MCA"}, 
    {"Content-Type", "text/plain; charset=utf-8"}, 
    {"Content-Length", "0"}, 
    {"Date", ""}, 
    {"Connection", "close"},
  };

  /** content type for JSON HTTP replies */
  public static final String jsonContentType = "application/json; charset=utf-8";

  /** Reader used for parsing an incoming request */
  private BufferedReader reader;

  /** HTTP method, such as GET, POST, etc., from line 1 of HTTP request */
  private String method = "";

  /** URL indicated in line 1 of HTTP request, with GET parameters removed */
  private String url = "";

  /** base component of URL indicated in line 1 of HTTP request */
  private String urlBase = "";

  /** final id component of URL indicated in line 1 of HTTP request */
  private String urlId = "";

  /** Filled with header label/value pairs during parsing. 
   Current implementation assumes only single-line headers */
  private Hashtable<String,String> headers = new Hashtable<String,String>();

  /** Filled with parameter key/value pairs during parsing */
  private Hashtable<String,String> params = new Hashtable<String,String>();

  /** Major and minor HTTP version numbers, from line 1 of HTTP request */
  private int[] ver = new int[2];

  /** Byte-array constructor 
      @param buff Contains an entire HTTP request 
      @param offset First byte of HTTP request
      @param len Length of HTTP request
      @sc Initializes state variables including <code>reader</code>, after 
      removing any extraneous whitespace from lines of the raw HTTP request */

  public HttpParser(byte [] buff, int offset, int len) {
    // remove any whitespace garbage from ends of lines
    String str = new String(buff, offset, len, StandardCharsets.UTF_8);
    //System.out.println("str: " + str);
    String [] strs = str.split("\n");
    str = "";
    for (int i = 0;  i < strs.length;  i++)
      str += strs[i].trim() + "\n";
    //System.out.println("str: " + str);
        
    reader = new BufferedReader(
	       new InputStreamReader(
	         new ByteArrayInputStream(str.getBytes())));
  }

  /** Stream constructor.  Intended for use with InputStream from a Socket, but 
      doesn't behave well sometimes if whitespace appears before newlines.
      @param is For reading input directly from a Socket. */

  public HttpParser(InputStream is) {
    reader = new BufferedReader(new InputStreamReader(is));
  }

  /** Perform parsing on a constructed object.
      @return An HTTP reply code.  Value 200 indicates that parsing succeeded, 
      other values indicate the nature of a parse failure.  */

  public int parseRequest() throws IOException {
    String initial, cmd[], temp[];
    int ret, idx, i;

    ret = 200; // default is OK now
    initial = reader.readLine();
    if (initial == null || initial.length() == 0) return 0;
    if (Character.isWhitespace(initial.charAt(0))) {
      // starting whitespace, return bad request
      return 400;
    }

    cmd = initial.split("\\s");
    if (cmd.length != 3) {
      return 400;
    }

    if (cmd[2].indexOf("HTTP/") == 0 && cmd[2].indexOf('.') > 5) {
      temp = cmd[2].substring(5).split("\\.");
      try {
        ver[0] = Integer.parseInt(temp[0]);
        ver[1] = Integer.parseInt(temp[1]);
      } catch (NumberFormatException nfe) {
        ret = 400;
      }
    }
    else ret = 400;

    //System.out.println("DEBUG:  cmd[0]=" + cmd[0]);
    if (cmd[0].equals("GET") || cmd[0].equals("HEAD")) {
      method = cmd[0];
      idx = cmd[1].indexOf('?');
      if (idx < 0) 
	setUrl(cmd[1]);
      else {
        setUrl(URLDecoder.decode(cmd[1].substring(0, idx), "ISO-8859-1"));
	parseGETParams(cmd[1].substring(idx+1));
      }
      parseHeaders();
      if (headers == null) 
	ret = 400;
      if (!parsePOSTParams())
	ret = 400;

    } else if (cmd[0].equals("POST")) {
      method = cmd[0];
      setUrl(cmd[1]);
      parseHeaders();
      if (headers == null) 
	ret = 400;
      if (!parsePOSTParams())
	ret = 400;

    } else if (ver[0] == 1 && ver[1] >= 1) {

      if (cmd[0].equals("PATCH")) {
	method = cmd[0];
	setUrl(cmd[1]);
	parseHeaders();
	if (headers == null) 
	  ret = 400;
	if (!parsePOSTParams())
	  ret = 400;

      } else if (cmd[0].equals("DELETE")) {
	method = cmd[0];
	setUrl(cmd[1]);
	parseHeaders();
	if (headers == null) 
	  ret = 400;
	if (!parsePOSTParams())
	  ret = 400;


      } else if (cmd[0].equals("OPTIONS") ||
		 cmd[0].equals("PUT") ||
		 cmd[0].equals("TRACE") ||
		 cmd[0].equals("CONNECT")) {
        ret = 501; // not implemented
      }
    }
    else {
      // meh not understand, bad request
      ret = 400;
    }

    if (ver[0] == 1 && ver[1] >= 1 && getHeader("Host") == null) {
      ret = 400;
    }

    return ret;
  }

  /** Helper method for parseRequest(), to perform parsing of headers in 
      an HTTP request. */

  private void parseHeaders() throws IOException {
    String line;
    int idx;

    // rfc822 allows multiple lines, but that's not implemented here
    line = reader.readLine();
    while (!line.trim().equals("")) {
      idx = line.indexOf(':');
      if (idx < 0) {
        headers = null;
        break;
      }
      else {
        headers.put(line.substring(0, idx).toLowerCase(), line.substring(idx+1).trim());
      }
      line = reader.readLine();
    }
  }
  
  /** Helper method for parseRequest(), to perform parsing of GET-style 
      parameters in URL portion of an HTTP request. 
      @return True on success, false if string contains a parameter element 
      that contains no <code>=</code>. */

  private boolean parseGETParams(String paramString) 
  throws UnsupportedEncodingException {
    String [] prms = paramString.split("&");
    int i;
    
    //params = new Hashtable<String,String>();  // RAB note - why another?
    for (i=0; i<prms.length; i++) {
      String [] temp = prms[i].split("=");
      if (temp.length == 2) {
	// we use ISO-8859-1 as temporary charset and then
	// String.getBytes("ISO-8859-1") to get the data
	params.put(URLDecoder.decode(temp[0], "ISO-8859-1"),
		   URLDecoder.decode(temp[1], "ISO-8859-1"));
      }
      else if(temp.length == 1 && prms[i].indexOf('=') == prms[i].length()-1) {
	// handle empty string separately
	params.put(URLDecoder.decode(temp[0], "ISO-8859-1"), "");
      }
    }
    return true;
  }

  /** Helper method for parseRequest(), to perform parsing of POST-style 
      parameters in content portion of an HTTP request. 
      @return True on success, false if <code>reader</code> encounters a line 
      that contains no <code>=</code>.*/

  private boolean parsePOSTParams() throws IOException {
    String line;
    int idx;

    line = reader.readLine();
    while (line != null) {
      System.out.println("DEBUG: " + line);
      idx = line.indexOf('=');
      if (idx < 0) {
        return false;
      } else {
        params.put(line.substring(0, idx).toLowerCase(), 
		   line.substring(idx+1).trim());
      }
      line = reader.readLine();
    }
    return true;
  }

  /** retrieve HTTP method from a parsed HTTP request */

  public String getMethod() {
    return method;
  }

  /** retrieve value of one header from a parsed HTTP request 
      @param label Label identifying a header line in that HTTP request 
      @return Value for header <code>label</code>, or null if no such header. */

  public String getHeader(String label) {
    if (headers != null)
      return (String) headers.get(label.toLowerCase());
    else return null;
  }

  /** retrieve all headers from a parsed HTTP request 
      @return Holds all label/value header pairs from that parsed HTTP request */

  public Hashtable<String,String> getHeaders() {
    return headers;
  }

  /** retrieve target URL from a parsed HTTP request 
      @return URL indicated in first line of that HTTP request */

  public String getRequestURL() {
    return url;
  }

  /** retrieve base URL for a REST API request.
      @return Initial portion of the original request <code>url</code> that 
      indicates an API action to be performed.  This portion of <code>url</code>
<ul>
<li>begins with <code>/</code>,</li>

<li>excludes a final path component if that component parses to an integer (see <code>getURLId()</code> to retrieve that component if present), and</li>

<li>does not end in <code>/</code> <em>except when</em> <code>urlId</code> = <code>/</code>.</li>

</ul>
 */ 

  public String getURLBase() {
    return urlBase;
  }

  /** retrieve final integer component of URL for a REST API request, if present.
      @return If the final component of the request <code>url</code> parses 
      to an integer, return that component (with no <code>/</code> characters).
      Otherwise, return empty string.   */

  public String getURLId() {
    return urlId;
  }

  /** retrieve value of one parameter from a parsed HTTP request 
      @param key Key identifying a (GET or POST-style) parameter 
      in that HTTP request 
      @return Value for that parameter <code>key</code>, or null if no such 
      parameter. */

  public String getParam(String key) {
    return params.get(key);
  }

  /** retrieve all parameters from a parsed HTTP request 
      @return Holds all key/value parameter pairs from that parsed HTTP 
      request */

  public Hashtable<String,String> getParams() {
    return params;
  }

  /** Retrieve HTTP version for a parsed HTTP request
      @return HTTP version as it appears in first line of that HTTP request */

  public String getVersion() {
    return ver[0] + "." + ver[1];
  }

  /** Compare HTTP version in a parsed HTTP request to alternative major/minor
      version numbers
      @param major An HTTP major version number
      @param minor An HTTP minor version number
      @return Negative if that request's HTTP version precedes 
      <code>major</code>.<code>minor</code>;  
      positive if that request's version comes after 
      <code>major</code>.<code>minor</code>;  and
      zero if the the version levels are the same. */

  public int compareVersion(int major, int minor) {
    if (major < ver[0]) return -1;
    else if (major > ver[0]) return 1;
    else if (minor < ver[1]) return -1;
    else if (minor > ver[1]) return 1;
    else return 0;
  }


  /** helper for parseRequest that assigns to URL-related state variables
      @param origURL URL obtained from first line of HTTP request after
      breaking off any GET-style parameters.
      @sc Assigns <code>origURL</code> to state variable <code>url</code>, 
      assigns a normalized version (see getUrlBase() documentation) 
      of <code>origURL</code> after removing final integer path component 
      (if any) to <code>urlBase</code>, and assigns final integer path 
      component (or "" if none) to <code>urlId</code>.  
   */
  private void setUrl(String origURL) {

    url = origURL;
      urlBase = url;
      // normalize urlBase to start with a slash, and not finish with one
      // except in the root case urlBase = "/"
      if (urlBase.charAt(0) != '/')
	urlBase = "/" + urlBase;
      int lastSlash = urlBase.lastIndexOf('/');
      while (lastSlash != 0 && lastSlash == urlBase.length() - 1) {
	urlBase = urlBase.substring(0,lastSlash);
	lastSlash = urlBase.lastIndexOf('/');
      }
      // urlBase normalized, and lastSlash is index of / before final component

      try {
	String last = urlBase.substring(lastSlash+1);  // final component
	Integer.parseInt(last);
	urlId = last;
	urlBase = urlBase.substring(0, lastSlash);
      } catch (NumberFormatException e) {};

  }


  /** generate an HTTP reply message 
      @param code HTTP reply code
      @param hdrs Headers to supplement/override <code>defaultReplyHeaders</code>
      @param body Content of HTTP reply
      @return Complete HTTP reply message
   */

  public static String makeReply(int code, String [][] moreHdrs, String body) {
    String ret = new String("HTTP/1.1 " + getHttpReply(code) + "\n");
    Hashtable<String,String> hdrVals = new Hashtable<String,String>();

    /* It's not necessary for (single-line) headers to appear in a consistent 
       order, but it's useful for writing code tests, so we implement it */
    String hdrKeys0 = " "; // records header keys in order of appearance
    int i;
    for (i = 0;  i < defaultReplyHeaders.length;  i++) {
      hdrVals.put(defaultReplyHeaders[i][0], defaultReplyHeaders[i][1]);
      hdrKeys0 += defaultReplyHeaders[i][0] + " ";
    }
    hdrVals.put("Date", getDateHeaderValue());
    hdrVals.put("Content-Length", Integer.toString(body.length()));
    for (i = 0;  i < moreHdrs.length;  i++) {
      hdrVals.put(moreHdrs[i][0], moreHdrs[i][1]);
      if (hdrKeys0.indexOf(" " + moreHdrs[i][0] + " ") == -1)
	hdrKeys0 += moreHdrs[i][0] + " ";
    }
    String [] hdrKeys = hdrKeys0.split(" ");
    for (i = 1;  i < hdrKeys.length; i++) {
      ret += hdrKeys[i] + ": " + hdrVals.get(hdrKeys[i]) + "\n";
    }
    ret += "\n";
    ret += body;
    return ret;
  }

  /** generate an HTTP reply message 
      @param code HTTP reply code
      @param body Content of HTTP reply
      @return Complete HTTP reply message
   */

  public static String makeReply(int code, String body) {
    return makeReply(code, new String[][] {}, body);
  }

  /** generate an HTTP reply message with an empty content
      @param code HTTP reply code
      @return Complete HTTP reply message
   */

  public static String makeReply(int code) {
    return makeReply(code, "");
  }

  /** generate an HTTP reply message with JSON content
      @param code HTTP reply code
      @param body Content of HTTP reply
      @return Complete HTTP reply message
   */

  public static String makeJsonReply(int code, String body) {
    return makeReply(code, new String[][] {
	{"Content-Type", HttpParser.jsonContentType}
      }, body);
  }


  /** Retrieve title for a given HTTP reply code
      @param HTTP reply code
      @return Title for that HTTP reply code <code>codevalue</code> */

  public static String getHttpReply(int codevalue) {
    String key, ret;
    int i;

    ret = null;
    key = "" + codevalue;
    for (i=0; i<HttpReplies.length; i++) {
      if (HttpReplies[i][0].equals(key)) {
        ret = codevalue + " " + HttpReplies[i][1];
        break;
      }
    }

    return ret;
  }

  
  /** generate value for a <code>Date:</code> header for an HTTP reply message
      @return Properly formatted <code>Date:</code> header value for the current 
      time. */

  public static String getDateHeaderValue() {
    SimpleDateFormat format;
    String ret;

    format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US);
    format.setTimeZone(TimeZone.getTimeZone("GMT"));
    ret = format.format(new Date()) + " GMT";

    return ret;
  }
}
