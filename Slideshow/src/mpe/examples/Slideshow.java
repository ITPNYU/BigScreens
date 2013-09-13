/**
 * Simple Template for the Big Screens Class, Fall 2013
 * <https://github.com/ITPNYU/BigScreens>
 * 
 * Note this project uses Processing 2.0.1
 */

package mpe.examples;

import mpe.client.*;
import processing.core.*;
import java.io.*;
import java.util.*;

@SuppressWarnings("serial")
public class Slideshow extends PApplet {
	
	
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	/////////////////////////WHAT MODE ARE YOU RUNNING IN?/////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	
	public static enum Mode {
		LOCAL, MPE, CUSTOM
	}
	
	public static Mode mode = Mode.MPE;
	
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
	
	int index;
	int lastImage;
	ArrayList<PImage> images;
	
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
			PApplet.main(new String[] {"mpe.examples.Slideshow" });
			// FullScreen Exclusive Mode
		} else {
			PApplet.main(new String[] {"--present","--bgcolor=#000000", "mpe.examples.Slideshow" });
		}
	}
	
	//--------------------------------------
	public void setup() {
		
		
		// If we are using the library set everything up
		if (MPE) {
			// make a new Client using an XML file
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
				size(client.getLWidth(), client.getLHeight());
			}
			// the size is determined by the client's local width and height
			mWidth = client.getMWidth();
			mHeight = client.getMHeight();
			
		} else {
			// Otherwise with no library, force size
			size(parseInt(11520*scale),parseInt(1080*scale), P2D);
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
	//////////////////////////////INIT YOUR VARIABLES//////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	
	//--------------------------------------
	// Start over!
	// Happens automatically when new client connects to mpe server
	public void resetEvent(TCPClient c) {
		
		///////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////YOUR SETUP//////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////
		
		// random and noise seed must be identical for all clients
		randomSeed(1);
		noiseSeed(1);
		background(255);
		
		index = 0;
		lastImage = 0;
		images = new ArrayList<PImage>();
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
	public void dataEvent(TCPClient c) {
		String[] msg = c.getDataMessage();
		int command = Integer.valueOf(msg[0]);
		
		switch(command) {
			case -1:
				selectFolder("Select folder to load images:", "folderSelected");
				break;
			default:
				index = command;
		}
		
		if(index < 0)
			index = lastImage;
		else if(index > lastImage)
			index = 0;
		
	}
	
	public void folderSelected(File selection) {
		if (selection == null) {
		    println("Window was closed or the user hit cancel.");
		} 
		else {
			File [] files = selection.listFiles();
			images = new ArrayList<PImage>();
			for (int i = 0; i < files.length; i++) {
			    String filename = files[i].getName().toLowerCase();
			    String path = files[i].getAbsolutePath();
			    if (filename.matches(".+\\.(png|jpg|jpeg|gif|tga)$")) {
					try {
						images.add(loadImage(path));
					}
					catch(Exception e) {
						println("No image at: " + i);
					}
			    }
			}
			lastImage = images.size()-1;
			client.broadcast(String.valueOf(images.size()));
			println("Loaded " + (lastImage+1) + " images from: " + selection.getAbsolutePath());
		}
	}
	
	
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
		
		background(0);
		// Only if there are images
		if(images.size() > 0) {
			PImage currentImage = images.get(index);
			image(currentImage, 0, 0, mWidth, mHeight);
		}
	}
	
	
}
