
/**
 * Simple Bouncing Ball Demo
 */

package mpe.examples.balls;

//Import necessary libraries
import java.util.ArrayList;

import mpe.client.*;
import processing.core.PApplet;

public class BouncingBallsManual extends PApplet {

	//--------------------------------------
	final int ID = 0;

	ArrayList<Ball> balls;
	TCPClient client;

	//--------------------------------------
	static public void main(String args[]) {
		PApplet.main(new String[] { "mpe.examples.balls.BouncingBallsManual" });
	}

	//--------------------------------------
	public void setup() {
		// Make a new Client with an INI file.  
		// sketchPath() is used so that the INI file is local to the sketch
		client = new TCPClient(sketchPath("mpefiles/mpe"+ID+".ini"), this, false);

		// The size is determined by the client's local width and height
		size(client.getLWidth(), client.getLHeight());

		// the random seed must be identical for all clients
		randomSeed(1);

		smooth();

		// add a "randomly" placed ball
		balls = new ArrayList<Ball>();
		Ball ball = new Ball(this, client, random(client.getMWidth()), random(client.getMHeight()));
		balls.add(ball);

		// IMPORTANT, YOU MUST START THE CLIENT!
		client.start();
	}

	//--------------------------------------
	public void draw() {
		frame.setLocation(client.getID()*client.getLWidth(),0);

		if (client.isRendering()) {
			// before we do anything, the client must place itself within the 
			//  larger display (this is done with translate, so use push/pop if 
			//  you want to overlay any info on all screens)
			client.placeScreen();
			// clear the screen
			background(255);

			// move and draw all the balls
			for (Ball ball : balls) {
				ball.calc();
				ball.draw();
			}

			// alert the server that you've finished drawing a frame
			client.done();
		}
	}

	//--------------------------------------
	// Triggered by the client whenever a new frame should be rendered.
	public void frameEvent(TCPClient c) {
		// read any incoming messages
		if (c.messageAvailable()) {
			String[] msg = c.getDataMessage();
			String[] xy = msg[0].split(",");
			float x = Integer.parseInt(xy[0]);
			float y = Integer.parseInt(xy[1]);
			balls.add(new Ball(this,client, x, y));
		}
	}


	//--------------------------------------
	// Adds a Ball to the stage at the position of the mouse click.
	public void mousePressed() {
		// never include a ":" when broadcasting your message
		int x = mouseX + client.getXoffset();
		int y = mouseY + client.getYoffset();
		client.broadcast(x + "," + y);
	}

}

