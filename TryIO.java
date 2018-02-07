/* example file TryIO.java -- RAB 1/99 
modified -- magnuscardell 1/18 */

import java.io.*;

/* perform miscellaneous tests of input/output, String class, etc. */
public class TryIO {
  static final int maxline = 100;

  public static void main(String[] args) {
    int i;  /* loop control */

    /************************************************************
     *  1. First, print the command line arguments in order     *
     ************************************************************/
    System.out.println("The command line arguments are:");
    for (i = 0;  i < args.length;  i++)
      System.out.println(args[i]);
    

    /************************************************************
     *  2. Next, read and write a string                        *
     ************************************************************/
    byte[] strline = new byte[maxline];
    int strlen;  

    System.out.println("Enter one line of input (up to " + 
		       maxline + " bytes):");
    try { 
      strlen = System.in.read(strline, 0, maxline); 
    }
    catch (IOException ioe) { 
      System.out.println("IOException caught"); 
      strlen = -2; 
    }

    System.out.println(strlen + " bytes were read, namely:");
    System.out.println(new String(strline, 0, strlen)); // INCLUDES newline


    /************************************************************
     *  3. Now, test integer input                              *
     ************************************************************/
    byte[] numline = new byte[maxline];
    int numlen;
    System.out.print("Enter a number: ");
    try { 
      numlen = System.in.read(numline, 0, maxline); 
    }
    catch (IOException e) { 
      System.out.println("IOException caught -- some problem with input"); 
      System.out.println(e.getMessage());
      numlen = 0;
    }
    /* assert:  successful input of byte array numline */

    int val = 0;  /* to hold value of input integer read into numline */
    try {
      val = Integer.parseInt(new String(numline, 0, numlen-1));
      System.out.println("The square of your number is " + val*val + ".");
    }
    catch (NumberFormatException e) {
      System.out.println("NumberFormatException caught: " + e.getMessage());
      System.out.println("Default value of " + val + " will be used.");
    }
    /* assert:  successful conversion of numline into integer val */
    
    /************************************************************
     *  4. Finally, print the first val substrings of str       *
     ************************************************************/
    String str = new String(strline, 0, strlen-1);  // EXCLUDES newline
    System.out.println("Here are the first " + val + 
		       " \"tails\" of the " + str.length() +
		       "-character string \"" + str + "\"");
    for (i = 0;  i < val && i < str.length();  i++) {
      System.out.println(str.substring(i));
    }
    if (i < val) {
      System.out.println("(followed by " + (val-i) + 
			 ((val == i+1) ? " repetition " : " repetitions ") +
			 "of an empty string)");
    }
  }
}

  



