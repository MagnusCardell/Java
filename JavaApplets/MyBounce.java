import java.applet.*;
import java.awt.*;
import javax.swing.*;

/* Demo of threads:  an Applet that creates a bouncing circle, using a
   timer thread to update the animation
   RAB 1/99, inspired by Flanagan, "Java Examples in a Nutshell", 1997

   Main idea:  We create an Applet Bounce whose paint method draws a red
   circle at (x,y) and that has a method doStep() for moving the circle
   by (dx,dy), except reversing direction when the circle reaches a Panel
   boundary.  We also create a separate class TimerThread that calls
   Bounce.doStep() every N milliseconds.
*/

public class MyBounce extends JApplet implements SteppingProcess {
  //int x = 150, y = 50, r = 50;  // position and radius of circle
  //int dx = 11, dy = 7;  // physical direction vector components
  Image image; 
  TimerThread timer = null;
  Ball ball_obj_r = null;
  Ball ball_obj_g = null;

  public class Ball extends MyBounce {
    Color c=null;
    int r=0, x=0, y=0;
    int dx=0, dy=0;

    public Ball(Color color, int radius, int x_pos, int y_pos, int dx_t, int dy_t){
      c = color;
      r = radius;
      x = x_pos;
      y = y_pos;
      dx = dx_t;
      dy = dy_t;
    }

    private void draw(Graphics g){

    }
    private void move(){
      if ((x - r + dx < 0) || (x + r + dx > 500)) 
        dx = -dx;
      if ((y - r + dy < 0) || (y + r + dy > 300)) 
        dy = -dy;
      x += dx;  y += dy;
    }
  }

  public void init() {
    timer = new TimerThread(this, 100);
    ball_obj_r = new Ball(Color.red,50,50,150,11,7);
    ball_obj_g = new Ball(Color.green,50,50,150,11,-7);

  }

  public void paint(Graphics g) {
    if (image == null) {
      // we initialize here to avoid ordering problems with Frame init...
      image = createImage(getSize().width, getSize().height);
    }
    // Image initialized

    Graphics imageG = image.getGraphics();
    imageG.setColor(getBackground());
    imageG.fillRect(0, 0, getSize().width, getSize().height);
    imageG.setColor(ball_obj_r.c);
    imageG.fillOval(ball_obj_r.x-ball_obj_r.r, ball_obj_r.y-ball_obj_r.r, ball_obj_r.r*2, ball_obj_r.r*2);
    imageG.setColor(ball_obj_g.c);
    imageG.fillOval(ball_obj_g.x-ball_obj_g.r, ball_obj_g.y-ball_obj_g.r, ball_obj_g.r*2, ball_obj_g.r*2);
    
    g.drawImage(image, 0, 0, this);

  }

  public void doStep() {
    ball_obj_r.move();
    ball_obj_g.move();
    repaint();
  }

  public void start() { timer.startStepping(); }  // override JApplet.start()

  public void stop() { timer.suspendStepping(); } // override JApplet.stop()
}


/* SteppingProcess allows Bounce to interact with TimerThread */

interface SteppingProcess {
  public void doStep();
}


/* TimerThread is a separate thread of execution that does nothing but 
   call proc.doStep() periodically */

class TimerThread extends Thread {
  SteppingProcess proc;  // process being timed
  int delay;  // wait in milliseconds between steps
  private boolean amSuspended = true;
    // enables/disables the periodic calling of proc.doStep()

  public TimerThread(SteppingProcess p, int d) {
    proc = p;  delay = d;
  }

  public synchronized void startStepping() {
    amSuspended = false;
    if (!isAlive())
      // assert:  run() has not yet been started on this thread
      start();
    else
      notify();
  }

  public synchronized void suspendStepping() {
    amSuspended = true;
  }

  public void run() {
    for (;;) {
      proc.doStep();
      try {
  Thread.sleep(delay);

  synchronized (this) {
    while (amSuspended)
      wait();
  }
      } catch (InterruptedException e) {}
    }
  }
}
