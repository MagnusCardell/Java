/* Example of network communication:  Sender.java -- RAB 1/99 
   Requires two command line args:  
     1.  name of host to connect to, 
     2.  port number to use. 
  Modified by Magnus Cardell 1/18 */

import java.io.*;
import java.net.*;

public class Sender {
  static final int maxline = 100;  

  public static void main(String[] args) {
    try {
      String host = args[0];
      int port = Integer.parseInt(args[1]);

      System.out.println("Initializing for network communication... ");
      Socket outSock = new Socket(host, port);
      OutputStream outStream = outSock.getOutputStream();
      /* assert:  socket and stream initialized */
      
      System.out.println("Enter a line of input:");
      byte[] outBuff = new byte[maxline];
      int count;  // to hold number of bytes read
      count = System.in.read(outBuff);
      /* assert:  input line stored in outBuff[0..count-1] */
    
      outStream.write(outBuff, 0, count);  
      // outStream.flush();  // may be necessary in some contexts...
      /* successful write on socket */
    
      outSock.close();
    }
    catch (IOException e) {
      System.err.println("Sender failed.");
      System.err.println(e.getMessage());
      System.exit(1);  // an error exit status
      return;
    }
  }
}


