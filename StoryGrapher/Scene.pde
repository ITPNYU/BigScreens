class Scene {
  boolean isClip = false;
  PImage still;
  Movie clip;
  float dur;

  Scene(PImage img) {
    still = img;
  }

  Scene(Movie mov) {
    
    clip = mov;
    clip.play();
    dur = clip.duration();
    clip.stop();
    println("This movie is: " + dur + "s long.");
    isClip = true;
  }

  void run(float counter, float light) {
    if (isClip) {
      float s = (counter/totalFrames)*dur;
      clip.play();
      clip.jump(s);
      if (clip.available()) {
        clip.read();
        tint(255, 255);
        image(clip, width/2, height/2);
      }
      clip.stop();
    }
    else {
      tint(255, light);
      image(still, width/2, height/2, still.width, still.height);
    }
  }
}

