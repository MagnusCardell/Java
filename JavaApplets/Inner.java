// This example is from the book "Java in a Nutshell, Second Edition".
// Written by David Flanagan.  Copyright (c) 1997 O'Reilly & Associates.
// You may distribute this source code for non-commercial purposes only.
// You may study, modify, and use this example for any purpose, as long as
// this notice is retained.  Note that this example is provided "as is",
// WITHOUT WARRANTY of any kind either expressed or implied.

/* Modified by R. Brown 1/8/99:  name change, doc tweak 
   Use an "adapter" class to implement Mouse interface 
   R. Brown 1/6/00:  Implement MouseMotion listener with adapter class, too */
   

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Inner extends JApplet {
  int lastX, lastY;

  public void init() {
    this.addMouseListener(new MyMouseAdapter());
    this.addMouseMotionListener(new MyMouseMotionAdapter());
  }

  /*********************************************************************
     MyMouseAdapter is an inner class (i.e., local to Inner) that 
     implements the interface MouseListener for mouse actions.  
     This is more flexible than having Inner implement MouseListener
     itself for large applications, because we can use a separate adapter
     object (and hence separate callback method) for each Event source,
     instead of handling multiple Event sources within a single callback. 
  **********************************************************************/

  class MyMouseAdapter implements MouseListener {
    // A method from the MouseListener interface.  Invoked when the
    // user presses a mouse button.
    public void mousePressed(MouseEvent e) {
      lastX = e.getX(); // NOTE: this is Inner.lastX, by locality
      lastY = e.getY();
    }
    
    // The other, unused methods of the MouseListener interface.
    public void mouseReleased(MouseEvent e) {;}
    public void mouseClicked(MouseEvent e) {;}
    public void mouseEntered(MouseEvent e) {;}
    public void mouseExited(MouseEvent e) {;}
  }
  
  
  /*********************************************************************
     MyMouseMotionAdapter is another inner class that implements the 
     interface MouseMotionListener for mouse motions.  
  **********************************************************************/

  class MyMouseMotionAdapter implements MouseMotionListener {
    // A method from the MouseMotionListener interface.  Invoked when the
    // user drags the mouse with a button pressed.
    public void mouseDragged(MouseEvent e) {
      Graphics g = getGraphics();
      /* NOTE:  this is Inner.getGraphics().  
	 To write "this.getGraphics() would mean the anonymous adapter's 
	 getGraphics(), which doesn't exist... */
      int x = e.getX(), y = e.getY();
      g.drawLine(lastX, lastY, x, y);
      lastX = x; lastY = y;
    }

    // The other method of the MouseMotionListener interface. (not used)
    public void mouseMoved(MouseEvent e) {;}
  }
}
