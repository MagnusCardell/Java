/* Example of network communication:  Receiver.java -- RAB 1/99 
   Requires one command line arg:  
     1.  port number to use (on this machine). 

  Modified by Magnus Cardell 1/18 */

import java.io.*;
import java.net.*;

public class Receiver {
  static final int maxinBuff = 1000;  

  public static void main(String[] args) {
    try {
      int port = Integer.parseInt(args[0]);
      System.out.println("Initializing for network communication... ");
      ServerSocket servSock = new ServerSocket(port);
      /* assert:  ServerSocket successfully created */

      System.out.println("Waiting for an incoming connection... ");
      Socket inSock = servSock.accept();
      InputStream inStream = inSock.getInputStream();
      /* assert:  input socket and stream initialized */
      
      byte[] inBuff = new byte[maxinBuff];
      int count;  // to hold number of bytes read
      count = inStream.read(inBuff);  
      // may have to do repeated reads to get it all in some contexts...
      /* successful read from socket */
    
      System.out.println("Successfully received the following " + count
			 + " bytes:");
      System.out.write(inBuff, 0, count);
      
      inSock.close();
    }
    catch (IOException e) {
      System.err.println("Receiver failed.");
      System.err.println(e.getMessage());
      System.exit(1);  // an error exit status
      return;
    }
    
  }
}

