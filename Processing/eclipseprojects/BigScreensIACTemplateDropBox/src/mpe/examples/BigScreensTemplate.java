/**
 * Simple Template for the Big Screens Class, Fall 2013
 * <https://github.com/shiffman/Most-Pixels-Ever>
 * 
 * Note this project uses Processing 2.1
 */

package mpe.examples;

import mpe.client.*;
import processing.core.*;

public class BigScreensTemplate extends PApplet {
	
	
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	/////////////////////////WHAT MODE ARE YOU RUNNING IN?/////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	
	public static enum Mode {
		LOCAL, MPE, CUSTOM
	}
	
	public static Mode mode = Mode.LOCAL;
	
	// Client ID (0: Left, 1: Middle, 2: Right)
	// Should be adjusted only for "local" testing
	//--------------------------------------
	int ID = 0;
		

	// Only fiddle with this if you choose Mode.CUSTOM
	//--------------------------------------

	// Set it to 1 for actual size, 0.5 for half size, etc.
	// This is useful for testing MPE locally and scaling it down to fit to your screen
	public static float scale = 0.15f;

	// if this is true, it will use the MPE library, otherwise just run stand-alone
	public static boolean MPE = true;
	public static boolean local = true;	
	

	TCPClient client;

	// "Real" dimensions of screen
	final int tWidth = 11520;
	final int tHeight = 1080;
	
	// These we'll use for master width and height instead of Processing's built-in variables
	int mWidth;
	int mHeight;
	
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////YOUR VARIABLES/////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////

	float x;
	float xspeed;
	float w;

	float yoffset;
	float dyoffset;

	float r;
	float g;
	float b;


	//--------------------------------------
	static public void main(String args[]) {
		
		//Set mode settings
		if(mode == Mode.LOCAL) {
			MPE = false;
			local = true;
			scale = .15f;			
		}
		else if(mode == Mode.MPE) {
			MPE = true;
			local = false;
			scale = 1.0f;
		}
		
		// Windowed
		if (local) {
			PApplet.main(new String[] {"mpe.examples.BigScreensTemplate" });
		// FullScreen Exclusive Mode
		} else {
			PApplet.main(new String[] {"--present","--exclusive", "--bgcolor=#000000", "mpe.examples.BigScreensTemplate" });
		}
	}

	//--------------------------------------
	public void setup() {
		
					
		// If we are using the library set everything up
		if (MPE) {
			// make a new Client using an INI file
			String path = "mpefiles/";
			if (local) {
				path += "local/mpe" + ID + ".xml";
			} else {
				ID = IDGetter.getID();
				path += "6screens/mpe" + ID + ".xml";
			}
			client = new TCPClient(this, path);
			// Not rendering with OPENGL for local testing
			if (local) {
				size((int)(client.getLWidth()*scale), (int)(client.getLHeight()*scale));
				client.setLocalDimensions((int)(ID*client.getLWidth()*scale), 0, (int)(client.getLWidth()*scale), (int)(client.getLHeight()*scale));
			} else {
				size(client.getLWidth(), client.getLHeight(),P2D);
			}
			// the size is determined by the client's local width and height
			mWidth = client.getMWidth();
			mHeight = client.getMHeight();
			
		} else {
			// Otherwise with no library, force size
			size(parseInt(11520*scale),parseInt(1080*scale));
			mWidth = tWidth;
			mHeight = tHeight;
		}
		
		smooth();		
		resetEvent(client);

		if (MPE) {
			// IMPORTANT, YOU MUST START THE CLIENT!
			client.start();
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////YOUR SETUP BELOW///////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	
	//--------------------------------------
	// Start over!
	// Happens automatically when new client connects to mpe server
	public void resetEvent(TCPClient c) {
		// random and noise seed must be identical for all clients
		randomSeed(1);
		noiseSeed(1);

		// re-initialize all of your variables
		x = 0;
		xspeed = 10.0f;
		w = 50f;

		yoffset = 0f;
		dyoffset = 1.0f;

		r = 255;
		g = 255;
		b = 255;

		background(255);		
	}
		
	//--------------------------------------
	// Keep the motor running... draw() needs to be added in auto mode, even if
	// it is empty to keep things rolling.
	public void draw() {

		// If we are on the 6 screens we want to preset the frame's location
		if (MPE && local) {
			frame.setLocation(ID*width,0);
		}

		// If we're not using the library frameEvent() will not be called automatically
		if (!MPE) {
			frameEvent(null);
		}
	}
	
	//--------------------------------------
	// Separate event for receiving data
	// Or a controller app
//	public void dataEvent(TCPClient c) {
//		String[] msg = c.getDataMessage();
//		String colors = msg[0];
//		float[] vals = parseFloat(colors.split(","));
//		r = vals[0];
//		g = vals[1];
//		b = vals[2];
//	}

	//--------------------------------------
	// Triggered by the client whenever a new frame should be rendered.
	// All synchronized drawing should be done here when in auto mode.
	public void frameEvent(TCPClient c) {
		
		// clear the screen
		if (!MPE || local) {
			scale(scale);
		}

		///////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////YOUR CODE//////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////

		// Receiving a message for background color
		// It is also possible to receive data inside frameEvent
		if (MPE && c.messageAvailable()) {
			String[] msg = c.getDataMessage();
			String colors = msg[0];
			float[] vals = parseFloat(colors.split(","));
			r = vals[0];
			g = vals[1];
			b = vals[2];
		}
		
		background(r,g,b);

		strokeWeight(6);
		stroke(0);
		// Draw a grid
		for (int i = 0; i < mWidth; i+=100) {
			line(i,0,i,mHeight);
		}

		for (int j = 0; j < mHeight; j+=100) {
			int y = (j + (int) yoffset) % mHeight;
			line(0,y,mWidth,y);
		}

		// Just some simple drawing stuff to demonstrate it works
		fill(0);
		rect(x,0,w,mHeight);

		x += xspeed;
		yoffset += dyoffset;

		if (x > mWidth) {
			x = 0;
		}
		
	}


}
