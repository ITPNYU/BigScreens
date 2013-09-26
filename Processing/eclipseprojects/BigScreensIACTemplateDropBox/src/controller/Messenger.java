/**
 * Simple Template for the Big Screens Class, Fall 2013
 * <https://github.com/ITPNYU/BigScreens>
 * 
 * Note this project uses Processing 2.0.1
 */

package controller;

import mpe.client.*;
import processing.core.*;

@SuppressWarnings("serial")
public class Messenger extends PApplet {
    //--------------------------------------

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
	Mode mode = Mode.LOCAL;
	
    //--------------------------------------
    static public void main(String args[]) {
        PApplet.main(new String[] { "controller.Messenger" });
    }

    //--------------------------------------
    public void setup() {
        size(255,255);
        
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
        
        //println(client.isAsynchronous());
        //println(client.isReceiver());
    }
    
    // Change colors values based on mouse position
    public void draw() {
    	int r = (int) mouseX;
    	int g = (int) (mouseX/2 + mouseY/2);
    	int b = (int) mouseY;
		background(r,g,b);
		textFont(font);
		String msg = r + "," + g + "," + b;    	
		fill(0);
		textAlign(CENTER);
		text("Broadcasting: " + msg,width/2,height/2);
		if(frameCount > 1)
			client.broadcast(msg);					
    }
    
    //--------------------------------------
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
}

