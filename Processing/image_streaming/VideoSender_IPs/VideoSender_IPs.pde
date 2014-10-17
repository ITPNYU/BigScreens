import processing.video.*;

import javax.imageio.*;
import java.awt.image.*; 
import java.net.*;
import java.io.*;


// This is the port we are sending to
int [] ports = { 9001, 9002, 9003 };

//@IAC
//String [] IPS = {
//  "192.168.130.240",
//  "192.168.130.241",
//  "192.168.130.242"
//};

//@ITP
//String [] IPS = {
//  "128.122.151.83",
//  "128.122.151.65",
//  "128.122.151.64",
//};

// Local
String [] IPS = {
  "localhost",
  "localhost",
  "localhost",
};

// This is our object that sends UDP out
DatagramSocket ds; 
// Capture object
Capture cam;

void setup() {
  size(320,240);
  // Setting up the DatagramSocket, requires try/catch
  try {
    ds = new DatagramSocket();
  } catch (SocketException e) {
    e.printStackTrace();
  }
  // Initialize Camera
  cam = new Capture( this, width,height,30);
  cam.start();
}

void captureEvent( Capture c ) {
  c.read();
  // Whenever we get a new image, send it!
  broadcast(c);
}

void draw() {
  image(cam,0,0);
}


// Function to broadcast a PImage over UDP
// Special thanks to: http://ubaa.net/shared/processing/udp/
// (This example doesn't use the library, but you can!)
void broadcast(PImage img) {

  // We need a buffered image to do the JPG encoding
  BufferedImage bimg = new BufferedImage( img.width,img.height, BufferedImage.TYPE_INT_RGB );

  // Transfer pixels from localFrame to the BufferedImage
  img.loadPixels();
  bimg.setRGB( 0, 0, img.width, img.height, img.pixels, 0, img.width);

  // Need these output streams to get image as bytes for UDP communication
  ByteArrayOutputStream baStream	= new ByteArrayOutputStream();
  BufferedOutputStream bos		= new BufferedOutputStream(baStream);

  // Turn the BufferedImage into a JPG and put it in the BufferedOutputStream
  // Requires try/catch
  try {
    ImageIO.write(bimg, "jpg", bos);
  } 
  catch (IOException e) {
    e.printStackTrace();
  }

  // Get the byte array, which we will send out via UDP!
  byte[] packet = baStream.toByteArray();

  // Send JPEG data as a datagram
  println("Sending datagram with " + packet.length + " bytes");
  try {
    for(int c = 0; c < IPS.length; c++) {
      ds.send(new DatagramPacket(packet,packet.length, InetAddress.getByName(IPS[c]), ports[c]));
    }
  } 
  catch (Exception e) {
    e.printStackTrace();
  }
}

