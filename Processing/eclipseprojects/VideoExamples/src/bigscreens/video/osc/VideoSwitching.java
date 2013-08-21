/**
 * Fall 2013 Big Screens Examples
 * https://github.com/ITPNYU/BigScreens
 * 
 * Note this project uses Processing 2.1
 */


package bigscreens.video.osc;

import processing.core.*;
import processing.video.Movie;

import oscP5.*;



public class VideoSwitching extends PApplet {

	//--------------------------------------
	static public void main(String args[]) {
		// Windowed
		PApplet.main(new String[] {"bigscreens.video.osc.VideoSwitching" });
		// FullScreen Exclusive Mode
		//PApplet.main(new String[] {"--present", "bigscreens.video.VideoSwitching" });
	}

	Movie m1;
	Movie m2;

	OscP5 oscP5;
	
	boolean switcher = true;

	//--------------------------------------
	public void setup() {
		size(600,400);
		m1 = new Movie(this,"fingers.mov");
		m1.loop();
		m2 = new Movie(this,"station.mov");
		m2.loop();

		oscP5 = new OscP5(this,12000);

	}

	public void movieEvent(Movie m) {
		m.read();
	}

	public void draw() {
		background(255);
		if (switcher) 
			image(m1,0,0,width,height);
		else
			image(m2,0,0,width,height);
	}

	public void oscEvent(OscMessage msg) {
		//println("Here comes a message: ");
		//println("Address pattern: " + msg.addrPattern());
		int val = msg.get(0).intValue(); 
		if (val == 0) switcher = !switcher;
	}


}
