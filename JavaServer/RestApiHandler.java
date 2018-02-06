

/** base class for REST API handler of a specific base URL.  
    Override these methods to implement an API action for a specific urlBase */

public class RestApiHandler {

  String urlBase = "";
  boolean ifId = false;
  
  /** for base URLs without required appended ID numbers
      @param url A url as stored in <code>HttpParser.urlBase</code> */

  public String toString() {
    return new String("[RestApiHandler \"" + urlBase + 
		      (ifId ? "/#" : "") + "\"]");
  }

  /** record base URL and whether or not an appended ID number is required
      @param url A url as stored in <code>HttpParser.urlBase</code> 
      @param id True if an ID must be appended to URL, false otherwise */

  public void recordApi(String url, boolean id) {
    urlBase = url;
    ifId = id;
  }

  /** handler for POST (Create) messages for this urlBase
      @param Result of parsing an incoming HTTP request
      @return Reply message after acting on that parsed request;  in this case, 
      the message indicated code 501, Not Implemented */

  public String doPost(HttpParser parser) {
    return defaultHandler(parser);
  }

  /** handler for GET (Read) messages for this urlBase
      @param Result of parsing an incoming HTTP request
      @return Reply message after acting on that parsed request;  in this case, 
      the message indicated code 501, Not Implemented */

  public String doGet(HttpParser parser) {
    return defaultHandler(parser);
  }

  /** handler for PATCH (Update) messages for this urlBase
      @param Result of parsing an incoming HTTP request
      @return Reply message after acting on that parsed request;  in this case, 
      the message indicated code 501, Not Implemented */

  public String doPatch(HttpParser parser) {
    return defaultHandler(parser);
  }

  /** handler for DELETE (Delete) messages for this urlBase
      @param Result of parsing an incoming HTTP request
      @return Reply message after acting on that parsed request;  in this case, 
      the message indicated code 501, Not Implemented */

  public String doDelete(HttpParser parser) {
    return defaultHandler(parser);
  }

  /** default handler for messages for this urlBase
      @param Result of parsing an incoming HTTP request
      @return Reply message after acting on that parsed request;  in this case, 
      the message indicated code 501, Not Implemented */

  private String defaultHandler(HttpParser parser) {
    return parser.makeReply(501);
  }
}
