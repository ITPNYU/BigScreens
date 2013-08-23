
import processing.core.*;
import controlP5.*;


@SuppressWarnings("serial")
public class ProjectLauncher extends PApplet  {

	
	Command command = Command.LAUNCH;
	Settings settings;
	//String user = "bigscreens";
	String user = "hamstar";
	String password;
	
	String[] hosts, iac, itp;
	
	ControlP5 cp5;
	DropdownList locDDL, ideDDL;
	Textfield pathTF;
	
	int margin = 50;
	PFont font = createFont("arial", 16);	
	
	public void setup() {
	  size(640,480);
	  smooth();
	  noStroke();
	  frameRate(30);
	  
	  settings = new Settings(this);

	  iac = loadStrings("../data/iac.txt");
	  itp = loadStrings("../data/itp.txt");
	  
	  hosts = iac;
	  
	  PFont font = createFont("arial",16);
	  cp5 = new MyControlP5(this);	  
	  
	  int tfWidth = 500;
	  
	  // Add text fields
	  pathTF = cp5.addTextfield("setPath").setPosition(margin,100).setSize(tfWidth,40).setFont(font).setAutoClear(false)
	  	.setValue(settings.path)
	  	.setCaptionLabel("Path to project. Only for XCODE projects.")
	  	.setVisible(settings.ide == Ide.XCODE);

	  boolean isProjectEmpty = settings.project == "";
	  cp5.addTextfield("setProject").setPosition(margin,160).setSize(tfWidth,40).setFont(font).setAutoClear(false).setFocus(isProjectEmpty)
	  	.setValue(settings.project)
	  	.setCaptionLabel("Project Name");
	  cp5.addTextfield("setPassword").setPosition(margin,250).setFont(font).setSize(tfWidth/2,40).setAutoClear(false).setFocus(!isProjectEmpty)
	  	.setCaptionLabel("Password");

	  	  
	  // create a DropdownList
	  locDDL = cp5.addDropdownList("location")
			  .setCaptionLabel("Location")
			  .setPosition(margin, 50);
	  initDDL( Location.values(), locDDL, "Location"); // customize the first list
	  locDDL.setIndex((int) Location.valueOf(settings.loc.toString()).ordinal());	  
	  
	  // create a second DropdownList
	  ideDDL = cp5.addDropdownList("ide")
			  .setCaptionLabel("Ide")
	          .setPosition(margin + 150, 50)
	          .setSize(50,200);
	  initDDL(Ide.values(), ideDDL, "IDE"); // customize the second list
	  ideDDL.setIndex((int) Ide.valueOf(settings.ide.toString()).ordinal());
	  textAlign(CENTER, CENTER);
	  
	  // Create checkboxes for each screen
	  for(int i = 0; i < settings.screens.length; i++) {
		// create a toggle
		  cp5.addToggle("Screen: " + i)
		     .setPosition(margin*(i+1), height-150)
		     .setColorActive(color(0, 200, 0))
		     .setColorBackground(color(225, 0, 0))
		     .setSize(20,20)
		     .setState(settings.screens[i])
		     .setCaptionLabel("Screen " + i)
		     ;
	  }
	  
	  // Create new buttons with name Launching and Quitting projects and server
	  cp5.addButton("launch")
	     .setValue(0)
	     .setPosition(margin,height-100)
	     .setSize(36, 36);
	  cp5.addButton("quit")
	     .setValue(0)
	     .setPosition(margin + 40,height-100)
	     .setSize(24, 36);
	  cp5.addButton("lsvr")
	     .setValue(0)
	     .setPosition(margin*9,height-100)
	     .setSize(69, 36)
	     .setCaptionLabel("Launch Server");
	  cp5.addButton("qsvr")
	     .setValue(0)
	     .setPosition(margin*10.5f,height-100)
	     .setSize(57, 36)
	     .setCaptionLabel("Quit Server");
	  
	  cp5.addTextfield("setNumScreens").setPosition(margin*9,height-170).setFont(font).setSize(50,40).setAutoClear(false)
	  	.setCaptionLabel("# screens to wait for: 0 to " + settings.screens.length);

	  
	}
	
	public void draw() {
	  background(155);
	}
	
	void setVisibility(Controller<?> controller, boolean isVisible) {
		controller.setVisible(isVisible);
	}
	
