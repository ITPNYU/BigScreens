/*
* Button classes and the functionality the buttons invoke
 */

class Button {
  int side, x, y;
  String label;

  Button(String _label, int xMult) {
    side = 60;
    x = width-((side + 10)*xMult);
    y = side/4;
    label = _label;
  } 

  void display() {
    rectMode(CORNER);
    stroke(255);
    fill(0);
    textSize(14);
    textAlign(CENTER, CENTER);
    rect(x, y, side, side/2);
    fill(255);
    text(label, x+(side/2), y+(side/5));
  }

  boolean isHovered() {
    if (mouseX > x && mouseX < x+side && mouseY > y && mouseY < y+(side/2))
      return true;
    else 
      return false;
  }
}

// Multi-state button
class ToggleButton extends Button {
  String onLabel, offLabel;
  boolean isOn;

  ToggleButton(String _label, int xMult, String _offLabel) {
    super(_label, xMult);
    label = _label;
    onLabel = _label;
    offLabel = _offLabel;
    isOn = true;
  }
  void toggle(boolean _isOn) {
    isOn = _isOn;
    if (isOn)
      label = offLabel;
    else
      label = onLabel;
  }
}

void load() {
  // Pause drawing while we load the file
  initialize();
  isDrawable = false;
  selectInput("Load Graph", "loadBeats");
}

void loadBeats(File file) {
  if (file == null) {
    println("Loading graph cancelled.");
    return;
  }
  String[] savedBeats = loadStrings(file.getAbsolutePath());
  beats = new Beat[savedBeats.length];
  for (int i = 0; i < savedBeats.length; i++) {
    String[] savedBeat = savedBeats[i].split(", ");
    beats[i] = new Beat(Float.parseFloat(savedBeat[0]), Float.parseFloat(savedBeat[1]), Boolean.parseBoolean(savedBeat[2]));
  }


  // Re-calculate
  interpolate();
  calcTrans();

  // Allow drawing again
  isDrawable = true;

  // Get ready to play
  isPlayable = true;
}

void save() {
  selectOutput("Save This Graph", "saveBeats");
}

void saveBeats(File file) {
  String[] savedBeats = new String[beats.length];
  String concatenator = ", ";
  for (int i = 0; i < beats.length; i++) {
    Beat beat = beats[i];
    String savedBeat = "" + beat.beat;
    savedBeat += concatenator + (beat.isUserCreated ? beat.rawTempo : mouseYMin);
    savedBeat += concatenator + beat.isUserCreated;
    savedBeats[i] = savedBeat;
  }
  saveStrings(file.getAbsolutePath(), savedBeats);
  println("Saved " + file.getName() + " to: " + file.getAbsolutePath());
}

void setAudioFile() {
  selectInput("Select Audio File", "loadAudio");
}

// Load audio file
void loadAudio(File file) {
  if (file == null) {
    println("Loading audio file cancelled.");  
    return;
  }

  try {
    sb.addAudio(file.getAbsolutePath());
  }
  catch(Exception e) {
    println("No audio");
  }
}

void setScenesFolder() {
  selectFolder("Select Folder to Load Images or Movies", "loadScenes");
}

// Load images from selected folder
void loadScenes(File folder) {
  if (folder == null) {
    println("Loading images/movies cancelled.");  
    return;
  }

  // Clear out existing scenes
  sb.initScenes();

  File [] files = folder.listFiles();
  for (int i = 0; i < files.length; i++) {
    String filename = files[i].getName().toLowerCase();
    String path = files[i].getAbsolutePath();
    if (filename.matches(".+\\.(png|jpg|jpeg|gif|tga)$")) {
      try {
        sb.addStill(path);
      }
      catch(Exception e) {
        println("No image at: " + i);
      }
    }
    else if (filename.matches(".+\\.mov$")) {
      sb.addClip(path);
    }
  }
}

void setExportFolder() {
  selectFolder("Select Media Folder", "setExportPath");
}

void setExportPath(File folder) {
  if (folder == null) {
    println("Exporting data canceled.");
    return;
  }
  exportPath = folder.getAbsolutePath();
  println(exportPath);
}

