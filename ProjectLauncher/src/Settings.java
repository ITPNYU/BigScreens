import processing.core.*;
import processing.data.XML;


public class Settings {
	PApplet parent;
	XML settings;
	String xml = "../data/settings.xml";
	
	Location loc = Location.IAC;
	Ide ide = Ide.ECLIPSE;
	String path = "";
	String project = "";
	String user = "";
	boolean[] screens;
	
	int numScreens;

	
	public Settings(PApplet p) {
		parent = p;
		settings = parent.loadXML(xml);
		loc = Location.valueOf(settings.getChild("location").getContent().toUpperCase());
		ide = Ide.valueOf(settings.getChild("ide").getContent().toUpperCase());
		path = settings.getChild("path").getContent();
		project = settings.getChild("project").getContent();
		user = settings.getChild("user").getContent();
		numScreens = settings.getChild("numScreens").getIntContent();
		
		PApplet.println("SETTINGS");
		PApplet.println("Location: " + loc);
		PApplet.println("IDE: " + ide);
		PApplet.println("User: " + user);
		PApplet.println("Wait for: " + numScreens + " screens");

		XML[] scrns  = settings.getChild("screens").getChildren("screen");
		screens = new boolean[scrns.length];
		for(int i = 0; i < scrns.length; i++) {
			screens[i] = Boolean.parseBoolean(scrns[i].getContent());
			String status = screens[i] ? "on" : "off";
			PApplet.println("Screen " + i + " is " + status);
		}

	}
		
	public void setLocation(Location value) {
		loc = value;
		settings.getChild("location").setContent(value.toString());
		PApplet.println(settings.getChild("location").getContent());
		save();
	}

	public void setIde(Ide value) {
		ide = value;
		settings.getChild("ide").setContent(value.toString());
		save();
	}

	public void setPath(String value) {
		path = value;
		settings.getChild("path").setContent(value);
		save();
	}
	
	public void setProject(String value) {
		project = value;
		settings.getChild("project").setContent(value);
		save();
	}
	public void setUser(String value) {
		user = value;
		settings.getChild("user").setContent(value);
		save();
	}	
	public void setScreen(int index, boolean value) {
		screens[index] = value;
		XML thisScreen = settings.getChild("screens").getChildren("screen")[index];
		thisScreen.setContent(Boolean.toString(value));
		save();
	}
	
	public void setNumScreens(int value) {
		numScreens = value;
		settings.getChild("numScreens").setIntContent(value);
		save();
	}
	private void save() {
		parent.saveXML(settings, path);
	}

}