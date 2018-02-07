import java.applet.*;
import java.awt.*;
import javax.swing.*;
public class FirstApplet extends JApplet {
	/** paint:  invoked by applet viewer to draw this applet.
	@param g  Graphics object for performing drawing operation.
	@return   None.  */
	public void paint(Graphics g) {
		g.drawString("Hello, world!", 25, 50);
	}
}