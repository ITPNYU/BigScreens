import processing.event.KeyEvent;
import controlP5.ControlP5;
import controlP5.ControllerGroup;
import controlP5.Textfield;

/**
 * Extending ControlP5's Textfield in order to submit on every keystroke
 *
 */
public class MyTextfield extends Textfield{

	public MyTextfield(ControlP5 theControlP5, ControllerGroup<?> theParent,
			String theName, String theDefaultValue, int theX, int theY,
			int theWidth, int theHeight) {
		super(theControlP5, theParent, theName, theDefaultValue, theX, theY, theWidth,
				theHeight);
		// TODO Auto-generated constructor stub
	}

	public void keyEvent(KeyEvent theKeyEvent) {
        if (isUserInteraction && isTexfieldActive && isActive && theKeyEvent.getAction() == KeyEvent.PRESS) {
                if (ignorelist.contains(cp5.getKeyCode())) {
                        return;
                }
                if (keyMapping.containsKey(cp5.getKeyCode())) {
                	keyMapping.get(cp5.getKeyCode()).execute();
                } else {
                	keyMapping.get(DEFAULT).execute();
                	submit();
                }
        }            
	}
}
