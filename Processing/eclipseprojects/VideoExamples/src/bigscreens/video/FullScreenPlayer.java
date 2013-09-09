/**
 * Fall 2013 Big Screens Examples
 * <https://github.com/ITPNYU/BigScreens>
 * 
 * Note this project uses Processing 2.0.1
 */

package bigscreens.video;

import processing.core.*;
import processing.video.Movie;

public class FullScreenPlayer extends PApplet {

	//--------------------------------------
	static public void main(String args[]) {
		// Windowed
		// PApplet.main(new String[] {bigscreens.video.FullScreenPlayer.class.getName() });
		// FullScreen Exclusive Mode
		PApplet.main(new String[] {"--present", bigscreens.video.FullScreenPlayer.class.getName() });
	}
	
	Movie m;

	//--------------------------------------
	public void setup() {
		size(displayWidth,displayHeight);
		m = new Movie(this,"fingers.mov");
		m.play();
	}

	public void movieEvent(Movie m) {
		m.read();
	}
	
	public void draw() {
		background(255);
		image(m,0,0,width,height);
	}

}
