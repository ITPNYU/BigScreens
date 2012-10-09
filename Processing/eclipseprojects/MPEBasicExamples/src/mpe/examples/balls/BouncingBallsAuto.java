
/**
 * Simple Bouncing Ball Demo
 */

package mpe.examples.balls;

//Import necessary libraries
import java.util.ArrayList;

import mpe.client.*;
import processing.core.PApplet;

public class BouncingBallsAuto extends PApplet {

	final int ID = 1;

	ArrayList<Ball> balls;
	TCPClient client;

	//--------------------------------------
	static public void main(String args[]) {
		PApplet.main(new String[] { "mpe.examples.balls.BouncingBallsAuto" });
	}

	//--------------------------------------
	public void setup() {
		// make a new Client using an INI file
		// sketchPath() is used so that the INI file is local to the sketch
		client = new TCPClient(sketchPath("mpefiles/mpe"+ID+".ini"), this);

		// the size is determined by the client's local width and height
		size(client.getLWidth(), client.getLHeight());

		// the random seed must be identical for all clients
		randomSeed(1);

		smooth();
		background(255);

		// add a "randomly" placed ball
		balls = new ArrayList<Ball>();
		Ball ball = new Ball(this,client,random(client.getMWidth()), random(client.getMHeight()));
		balls.add(ball);

		// IMPORTANT, YOU MUST START THE CLIENT!
		client.start();
	}

	//--------------------------------------
	// Keep the motor running... draw() needs to be added in auto mode, even if
	// it is empty to keep things rolling.
	public void draw() {
		frame.setLocation(300+client.getID()*client.getLWidth(),300);
	}

	//--------------------------------------
	// Triggered by the client whenever a new frame should be rendered.
	// All synchronized drawing should be done here when in auto mode.
	public void frameEvent(TCPClient c) {
		// clear the screen     
		background(255);

		// move and draw all the balls
		for (Ball ball : balls) {
			ball.calc();
			ball.draw();
		}

		// read any incoming messages
		if (c.messageAvailable()) {
			String[] msg = c.getDataMessage();
			String[] xy = msg[0].split(",");
			float x = Integer.parseInt(xy[0]);
			float y = Integer.parseInt(xy[1]);
			balls.add(new Ball(this, client, x, y));
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
