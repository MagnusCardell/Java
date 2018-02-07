
import java.util.*;

/** Base class for customizing REST API handlers.  
    Create a subclass of <code>RestApiModel</code> that defines <em>Handler</em>
    classes (subclasses of <code>RestApiHandler</code>, which have custom 
    handler methods <code>doGet()</code>, <code>doPost()</code>, etc.) to 
    handle the four conventional REST requests:
    <ul>
    <li><code>POST</code> for a <em>Create</em> service</li>
    <li><code>GET</code> for a <em>Read</em> service</li>
    <li><code>PATCH</code> for an <em>Update</em> service</li>
    <li><code>DELETE</code> for a <em>Delete</em> service</li>
    </ul> 
    We refer to the  <em>Model</em> classes, since they typically 
    interact with database tables (the term "Model" refers to the 
    Model/View/Control pattern for UI programming).  <em>Locate database 
    interactions for API calls in model and handler objects</em>;  other 
    concerns such as network communications, details of parsing requests and 
    constructing replies, finding the right handler method for an HTTP request, 
    etc., are implemented elsewhere. */


public class RestApiModel {
  /** if non-null, print debug output prefixed by <code>DEBUG</code> */
  private static String DEBUG = null;

  /** data structure of all Handler objects.  See <code>addHandler()</code> 
      to add a handler to this data structure. */
  public Hashtable<String, RestApiHandler> handlers = initHandlers();
    
  /** helper method for constructing a new <code>RestApiModel</code> object 
      @return An initial value for <code>handlers</code> */
  private Hashtable<String, RestApiHandler> initHandlers() {
    Hashtable<String, RestApiHandler> tmp = new Hashtable<String, RestApiHandler>();
    RestApiHandler h = new RestApiHandler();
    h.recordApi("", false);
    tmp.put("", h);
    return tmp;
  }

  /** Respond to a parsed REST-style HTTP request.  
      @param p Parser object <em>after</em> parsing a new HTTP request
      @return HTTP reply message for that request, according to the API */
  public String handle(HttpParser p) {
    RestApiHandler h = 
      getHandler(makeKey(p.getURLBase(), p.getURLId().equals("")));
    if (DEBUG != null) {
      System.out.println(DEBUG + "handle():  " + h);
      System.out.println(DEBUG + "handle():  " + p.getMethod());
      System.out.println(DEBUG + "handle() urlId:  \"" + p.getURLId()+ "\"");
    }
    if (p.getMethod().equals("POST")){
      return h.doPost(p);
    }
    else if (p.getMethod().equals("GET")){
      return h.doGet(p);
    }
    else if (p.getMethod().equals("PATCH"))
      return h.doPatch(p);
    else if (p.getMethod().equals("DELETE"))
      return h.doDelete(p);
    else // method not supported
      return p.makeReply(501, "Unsupported HTTP method " + p.getMethod() + ".");
  }

  /** helper method for generating Hashtable keys for <code>handlers</code> 
  data structure */
  private String makeKey(String url, boolean id) {
    return new String(url + (id ? " #" : ""));
  }

  /** helper method for generating Hashtable keys for <code>handlers</code> 
  data structure */
  private String makeKey(String url) {
    return url;
  }

  /** Add or replace a handler for a new API component.  
      @param url Base URL for the new API component
      @param id True if API component expects a final integer path component 
      (typically representing a record ID number), false if no such final 
      integer should be used 
      @param handlr Handler object providing methods such as 
      <code>doGet()</code>, <code>doPost()</code>, etc., for implementing a 
      REST-style API.  <code>handlr</code> is typically an object in a subclass 
      of <code>RestApiHandler</code> that implements some or all of those 
      four <code>doX()</code> methods.  */  

  public void addHandler(String url, boolean id, RestApiHandler handlr) {
    handlr.recordApi(url, id);
    handlers.put(makeKey(url,id), handlr);
  }

  /** Add or replace a handler for a new API component without a final integer
      in the URL.  
      @param url Base URL for the new API component
      @param handlr Handler object providing methods such as 
      <code>doGet()</code>, <code>doPost()</code>, etc., for implementing a 
      REST-style API.  <code>handlr</code> is typically an object in a subclass 
      of <code>RestApiHandler</code> that implements some or all of those 
      four <code>doX()</code> methods.  */  

  public void addHandler(String url, RestApiHandler handlr) {
    addHandler(url, false, handlr);
  }

  /** Retrieve a handler object for a particular API URL, with or without
      a final integer ID component
      @param url Base URL for an API request
      @param id True if API component expects a final integer path component 
      (typically representing a record ID number), false if no such final 
      integer should be used 
      @return Handler object for that <code>url</code>/<code>id</code> 
      combination, or default handler (no services implemented) if no such 
      handler exists in <code>handlers</code> data structure */

  public RestApiHandler getHandler(String url, boolean id) {
    RestApiHandler h = handlers.get(makeKey(url,id));
    if (h == null) {
      System.out.println("Warning: REST API handler for URL \"" + url +
			 (id ? "/#" : "") + "\"\n" + 
			 "not found, using default handler");
      h = handlers.get("");
    }
    return h;
  }
  
  /** Retrieve a handler object for a particular API URL, with no final 
      integer ID component
      @param url Base URL for an API request
      @return Handler object for that <code>url</code> and no final integer ID 
      component, or default handler (no services implemented) if no such 
      handler exists in <code>handlers</code> data structure */

  public RestApiHandler getHandler(String url) {
    return getHandler(url, false);
  }

  /** retrieve all keys in <code>handlers</code> data structure.
      @return A space-separated list of keys in the <code>handlers</code> 
      data structure.  If an API url expects a final
      integer component in its path, the base URL is followed by a space and 
      the character <code>#</code> in this return string.  */

  public String getHandlerKeys() {
    String ret = new String();
    for (Enumeration<String> e = handlers.keys(); 
	 e.hasMoreElements(); ) {
      ret += " " + e.nextElement();
    }
    
    return ret;
  }

  /** Retrieve String representations of all handler values stored in 
      <code>handlers</code> data structure.
      @return A space-separated list of string representations of the handlers
      in the <code>handlers</code> data structure.  */

  public String getHandlerValues() {
    String ret = new String();
    for (Enumeration<RestApiHandler> e = handlers.elements(); 
	 e.hasMoreElements(); ) {
      ret += " " + e.nextElement();
    }
    
    return ret;
  }
}
