/**
 * Fall 2014 Big Screens Examples
 * <https://github.com/ITPNYU/BigScreens>
 * 
 * Note this project uses Processing 2.0.1
 */

package bigscreens.video.syncOrStream;

import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.*;
import processing.video.Movie;

public class StreamingVideoPlayer extends PApplet {

	Movie m;
	OscP5 oscP5;
	boolean started;
	
	// IP Addresses for Client Video Players
	NetAddress client1;
	NetAddress client2;
	
	// Streaming video and thread
	PImage s;
	ReceiverThread thread;
		
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	/////////////////////////WHAT MODE ARE YOU RUNNING IN?/////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////

	public static enum Mode {
		LOCAL, SCREENS
	}	
	public static Mode mode = Mode.LOCAL;
	
	// Change the ID to test locally
	static public int ID = 1;
	
	// There are 3 screens
	int NUM_SCREENS = 3;
	
	// Middle computer is the master video player
	static public int MASTER_ID = 1;
	
	// Filename for video
	static public String filename;	
	
	// Scale for testing in local mode
	static public float scale = 1;

	//--------------------------------------
	static public void main(String args[]) {
		if(mode == Mode.LOCAL) {
			scale = .34f;
			// Windowed
			PApplet.main(new String[] {bigscreens.video.syncOrStream.StreamingVideoPlayer.class.getName() });

		}
		else {
			ID = IDGetter.getID();
			// FullScreen Exclusive Mode
			PApplet.main(new String[] {"--present", bigscreens.video.syncOrStream.StreamingVideoPlayer.class.getName() });
		}
	}


	//--------------------------------------
	public void setup() {				
		size(parseInt(displayWidth*scale),parseInt(displayHeight*scale));
		
		// Load files based on ID of client (0:Left, 1:Middle, 2:Right)
		switch(ID) {
		case 0:
			filename = "fingers.mov";
			break;
		case 1:
			started = true;
			filename = "fingers.mov";
			break;
		case 2:
			filename = "fingers.mov";
			break;
		}
		
		m = new Movie(this,filename);	
		m.loop();

		
		// Master needs IP addresses of Clients
		if(ID == MASTER_ID) {
			// Assign different IP address depending on mode
			if(mode == Mode.LOCAL) {
				// Where we are sending out message
				client1 = new NetAddress("127.0.0.1",12345);
				client2 = new NetAddress("127.0.0.1",12345);
			}
			else {
				// Where we are sending out message
				client1 = new NetAddress("192.168.130.240",12345);
				client2 = new NetAddress("192.168.130.242",12345);			
			}
		}
		
		// Assign ports for syncing movie and streaming video
		int mPort, sPort; 
		switch(ID) {
		case 1:
			sPort = 9002;
			mPort = 12001;
			break;
		default:
			sPort = 9001;
			mPort = 12345;
			break;
		}
		
		// osc object for syncing movies
		oscP5 = new OscP5(this,mPort);
		
		// Thread for receiving streaming video		
		s = createImage(320,240,RGB);
		thread = new ReceiverThread(this, s.width,s.height, sPort);
		thread.start();

	}

	public void movieEvent(Movie m) {
		m.read();
	}

	public void draw() {
		
		// Display movie for 2 seconds, every 3 seconds
		if (m.time()%3 < 2 && started) {
			image(m,0,0,width,height);
		}
		// Otherwise, show the stream
		else {
			if (thread.available()) {
				s = thread.getImage();
			}
			
		  // Stretch streaming video across all screens
		  float sWidth = width*NUM_SCREENS;
		  float mult = sWidth/s.width;
		  float sHeight = mult*s.height;
		  // Display the middle sliver of the image
		  image(s,-ID*width,-sHeight*.42f, sWidth, sHeight);
		}
		
		if (ID == MASTER_ID && frameCount % 30 == 0) {
			OscMessage msg = new OscMessage("/time");
			msg.add(m.time());
			oscP5.send(msg, client1); 
			if(mode == Mode.SCREENS) {
				oscP5.send(msg, client2); 				
			}
		}
		
		if (mode == Mode.LOCAL) {
			frame.setLocation(ID*width,0);
		}
	}

	public void oscEvent(OscMessage msg) {
		//println("Here comes a message: ");
		//println("Address pattern: " + msg.addrPattern());
		float t = msg.get(0).floatValue(); 
		System.out.println("Screen: " + ID + " is jumping to " + t);
		if(!started) {
			started = true;
		}
		m.jump(t);
	}

}

