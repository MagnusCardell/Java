/* REST server implemented from scratch.
  Opens a socket connection with a parallel thread reading command-line strings. 
  Sends and recieves messages embedded in REST headers. Parsed and compiled by HttpParser.html.
  POST - saves a string in an ID corresponding to the React- server database through File IO.
  GET - gets the contents of a file corresponding to the unique ID corresponding to the react-server database
   Requires one command line arg:  
   1.  port number to use (on this machine). */

   import java.io.*;
   import java.net.*;
   import java.util.*;
   import java.nio.charset.*;
   //import java.imageio.*;

   public class JavaAPIServer {
   	private static String DEBUG = null;
   	static final int MAXBUFF = 100000000; //100million  
   	static final int MEDIUMBUFF = 10000000; //10 million
	static boolean contin = true; // continue with main loop?
	static ServerSocket servSock = null;

	public static void main(String[] args) throws IOException{
		int port = -1;
		final String prefix = new String("RestServer: ");
		// define API handlers

		RestModel model = new RestModel();
		if (DEBUG != null) {
			System.out.println(DEBUG + "model handler keys: " + model.getHandlerKeys());
			System.out.println(DEBUG + "model handler values: " + model.getHandlerValues());
		}

		// define and start a thread to process commands from standard input,
		// including EXIT to shut down this server
		Thread commandThread = getCommandThread(prefix + "CMD: ");
		commandThread.start();
		// perform server initialization

		try {
			port = Integer.parseInt(args[0]);
			System.out.println("Initializing for network communication... ");
			servSock = new ServerSocket(port);
			/* assert:  ServerSocket successfully created */
		} catch (IOException e) {
			System.err.println(prefix + "init failure.");
			System.err.println(e.getMessage());
			System.exit(1);  // an error exit status
			return;
		}
		// successful initialization
		/* MAIN LOOP:  repeatedly receive new HTTP requests and reply to them,
			using a fresh Socket connection per request
			NOTE:  In this simple demo, requests are handled in main thread, 
			NOT a separate Worker thread. */
		int failcount = 0;  // number of consec failures with a client
		while (contin) {
			InputStream inStream = null;
			OutputStream outStream = null;
			Socket inSock = null;
			try {
				System.out.println("========================================\n" + 
					"Waiting for an incoming connection... ");
				inSock = servSock.accept();

				inStream = inSock.getInputStream();
				outStream = inSock.getOutputStream();

				byte[] inBuff = new byte[MAXBUFF];
				int count;  // to hold number of bytes of I/O
				count = inStream.read(inBuff);  
				// successful read from socket 
				 
				System.out.println("Successfully received the following " 
					+ count + " bytes:");
				System.out.write(inBuff, 0, count);

				HttpParser parser = new HttpParser(inBuff, 0, count);  
				int code = parser.parseRequest();

				//String id = parser.getURLBase();
				//Hashtable<String,String> text= parser.getParams();

				//System.out.println(id);
				//System.out.println(text.get("text"));
				  
				if (DEBUG != null) 
					dumpParseResults(parser);	
				 
				String reply;
				if (code != 200)
					reply = parser.makeReply(code);
				else {
				 	reply = model.handle(parser);
				}
				  
				if (DEBUG != null) 
					System.out.println(DEBUG + "Sending HTTP reply:\n" + reply + 
						"\n" + DEBUG + "End of reply");
				  
				outStream.write(reply.getBytes());
				System.out.println("HTTP reply message sent");
			} catch (IOException e) {
				System.out.println(prefix + "client interaction failed.");
				System.out.println(e.getMessage());
			} finally {
				inSock.close();
			}
		}
	}
	public static Thread getCommandThread(String prefix) {
		return new Thread(new Runnable () {
			public void run() {
				System.out.println(prefix + "starting command thread");
				System.out.flush();
				byte [] buff = new byte[100];
				int count;
				try {
					while ((count = System.in.read(buff)) >= 0) {
						String inLine = new String(buff, 0, count);
						if (inLine.trim().equals("EXIT")) {
							contin = false; 
							servSock.close();
							System.out.println(prefix + "command thread returning");
							return;
						} else {
							System.out.println(prefix + "Unknown standard-input command."+ 
								"Enter EXIT to quit");
						}
					}
				} catch (IOException e) {
					System.err.println(prefix+"error reading standard input, aborting"
						+ e.getMessage());
					contin = false;
					return;
				}
			}
		});

	}


	private static void dumpParseResults(HttpParser parser) {
		System.out.println(DEBUG + "getRequestURL() --> " + parser.getRequestURL());
		System.out.println(DEBUG + "getURLBase() --> " + parser.getURLBase());
		System.out.println(DEBUG + "getURLId() --> " + parser.getURLId());

		System.out.println(DEBUG + "=== Printing headers ===");
		for (Enumeration<String> e = parser.getHeaders().keys(); 
			e.hasMoreElements(); ) {
			String next = e.nextElement();
		System.out.println(DEBUG + next + ": " + parser.getHeader(next));
	}
	System.out.println(DEBUG + "=== End headers ===");

	System.out.println(DEBUG + "=== Printing parameters ===");
	for (Enumeration<String> e = parser.getParams().keys(); 
		e.hasMoreElements(); ) {
		String next = e.nextElement();
	System.out.println(DEBUG + next + "=" + parser.getParam(next));
}
System.out.println(DEBUG + "=== End parameters ===");
}

}
