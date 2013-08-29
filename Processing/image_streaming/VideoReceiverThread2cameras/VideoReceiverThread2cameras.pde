// Daniel Shiffman
// <http://www.shiffman.net>

// A Thread using receiving UDP to receive images

import java.awt.image.*; 
import javax.imageio.*;
import java.net.*;
import java.io.*;

PImage video1;
PImage video2;

ReceiverThread thread1;
ReceiverThread thread2;

void setup() {
  size(640,240);
  video1 = createImage(320,240,RGB);
  video2 = createImage(320,240,RGB);
  thread1 = new ReceiverThread(video1.width,video1.height, 9100);
  thread1.start();

  thread2 = new ReceiverThread(video2.width,video2.height, 9101);
  thread2.start();


}

 void draw() {
  if (thread1.available()) {
    video1 = thread1.getImage();
  }

  if (thread2.available()) {
    video2 = thread2.getImage();
  }


  // Draw the image
  background(0);
  image(video1,0,0);
  image(video2,320,0);
}



