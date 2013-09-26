/**
 * Simple Template for the Big Screens Class, Fall 2013
 * <https://github.com/shiffman/Most-Pixels-Ever>
 * 
 * Note this project uses Processing 2.1
 */

package controller;

import mpe.client.*;
import processing.core.*;

public class Messenger extends PApplet {
	// --------------------------------------
	
	TCPClient client;
	PFont font;
	
	/////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////WHAT MODE ARE YOU RUNNING IN?/////////////////////////////
		/////////////////////////////////////////////////////////////////////////////////////
		enum Mode {
			LOCAL, ITP, IAC
		}
	
		// Where are you?
		// We need to know in order to connect to server
		Mode mode = Mode.ITP;
	
		int dir = -1;
		int max = 9;
	
		// --------------------------------------
		static public void main(String args[]) {
			PApplet.main(new String[] { "controller.Messenger" });
		}
	
		// --------------------------------------
		public void setup() {
			size(480, 320);
	
			smooth();
			frameRate(20);
			font = createFont("Arial", 18);
	
			// set up the client
			String path = "mpefiles/"
					+ (mode == Mode.LOCAL ? "local" : "6screens" )
					+ "/asynch" + (mode == Mode.ITP ? "ITP" : "") + ".xml";
		
		// make a new Client using an XML file
		client = new TCPClient(this, path);
		
		// IMPORTANT, YOU MUST START THE CLIENT!
		client.start();
		
		// println(client.isAsynchronous());
		// println(client.isReceiver());
	}
	
	// Change colors values based on mouse position
	public void draw() {
		background(255);
		textFont(font);
		fill(0);
		textAlign(CENTER);
		textSize(10);
		text("Press UP to load images. RIGHT to advance. LEFT to go back.",
			 width / 2, height / 3);
		
	}
	
	// --------------------------------------
	// asynchreceive must be set to true in
	// asynch.xml to receive data here
	public void dataEvent(TCPClient c) {
		println("Raw message: " + c.getRawMessage());
		if (c.messageAvailable()) {
			String[] msgs = c.getDataMessage();
			for (int i = 0; i < msgs.length; i++) {
				println("Parsed message: " + msgs[i]);
			}
		}
		
	}
	
	void broadcast() {
		String command = "";
		switch (dir) {
			case 0:
				command = "LOAD IMAGES";
				break;
			case 1:
				command = "MOVE FORWARD";
				break;
			case -1:
				command = "MOVE BACKWARD";
				break;
		}
		text("Broadcasting: " + command, width / 2, height / 2);
		if (frameCount > 1)
			client.broadcast(String.valueOf(dir));
	}
	
	public void keyPressed() {
    	if(key == CODED) {
    		if(keyCode == RIGHT) {
    			dir++;
    			if(dir >= max)
    				dir = 0;
    		}
    		else if(keyCode == LEFT) {
    			dir--;
    			if(dir < 0)
    				dir = (max-1);
    		}
    		else if(keyCode == UP) {
    			dir = -1;
    		}
    		
    	}
    	broadcast();
    }
}
