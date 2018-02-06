import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/** A Java API for GET and POST. This is the parent-most class
  @author magnus cardell */
public class JavaAPI {

  //class variables

  /** limit on buffer capacity when reading in from socket */
  static final int maxinBuff = 1000; 

  /** main method
    @param args String array to hold  url and port number*/
  public static void main(String[] args) throws InterruptedException {
    int port = Integer.parseInt(args[0]);


    try {
      //Set up the server
      System.out.println("Initializing API communication... ");
      ServerSocket servSock = new ServerSocket(port);
      System.out.println("Waiting for an incoming request... ");
      Socket inSock = null;

      // Multithreaded accept loop
      while(true){
        inSock = servSock.accept();
        System.out.println(inSock.getRemoteSocketAddress().toString() + " connected");
        Thread t = new Thread(new Worker(inSock));
        t.start();
      }
    } catch (IOException e) {
      System.err.println("IOException caught in backend: " + e.getMessage());
    } catch (Exception e) {
      System.err.println("Port already in use. Try again!");
      System.exit(1);
    }
    
  }
}

