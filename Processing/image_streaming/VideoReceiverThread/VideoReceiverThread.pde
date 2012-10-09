// Daniel Shiffman
// <http://www.shiffman.net>

// A Thread using receiving UDP to receive images

import java.awt.image.*; 
import javax.imageio.*;

PImage video;
ReceiverThread thread;

void setup() {
  size(400,300);
  video = createImage(320,240,RGB);
  thread = new ReceiverThread(video.width,video.height);
  thread.start();
}

 void draw() {
  if (thread.available()) {
    video = thread.getImage();
  }

  // Draw the image
  background(0);
  imageMode(CENTER);
  image(video,width/2,height/2);
}



