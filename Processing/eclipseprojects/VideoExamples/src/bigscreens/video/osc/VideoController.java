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
import netP5.*;



public class VideoController extends PApplet {

	//--------------------------------------
	static public void main(String args[]) {
		PApplet.main(new String[] {"bigscreens.video.osc.VideoController" });
	}

	OscP5 oscP5;
	NetAddress location;

	//--------------------------------------
	public void setup() {
		size(400,300);
		
		// In theory we could also listen on port 12001 but no need to in this scenario
		oscP5 = new OscP5(this,12001);

		// Where we are sending out message
		location = new NetAddress("127.0.0.1",12000);

	}

	public void movieEvent(Movie m) {
		m.read();
	}

	public void draw() {
		background(0);
		textAlign(CENTER);
		fill(255);
		textSize(24);
		text("Hit space bar to switch videos.",width/2,height/2+10);
	}

	public void keyPressed() {
	  if (key == ' ') {
		  OscMessage msg = new OscMessage("/movie");
		  msg.add(0);
		  oscP5.send(msg, location); 
	  }
	}
	
	

}
