
import processing.core.*;
import controlP5.*;

@SuppressWarnings("serial")
public class ProjectLauncher extends PApplet {

	Command command = Command.LAUNCH;
	Settings settings;
	String password;

	String[] iac, itp, local;

	ControlP5 cp5;
	DropdownList locDDL, ideDDL;
	Textfield pathTF;

	int margin = 50;
	PFont font = createFont("arial", 16);

	boolean isInit = false;

	public static void main(String[] args) {
		PApplet.main(new String[] { "ProjectLauncher" });
	}

	public void setup() {
		size(640, 520);
		smooth();
		noStroke();
		frameRate(30);

		settings = new Settings(this);

		iac = loadStrings("data/iac.txt");
		itp = loadStrings("data/itp.txt");
		local = loadStrings("data/local.txt");

		PFont font = createFont("arial", 16);
		cp5 = new MyControlP5(this);

		int tfWidth = 500;

		// Add text fields
		pathTF = cp5.addTextfield("setPath").setPosition(margin, 100)
				.setSize(tfWidth, 40).setFont(font).setAutoClear(false)
				.setValue(settings.path)
				.setCaptionLabel("Path to project. Use \":\" for XCODE projects, \"/\" for APPS.");

		boolean isProjectEmpty = settings.project == "";
		cp5.addTextfield("setProject").setPosition(margin, 160)
				.setSize(tfWidth, 40).setFont(font).setAutoClear(false)
				.setFocus(isProjectEmpty).setValue(settings.project)
				.setCaptionLabel("Project Name");
		cp5.addTextfield("setUser").setPosition(margin, 250).setFont(font)
				.setSize(tfWidth / 2, 40).setAutoClear(false)
				.setFocus(!isProjectEmpty).setValue(settings.user)
				.setCaptionLabel("User");
		cp5.addTextfield("setPassword").setPosition(margin, 310).setFont(font)
				.setSize(tfWidth / 2, 40).setAutoClear(false)
				.setFocus(!isProjectEmpty).setCaptionLabel("Password");

		// create a DropdownList
		locDDL = cp5.addDropdownList("location").setCaptionLabel("Location")
				.setPosition(margin, 50);
		initDDL(Location.values(), locDDL, "Location"); // customize the first
														// list
		locDDL.setIndex((int) Location.valueOf(settings.loc.toString())
				.ordinal());

		// create a second DropdownList
		ideDDL = cp5.addDropdownList("ide").setCaptionLabel("Ide")
				.setPosition(margin + 150, 50).setSize(50, 200);
		initDDL(Ide.values(), ideDDL, "IDE"); // customize the second list
		ideDDL.setIndex((int) Ide.valueOf(settings.ide.toString()).ordinal());
		textAlign(CENTER, CENTER);

		int bottomMargin = 70;
		// Create checkboxes for each screen
		for (int i = 0; i < settings.screens.length; i++) {
			// create a toggle
			cp5.addToggle("Screen: " + i)
					.setPosition(margin * (i + 1), height - (bottomMargin+50))
					.setColorActive(color(0, 200, 0))
					.setColorBackground(color(225, 0, 0)).setSize(20, 20)
					.setState(settings.screens[i])
					.setCaptionLabel("Screen " + i);
		}
		
		//// Create new buttons with name Launching and Quitting projects and server
		cp5.addButton("launch").setValue(0).setPosition(margin, height - bottomMargin)
				.setSize(36, 36);
		cp5.addButton("quit").setValue(0)
				.setPosition(margin + 40, height - bottomMargin).setSize(24, 36);
		cp5.addButton("lsrvr").setValue(0)
				.setPosition(margin * 9, height - bottomMargin).setSize(69, 36)
				.setCaptionLabel("Launch Server");
		cp5.addButton("qsrvr").setValue(0)
				.setPosition(margin * 10.5f, height - bottomMargin).setSize(57, 36)
				.setCaptionLabel("Quit Server");
		
		//// How many screens should server wait for before starting?
		cp5.addTextfield("setNumScreens")
				.setText(String.valueOf(settings.numScreens))
				.setPosition(margin * 9, height - (bottomMargin+70))
				.setFont(font)
				.setSize(50, 40)
				.setAutoClear(false)
				.setCaptionLabel(
						"# screens to wait for: 0 to "
								+ settings.screens.length);
		
		// Throttle so that loading data into form
		// doesn't launch anything
		isInit = true;

	}

