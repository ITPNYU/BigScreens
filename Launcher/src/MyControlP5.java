
import processing.core.PApplet;
import controlP5.ControlP5;
import controlP5.Tab;
import controlP5.Textfield;

/**
 * Extending ControlP5 in order to call MyTextfield
 *
 */
public class MyControlP5 extends ControlP5 {
	
	public MyControlP5(PApplet theParent) {
		super(theParent);
		// TODO Auto-generated constructor stub
	}
		
	public Textfield addTextfield(final Object theObject, final String theIndex, final String theName, final int theX, final int theY, final int theW, final int theH) {
		Textfield myController = new MyTextfield(cp5, (Tab) cp5.controlWindow.getTabs().get(1), theName, "", theX, theY, theW, theH);
		cp5.register(theObject, theIndex, myController);
		myController.registerProperty("text");
		return myController;
	}

}