	void initDDL(Object[] enumValues, DropdownList ddl, String label) {
		
		for(int i = 0; i < enumValues.length; i++) {			
			  ddl.addItem(enumValues[i].toString(), i);
		  }	
		  // a convenience function to customize a DropdownList
		  ddl.setBackgroundColor(color(225));
		  ddl.setItemHeight(15);
		  ddl.setBarHeight(30);
		  ddl.setSize(100, 60);
		  ddl.getCaptionLabel()
		  	.setFont(font)
		  	.getStyle().setMargin(5, 0, 0, 3);
		  ddl.getValueLabel()
		  	.setFont(font)
		  	.setPaddingY(30);
		  //ddl.scroll(0);
		  ddl.setColorBackground(color(128));
		  ddl.setColorActive(color(255, 128));
		}
	
	public void controlEvent(ControlEvent theEvent) {
		  // DropdownList is of type ControlGroup.
		  // A controlEvent will be triggered from inside the ControlGroup class.
		  // therefore you need to check the originator of the Event with
		  // if (theEvent.isGroup())
		  // to avoid an error message thrown by controlP5.

		  if (theEvent.isGroup()) {
		    // check if the Event was triggered from a ControlGroup
		    println("Event from Dropdown : "+theEvent.getGroup().getValue()+" from "+theEvent.getGroup());
		    String whichDDL = theEvent.getGroup().toString();
		    int index = (int)theEvent.getGroup().getValue();
		    if(whichDDL.contains("location"))
		    	setLocation(index);
		    else if(whichDDL.contains("ide"))
		    	setIde(index);		   
		  } 
		  else if (theEvent.isController()) {
		    println("event from controller : "+theEvent.getController().getValue()+" from "+theEvent.getController());
		    // Cheap solution for setting screen values
		    Controller<?> theController = theEvent.getController();
		    if(theController instanceof Toggle) {
		    	String label = theController.getCaptionLabel().getText();
		    	println(label);
		    	boolean isScreenToggle = label.matches("^(Screen).*");
		    	if(isScreenToggle) {
		    		int index = Integer.parseInt(label.split("\\s")[1]);
			    	setScreen(index, (Toggle) theEvent.getController());
		    	}
		    }
		  }
		}
	
	void setLocation(int index) {
		settings.setLocation(Location.values()[index]);
	}
	
	void setIde(int index) {
		settings.setIde(Ide.values()[index]);
		// Only show textfield if IDE is xcode
		setVisibility(pathTF, settings.ide == Ide.XCODE);
	}
	
	void setPath(String theText) {
		println("Project Path: " + theText);
		// Clean the path
		theText.trim();
		theText.replace("/", ":");
		settings.setPath(theText);
	}
	
	void setProject(String theText) {
		println("Project Name: " + theText);
		theText.trim();
		settings.setProject(theText);		
	}
	void setPassword(String theText) {
		println("Password: " + theText);
		theText.trim();
		password = theText;
	}
	
	void setScreen(int index, Toggle theToggle) {
    	boolean isOnOrOff = theToggle.getValue() == 1;
    	settings.setScreen(index, isOnOrOff);
	}
	
	
	void setNumScreens(String theNumber) {
		println("Wait for " + theNumber + " screens.");
		settings.setNumScreens(Integer.parseInt(theNumber));
	}
	
	void launch() {
		println("Launching " + settings.project);
		command = Command.LAUNCH;
		
		if(frameCount > 10) {
			String commands = "osascript launcher.scpt " + command.toString() + " " + settings.ide + " " + settings.project;
			String [] args = { "hamstar", "10.10.10.199", password, commands };
			Exec.execute(args);
		}	
		//execute();
	}
	
	void quit() {
		println("Quitting " + settings.project);
		command = Command.QUIT;
		//execute();
	}
	
	void lsrvr() {
		println("Launching server");
		command = Command.LSRVR;
		//execute();
	}
	
	void qsrvr() {
		println("Quitting server");
		command = Command.QSRVR;
		//execute();
	}
	void execute() {
		
		// ssh to each computer and run applescript to launch/quit project
		// only send to specified "screens"
		// if quitting, only need to send quit command
		if(command == Command.LAUNCH || command == Command.QUIT) {
			for(int i = 0; i < settings.screens.length; i++) {
				if(settings.screens[i]) {
					String commands = "osascript launcher.scpt " + command.toString() + (command == Command.QUIT ? " " + settings.ide + " " + settings.project + " " + settings.path : "");
					String [] args = { user, hosts[i], password, commands }; 
					Exec.execute(args);
				}
			}
		}
		// or launch/quit server
		// only send server commands to middle computer
		else { 
			String commands = command == Command.LSRVR ? "cd ~/; java -jar mpeServer.jar -verbose -screens" + settings.numScreens : "exit";
			String [] args = { user, hosts[1], password, commands }; 
			Exec.execute(args);
			}
	}
}	