	public void draw() {
		background(155);
	}

	void setVisibility(Controller<?> controller, boolean isVisible) {
		controller.setVisible(isVisible);
	}

	void initDDL(Object[] enumValues, DropdownList ddl, String label) {

		for (int i = 0; i < enumValues.length; i++) {
			ddl.addItem(enumValues[i].toString(), i);
		}
		// a convenience function to customize a DropdownList
		ddl.setBackgroundColor(color(225));
		ddl.setItemHeight(15);
		ddl.setBarHeight(30);
		ddl.setSize(100, 60);
		ddl.getCaptionLabel().setFont(font).getStyle().setMargin(5, 0, 0, 3);
		ddl.getValueLabel().setFont(font).setPaddingY(30);
		// ddl.scroll(0);
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
			println("Event from Dropdown : " + theEvent.getGroup().getValue()
					+ " from " + theEvent.getGroup());
			String whichDDL = theEvent.getGroup().toString();
			int index = (int) theEvent.getGroup().getValue();
			if (whichDDL.contains("location"))
				setLocation(index);
			else if (whichDDL.contains("ide"))
				setIde(index);
		} else if (theEvent.isController()) {
			println("Event from controller : "
					+ theEvent.getController().getValue() + " from "
					+ theEvent.getController());
			// Cheap solution for setting values
			Controller<?> theController = theEvent.getController();
			if (theController instanceof Toggle) {
				String label = theController.getCaptionLabel().getText();
				boolean isScreenToggle = label.matches("^(Screen).*");
				if (isScreenToggle) {
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
		setVisibility(pathTF, settings.ide != Ide.ECLIPSE);
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

	void setUser(String theText) {
		println("User: " + theText);
		theText.trim();
		settings.setUser(theText);
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
		execute();
	}

	void quit() {
		println("Quitting " + settings.project);
		command = Command.QUIT;
		execute();
	}

	void lsrvr() {
		println("Launching server");
		command = Command.LSRVR;
		execute();
	}

	void qsrvr() {
		println("Quitting server");
		command = Command.QSRVR;
		execute();
	}
	
	String getHost(Location loc, int index) {
		if(loc == Location.IAC) {
			return iac[index];
		}
		else if(loc == Location.ITP) {
			return itp[index];
		}
		else {
			return local[index];
		}		
	}

	void execute() {

		if (!isInit)
			return;
				
		// ssh to each computer and run applescript to launch/quit project
		// only send to specified "screens"
		// if quitting, only need to send quit command
		if (command == Command.LAUNCH || command == Command.QUIT) {
			for (int i = 0; i < settings.screens.length; i++) {
				String host = getHost(settings.loc, i);
				String commands = "";
				String computer = "";
				
				// Only add left/middle/right to path if not testing locally
				if(settings.loc != Location.LOCAL && settings.ide == Ide.APP) {
					switch(i) {
						case 0:
							computer = "left";
							break;
		
						case 1:
							computer = "middle";
							break;
						case 2:
							computer = "right";
							break;
					}
				}
				
				computer = "/" + computer + "/";
				
				if (settings.screens[i]) {
					if (command == Command.QUIT) {
						commands = "pkill -9 -f " + settings.project;
					}
					else if(command == Command.LAUNCH && settings.ide == Ide.APP) {
						commands = "open \"" + settings.path + computer + settings.project + "\"/";
					}
					else {
						commands = "osascript ~/Dropbox/BigScreens2013/utils/launcher.scpt "
							+ command.toString()
							+ (command == Command.LAUNCH ? " " + settings.ide
									+ " " + settings.project + " "
									+ settings.path : "");
					}
				}
				String[] args = { settings.user, host, password, commands };
				for (String arg : args)
					println(arg);
				Exec.execute(args);
			}
		}
		// or launch/quit server
		// only send server commands to middle computer
		else {
			String commands = command == Command.LSRVR ? "cd ~/; java -jar mpeServer.jar -verbose " + (settings.numScreens > 0 ? "-screens"
					+ settings.numScreens : "") : "pkill -9 -f mpeServer";
			String host = getHost(settings.loc, 1);
			String[] args = { settings.user, host, password, commands };
			for (String arg : args)
				println(arg);
			Exec.execute(args);
		}
	}
}