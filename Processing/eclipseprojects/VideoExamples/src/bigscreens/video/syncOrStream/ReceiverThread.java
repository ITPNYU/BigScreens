package bigscreens.video.syncOrStream;
import processing.core.*;
import java.awt.image.*; 
import javax.imageio.*;
import java.net.*;
import java.io.*;

public class ReceiverThread extends Thread{
	// Port we are receiving.
	  int port; 
	  DatagramSocket ds; 
	  // A byte array to read into (max size of 65536, could be smaller)
	  byte[] buffer = new byte[65536]; 

	  boolean running;    // Is the thread running?  Yes or no?
	  boolean available;  // Are there new tweets available?

	  // Start with something 
	  PImage img;
	  
	  PApplet parent;

	  ReceiverThread (PApplet p, int w, int h, int prt) {
		parent = p;
	    img = parent.createImage(w,h,PApplet.RGB);
	    running = false;
	    available = true; // We start with "loading . . " being available
	    port = prt;
	    
	    try {
	      ds = new DatagramSocket(port);
	    } catch (SocketException e) {
	      e.printStackTrace();
	    }
	  }

	  PImage getImage() {
	    // We set available equal to false now that we've gotten the data
	    available = false;
	    return img;
	  }

	  boolean available() {
	    return available;
	  }

	  // Overriding "start()"
	  public void start () {
	    running = true;
	    super.start();
	  }

	  // We must implement run, this gets triggered by start()
	  public void run () {
	    while (running) {
	      checkForImage();
	      // New data is available!
	      available = true;
	    }
	  }

	  void checkForImage() {
	    DatagramPacket p = new DatagramPacket(buffer, buffer.length); 
	    try {
	      ds.receive(p);
	    } 
	    catch (IOException e) {
	      e.printStackTrace();
	    } 
	    byte[] data = p.getData();

	    //println("Received datagram with " + data.length + " bytes." );

	    // Read incoming data into a ByteArrayInputStream
	    ByteArrayInputStream bais = new ByteArrayInputStream( data );

	    // We need to unpack JPG and put it in the PImage img
	    img.loadPixels();
	    try {
	      // Make a BufferedImage out of the incoming bytes
	      BufferedImage bimg = ImageIO.read(bais);
	      // Put the pixels into the PImage
	      bimg.getRGB(0, 0, img.width, img.height, img.pixels, 0, img.width);
	    } 
	    catch (Exception e) {
	      e.printStackTrace();
	    }
	    // Update the PImage pixels
	    img.updatePixels();
	  }


	  // Our method that quits the thread
	  void quit() {
	    System.out.println("Quitting."); 
	    running = false;  // Setting running to false ends the loop in run()
	    // In case the thread is waiting. . .
	    interrupt();
	  }
}
