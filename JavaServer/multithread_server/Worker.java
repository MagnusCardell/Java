import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;

import java.io.DataInputStream;


/** A class for starting every new socket connection with a new thread
@author magnus cardell */
public class Worker implements Runnable{
	//class variables

	/** holds the accepted conection*/
	private Socket sock;

	/** constructor with one socket variable
	@param s Socket with accepted connection */
	public Worker(Socket s) {
		this.sock = s;
	}

	// methods

	/** Run thread with initialized socket */
	public void run(){

		try {
			System.out.println("New Thread:" + Thread.currentThread().getName());
			clientSession();

			System.out.println("Closing socket");
			sock.close();

		} catch(IOException e){
			System.err.println("IOException in Worker.java(): " + e.getMessage());

		} catch(InterruptedException e) {
			System.err.println("InterruptedExcetion in Worker(): " + e);
		}
	}
	
	private void clientSession() throws IOException, InterruptedException {

		try {

			BufferedReader request = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			BufferedWriter response = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));

			String putDataFromClient = "";
			String requestHeader = "";
			String temp = ".";
			while (!temp.equals("")) {
				temp = request.readLine();
				System.out.println(temp);
				requestHeader += temp + "\n";
			}

			// Get the method from HTTP header
			StringBuilder sb = new StringBuilder();

			String method = requestHeader.split("\n")[0].split(" ")[0];
			System.out.println("method: "+ method);

			// GET get_file
			String get_file = requestHeader.split("\n")[0].split(" ")[1].split("/")[1];
			if (method.equals("GET") && checkURL(get_file)) {

				System.out.println("1 -- ");
				System.out.println("get_file: "+ get_file);
				// Get the correct page
				constructResponseHeader(200, sb);
				response.write(sb.toString());
				response.write(getData(get_file));
				sb.setLength(0);
				response.flush();

			//POST
			} else if (method.equals("POST")) {

				System.out.println("2 -- ");
				// Get the data from the inputStream
				temp = "temp";
				String file_info = "";
				temp = request.readLine();
				System.out.println(temp);
				System.out.println("Beginning read in stream...");
				while (!temp.equals("")) {
					temp = request.readLine();
					//System.out.println(temp);
					file_info += temp + "\n";
				}
				//System.out.println(file_info);

				temp = "temp";
				System.out.println("another stream session...");
				while (!temp.equals("")) {
					temp = request.readLine();
					//System.out.println(temp);
					putDataFromClient += temp;
				}
				//System.out.println(putDataFromClient);

				int responseCode;
				DataInputStream dis = new DataInputStream(sock.getInputStream());
				FileOutputStream fout = new FileOutputStream("image.jpg");
				try{
				// int bytesRead;
				// int current = 0;
				// FileOutputStream fos = null;
				// BufferedOutputStream bos = null;
				// byte [] mybytearray  = new byte [6022386];
				// InputStream is = sock.getInputStream();
				// fos = new FileOutputStream("test.jpg");
				// bos = new BufferedOutputStream(fos);
				// bytesRead = is.read(mybytearray,0,mybytearray.length);
				// current = bytesRead;

				// do {
				// 	bytesRead =
				// 	is.read(mybytearray, current, (mybytearray.length-current));
				// 	if(bytesRead >= 0) current += bytesRead;
				// } while(bytesRead > -1);

				// bos.write(mybytearray, 0 , current);
				// bos.flush();
				// System.out.println("File " + "test.jpg" + " downloaded (" + current + " bytes read)");

				// if (fos != null) fos.close();
				// if (bos != null) bos.close();
					int i;
					while ( (i = dis.read()) > -1) {
						fout.write(i);
					}

					fout.flush();
					fout.close();
					dis.close();
					responseCode = 404;
				} catch(IOException e){
					fout.flush();
					fout.close();
					dis.close();
					responseCode = 304;
				}
				
				if (putDataFromClient != "") {
					System.out.println("3 -- ");
					//int responseCode = putData(putDataFromClient, "test22");
					constructResponseHeader(responseCode, sb);
					response.write(sb.toString());
					sb.setLength(0);
					response.flush();
				} else {
					System.out.println("4 -- ");
					constructResponseHeader(304, sb);
					response.write(sb.toString());
					sb.setLength(0);
					response.flush();
				}

			} else {
				System.out.println("5 -- ");
				// Enter the error code
				// 404 page not found
				constructResponseHeader(404, sb);
				response.write(sb.toString());
				sb.setLength(0);
				response.flush();
			}
			System.out.println("6 -- ");
			request.close();
			response.close();

			sock.close();
			return;
		} catch (Exception e) {
			System.err.println("Error in clientSession(): " + e);
		}

	}

	// Check the URL from the Request header to the server's database
	private static boolean checkURL(String file) {

		File myFile = new File(file);
		System.out.println(file);
		System.out.println("IT IS CHEKCING");
		System.out.println(myFile.exists() && !myFile.isDirectory());
		return myFile.exists() && !myFile.isDirectory();

	}

	// Construct Response Header
	private static void constructResponseHeader(int responseCode,
		StringBuilder sb) {

		if (responseCode == 200) {

			sb.append("HTTP/1.1 200 OK\r\n");
			sb.append("Date:" + getTimeStamp() + "\r\n");
			sb.append("Server:localhost\r\n");
			sb.append("Content-Type: text/html\r\n");
			sb.append("Connection: Closed\r\n\r\n");

		} else if (responseCode == 404) {

			sb.append("HTTP/1.1 404 Not Found\r\n");
			sb.append("Date:" + getTimeStamp() + "\r\n");
			sb.append("Server:localhost\r\n");
			sb.append("\r\n");
		} else if (responseCode == 304) {
			sb.append("HTTP/1.1 304 Not Modified\r\n");
			sb.append("Date:" + getTimeStamp() + "\r\n");
			sb.append("Server:localhost\r\n");
			sb.append("\r\n");
		}
	}

	// PUT data to file ServerIndex.htm
	private static int putData(String putDataFromClient, String file)
	throws IOException {

		return writeTextFile(putDataFromClient, file);
	}

	private static String getData(String file) {

		File myFile = new File(file);
		String responseToClient = "";
		BufferedReader reader;

		// System.out.println(myFile.getAbsolutePath());

		try {
			reader = new BufferedReader(new FileReader(myFile));
			String line = null;
			while (!(line = reader.readLine()).contains("</html>")) {
				responseToClient += line;
			}
			responseToClient += line;
			// System.out.println(responseToClient);
			reader.close();

		} catch (Exception e) {

		}
		System.out.println(responseToClient);
		return responseToClient;
	}

	// Write the data to server - Helper method for putData method
	private static int writeTextFile(String putDataFromClient, String file) {

		File myFile = new File(file + ".jpg");
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(myFile));
			writer.write(putDataFromClient);
			writer.close();
			return 200;
		} catch (IOException e) {
			return 304;
		}
	}

	// TimeStamp
	private static String getTimeStamp() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
		String formattedDate = sdf.format(date);
		return formattedDate;
	}

}
