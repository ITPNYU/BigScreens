// Manages display of your content
class Storyboard {
  PApplet parent;

  // Your content
  ArrayList<Scene> scenes = new ArrayList<Scene>();
  Minim minim;
  AudioPlayer audio;

  boolean hasScenes = false;
  boolean hasAudio = false;

  int sceneIndex;
  float counter, startTime, light, lightSpeed, rMult, gMult, bMult;
  float tempo; // how quickly to advance through scene

  Storyboard(PApplet p) {
    parent = p;
    // Get images from Flickr or Google
    //scenes = getFlickr();
    //scenes = getGoogle();
    minim = new Minim(parent);

    init();
    initSceneIndex();
    initScenes();
  }

  void init() {
    counter = 0;
    startTime = millis();    
    light = 0;
    lightSpeed = 1;
    tempo = 1;

    // Weighting rgb channels randomly
    // to create different colors
    rMult = random(.5, 2);
    gMult = random(.5, 2);
    bMult = random(.5, 2);
  }

  void initSceneIndex() {
    sceneIndex = 0;
  }

  void initScenes() {
    scenes = new ArrayList<Scene>();
  }

  void addAudio(String path) {
    audio = minim.loadFile(path);
    audio.cue(0);

    hasAudio = true;
    println("Loaded audio from: " + path);
    println("The audio is " + seconds + "s long.");
  }

  float getAudioDuration() {
    return Math.round(audio.length()/1000);
  }

  void addStill(String path) {
    scenes.add(new Scene(loadImage(path)));
    hasScenes = true;
    println("Loaded image from: " + path);
  }

  void addClip(String path) {
    scenes.add(new Scene(new Movie(parent, path)));
    hasScenes = true;
    println("Loaded movie from: " + path);
  }

  void startEvent() {
    init();
    initSceneIndex();
    if (hasAudio) {
      audio.cue(0);
      audio.play();
    }
  }

  void stopEvent() {
    if (hasAudio)
      audio.pause();
  }

  void update(float _tempo) {
    tempo = _tempo; 
    // Set lightspeed so that it takes 
    // 1/2 the entire duration of the scene
    // to go from black to white
    lightSpeed = 255/(totalFrames/tempo);
  }

  void display(boolean isOn) {
    background(0);
    if (isOn)
      light+=lightSpeed;
    else
      light-=lightSpeed;


    // Cycling through scenes
    if (hasScenes) {
      Scene thisScene = scenes.get(sceneIndex);
      thisScene.run(counter, light);
    }
    // Or just "Color-Shift" mode
    else {
      noStroke();
      fill(light*rMult, light*gMult, light*bMult);
      rect(0, 0, width, height);
    }
  }

  // Play scene
  void run() {
    checkForEndOfScene();

    // Do something half-way through the scenes
    if (counter < totalFrames/2)
      display(true);
    else
      display(false);
    counter+=tempo;
  }


  // Are we done? If so, re-init counter
  // and other things
  void checkForEndOfScene() {
    if (counter > totalFrames) {
      init();
      if (hasScenes) {
        sceneIndex++;
        if (sceneIndex > scenes.size()-1) {
          sceneIndex = 0;
        }
      }
    }
  }
}

