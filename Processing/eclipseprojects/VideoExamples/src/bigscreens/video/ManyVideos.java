/**
 * Simple Template for the Big Screens Class, Fall 2011
 * <https://github.com/shiffman/Most-Pixels-Ever>
 * 
 * Note this project uses Processing 1.5.1
 */

package bigscreens.video;

import processing.core.*;
import processing.opengl.*;
import processing.video.Movie;

public class ManyVideos extends PApplet {

	//--------------------------------------
	static public void main(String args[]) {
		// Windowed
		PApplet.main(new String[] { bigscreens.video.ManyVideos.class.getName() });
		// FullScreen Exclusive Mode
		//PApplet.main(new String[] {"--present", bigscreens.video.ManyVideos.class.getName() });
	}

	Movie[] movies;

	//--------------------------------------
	public void setup() {
		size(800,600);

		movies = new Movie[10];
		for (int i = 0; i < movies.length; i++) {
			movies[i] = new Movie(this,"fingers.mov");
		}


	}

	public void movieEvent(Movie m) {
		m.read();
	}

	int counter = 0;

	public void draw() {
		background(255);

		if (counter < movies.length) {
			movies[counter].loop();
			counter++;
			println(counter);
		}



		randomSeed(1);
		for (int i = 0; i < movies.length; i++) {
			float x = random(width);
			float y = random(height);
			image(movies[i],x,y,64,64);

		}


	}





}
