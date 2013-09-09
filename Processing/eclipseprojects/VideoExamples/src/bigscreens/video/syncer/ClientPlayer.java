/**
 * Simple Template for the Big Screens Class, Fall 2013
 * <https://github.com/ITPNYU/BigScreens>
 * 
 * Note this project uses Processing 2.0.1
 */



package bigscreens.video.syncer;

import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.*;
import processing.video.Movie;

public class ClientPlayer extends PApplet {

	//--------------------------------------
	static public void main(String args[]) {
		// Windowed
		PApplet.main(new String[] {bigscreens.video.syncer.ClientPlayer.class.getName() });
		// FullScreen Exclusive Mode
		//PApplet.main(new String[] {"--present", bigscreens.video.syncer.MasterPlayer.class.getName() });
		}

	Movie m;
	OscP5 oscP5;

	//--------------------------------------
	public void setup() {
		size(320,240);
		m = new Movie(this,"fingers.mov");
		m.loop();

		oscP5 = new OscP5(this,12345);

	}

	public void movieEvent(Movie m) {
		m.read();
	}

	public void draw() {
		background(255);
		image(m,0,0,width,height);
	}

	public void oscEvent(OscMessage msg) {
		//println("Here comes a message: ");
		//println("Address pattern: " + msg.addrPattern());
		float t = msg.get(0).floatValue(); 
		//System.out.println("Jumping to " + t);
		m.jump(t);
	}

}
