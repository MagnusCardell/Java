
import java.applet.*;
import java.awt.*;

/* Demo of threads:  double-buffered Bounce Applet
   RAB, inspired by Flanagan, "Java Examples in a Nutshell", 1997 
   Implemented as a subclass to highlight changes needed for double buffering
*/

public class Bounce2 extends Bounce {
  Image image;  // alternate image buffer

  // update() calls are scheduled as events, to produce the next screen
  // override to avoid default clearing of the image...
  // we will clear the image ourselves in paint()
  public void update(Graphics g) {
    paint(g); 
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
    imageG.setColor(Color.red);
    imageG.fillOval(x-r, y-r, r*2, r*2);
    
    g.drawImage(image, 0, 0, this);
  }
}
