// This example is from the book "Java in a Nutshell, Second Edition".
// Written by David Flanagan.  Copyright (c) 1997 O'Reilly & Associates.
// You may distribute this source code for non-commercial purposes only.
// You may study, modify, and use this example for any purpose, as long as
// this notice is retained.  Note that this example is provided "as is",
// WITHOUT WARRANTY of any kind either expressed or implied.

/* Modified by R. Brown 1/6/98:  
 Add a button for clearing screen;  added a Choice menu for selecting 
 drawing color, together with color initialization/management code */

/* Modified by M. Cardell 1/7/18:  
Added features according to lab instructions */

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Background extends Scribble0
implements MouseListener,  MouseMotionListener, ActionListener {
protected Container cPane;
private int lastX, lastY;
JButton clearButton, bgButton;
JComboBox<String> colorChoices;
String[] colorStrings =
{"black", "blue", "red", "yellow", "green", "orange", "white", "pink"};
Color[] colorVec =
{Color.black, Color.blue, Color.red, Color.yellow, Color.green, Color.orange, Color.white, Color.pink};
private int colorIndex = 0; // index of the current color.

class MyItemAdapter implements ItemListener{
// The method from the ItemListener interface.  Invoked when the 
// user selects from the colorChoices menu
  public void itemStateChanged(ItemEvent e) { // change the color
    colorIndex = colorChoices.getSelectedIndex();    
  }    

}

class MyButtonAdapter implements ActionListener{

  public void actionPerformed(ActionEvent e) {

    cPane.setBackground(colorVec[colorIndex]);
  }
}
public void init() {
  cPane = this.getContentPane();
  cPane.setLayout(new FlowLayout(FlowLayout.LEADING));
  cPane.setBackground(Color.white);

  // Tell this applet what MouseListener and MouseMotionListener
  // objects to notify when mouse and mouse motion events occur.
  // Since we implement the interfaces ourself, our own methods are called.
  cPane.addMouseListener(this);
  cPane.addMouseMotionListener(this);

  clearButton = new JButton("Clear");
  clearButton.addActionListener(this);
  clearButton.setForeground(Color.black);
  clearButton.setBackground(Color.lightGray);
  cPane.add(clearButton);

  colorChoices = new JComboBox<String>(colorStrings);
  colorChoices.addItemListener(new MyItemAdapter());
  colorChoices.setForeground(Color.black);
  colorChoices.setBackground(Color.lightGray);
  //    for (int i = 0;  i < colorStrings.length;  i++)
  //      colorChoices.addItem(colorStrings[i]);
  cPane.add(new JLabel(" Color:"));
  cPane.add(colorChoices);

  bgButton = new JButton("Set BG");
  bgButton.addActionListener(new MyButtonAdapter());
  bgButton.setForeground(Color.black);
  bgButton.setBackground(Color.lightGray);
  cPane.add(bgButton);
}

// A method from the MouseListener interface.  Invoked when the
// user presses a mouse button.
public void mousePressed(MouseEvent e) {
  lastX = e.getX();
  lastY = e.getY();
}

// A method from the MouseMotionListener interface.  Invoked when the
// user drags the mouse with a button pressed.
public void mouseDragged(MouseEvent e) {
  Graphics g = cPane.getGraphics();
  int x = e.getX(), y = e.getY();
  g.setColor(colorVec[colorIndex]);
  g.drawLine(lastX, lastY, x, y);
  lastX = x; lastY = y;
}

// The method from the ActionListener interface.  Invoked when the 
// user presses the clear button
public void actionPerformed(ActionEvent e) { // clear the scribble
    // Repaint to display the buttons, uses the already set background color
  cPane.repaint();
}

// The other, unused methods of the MouseListener interface.
public void mouseReleased(MouseEvent e) {;}
public void mouseClicked(MouseEvent e) {;}
public void mouseEntered(MouseEvent e) {;}
public void mouseExited(MouseEvent e) {;}

// The other method of the MouseMotionListener interface.
public void mouseMoved(MouseEvent e) {;}
}
