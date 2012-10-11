/**
 * Simple Template for the Big Screens Class, Fall 2011
 * <https://github.com/shiffman/Most-Pixels-Ever>
 * 
 * Note this project uses Processing 1.5.1
 */

package bigscreens.video.syncer;

import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.*;
import processing.opengl.*;
import processing.video.Movie;

public class MasterPlayer extends PApplet {

	//--------------------------------------
	static public void main(String args[]) {
		// Windowed
		PApplet.main(new String[] {bigscreens.video.syncer.MasterPlayer.class.getName() });
		// FullScreen Exclusive Mode
		//PApplet.main(new String[] {"--present", bigscreens.video.syncer.MasterPlayer.class.getName() });
	}

	Movie m;
	OscP5 oscP5;
	NetAddress client1;
	NetAddress client2;

	//--------------------------------------
	public void setup() {
		size(320,240);
		m = new Movie(this,"fingers.mov");
		m.play();

		// In theory we could also listen on port 12001 but no need to in this scenario
		oscP5 = new OscP5(this,12001);

		// Where we are sending out message
		client1 = new NetAddress("127.0.0.1",12345);
		//client2 = new NetAddress("127.0.0.1",12346);
	}

	public void movieEvent(Movie m) {
		m.read();
	}

	public void draw() {
		background(255);
		image(m,0,0,width,height);

		if (frameCount % 30 == 0) {
			OscMessage msg = new OscMessage("/time");
			msg.add(m.time());
			oscP5.send(msg, client1); 
		}
	}

}
