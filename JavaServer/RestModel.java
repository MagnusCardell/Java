import java.nio.file.*;
import java.util.*;
import java.io.*;
class RestModel extends RestApiModel {

  public RestModel() {
    addHandler("/add_text", new TextHandler());
    addHandler("/get_text", new TextHandler());
  }
  

  
  class TextHandler extends RestApiHandler {

    public String doPatch(HttpParser p) {
      String id = p.getURLId();
      Hashtable<String,String> text= p.getParams();
      try(FileWriter fw = new FileWriter(id+".txt", false);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(bw)){
        out.println(text.get("text"));
      } catch (IOException e) {
        System.out.println("Error in doPatch() writing file: " + e);
        return p.makeReply(400);
      }
      return p.makeReply(200, "OK");
    }

    public String doPost(HttpParser p) {
      return doPatch(p);
    }

    public String doGet(HttpParser p) {
      String id = p.getURLId();
      String text ="";
      try{
        FileReader fr = new FileReader(id+".txt");
        BufferedReader br = new BufferedReader(fr);
        String s;
        while((s = br.readLine()) != null) {
          System.out.println(s);
          text += s;
        }
        fr.close();
      }catch(FileNotFoundException e){
        System.out.println("File not found");
        return p.makeReply(400);
      }catch(IOException e){
        System.out.println("File read IOException");
        return p.makeReply(400);
      }
      String jsonStr= new String(text);
      return p.makeJsonReply(200, jsonStr);
    }
}

class CountHandler extends RestApiHandler {

    /* simulates SQL   CREATE TABLE count (val int);
    INSERT INTO count VALUES (0);     */
    int count_val = 0;  

    public String doPost(HttpParser p) {
      return doPatch(p);
    }

    public String doPatch(HttpParser p) {
      /* Simulates SQL   UPDATE count SET val = val + 1;   */
      count_val++;
      return p.makeReply(200, "OK");
    }

    public String doGet(HttpParser p) {
      /* Simulates SQL   SELECT val FROM count;   */
      return p.makeJsonReply(200, 
        "{ \"count\": " + Integer.toString(count_val) + 
        " }");
    }
  }



  class NamesHandler extends RestApiHandler {
    static final int maxnames = 10000;

    
    /* simulates SQL    CREATE TABLE names (name text);    */
    String [] names = new String[maxnames];  
    int ct = 0;

    public String doPatch(HttpParser p) {
      /* Simulates SQL   INSERT INTO names VALUES (&sto:ENTRY;);   */
      if (ct == maxnames)
       return p.makeReply(500, "Simulated table full");
      // safe to insert into the simulated table
     names[ct++] = p.getParam("name");
     return p.makeReply(200, "OK");
   }

   public String doPost(HttpParser p) {
    return doPatch(p);
  }

  public String doGet(HttpParser p) {
    /* Simulates SQL   SELECT name FROM names;   */
    String jsonStr = new String("{ \"names\": [\n");
    for (int i = 0;  i < ct;  i++) {
     jsonStr += "    ";
     if (names[i] == null)
       jsonStr += "null,\n";
     else
       jsonStr += "\"" + names[i] + "\",\n";
   }
      // remove final comma if present and terminate string
   if (ct > 0)
     jsonStr = jsonStr.substring(0, jsonStr.length()-2) + "\n";
   jsonStr += "  ] }";  
   return p.makeJsonReply(200, jsonStr);
 }
}
}
