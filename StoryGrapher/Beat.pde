// Keeps track of "tempo" as we move across the storyboard graph

class Beat {
  float beat, tempo, rawTempo, scaledTempo, counter;
  boolean isUserCreated;

  Beat(float _beat, float _tempo, boolean _isUserCreated) {
    beat = _beat;
    rawTempo = _tempo;
    tempo = 0;
    isUserCreated = _isUserCreated;
  }

  void init() {    
    // A tempo of 1 frame (3 minutes at 60fps) means take all 3 minutes to play out 1 scene
    // Max tempo means 10 scenes will play out in 1 second
    // Tempo values are mapped "on a curve" (natural log curve)
    // Play with expMax and expMin to play with which part of the curve you'd like to map your values to
    float max = seconds*10;
    float expMin = -5;
    float expMax = 2.5;
    float exp = map(rawTempo, height, mouseYMin, expMin, expMax);   
    float log = exp(exp);
    tempo = map(log, exp(expMin), exp(expMax), 1, max);
    //println("Beat: " + beat + "\tRaw Tempo: " + rawTempo + "\tNatural Log: " + log + "\tTempo: " + tempo);
  }

  void drawDot(boolean isRed) {
    noStroke();
    if (isRed)
      fill(255, 0, 0);
    else
      fill(0, 200);
    ellipse(beat, rawTempo, 10, 10);
  }
}



// Interpolate beats so that
// every x-position has a beat value
void interpolate() {

  // Calculate the first/last beat
  for (int i = 0; i < beats.length; i++) {
    if (beats[i].isUserCreated) {
      firstBeatInd = i;
      break;
    }
  }

  for (int i = beats.length-1; i > 0; i--) {
    if (beats[i].isUserCreated) {
      lastBeatInd = i;
      break;
    }
  }

  int prevBeatInd = firstBeatInd;
  int nextBeatInd = findNextBeat(firstBeatInd + 1);
  int indRange = nextBeatInd-prevBeatInd;
  PVector prevBeat = new PVector (beats[firstBeatInd].beat, beats[firstBeatInd].rawTempo);
  PVector nextBeat = new PVector (beats[nextBeatInd].beat, beats[nextBeatInd].rawTempo);
  PVector range = PVector.sub(nextBeat, prevBeat);
  float progress = 0;
  for (int i = firstBeatInd; i <= lastBeatInd; i++) {
    Beat thisBeat = beats[i];
    float beat = thisBeat.beat;
    float tempo = thisBeat.rawTempo;
    if (thisBeat.isUserCreated) {
      prevBeatInd = i;
      nextBeatInd = findNextBeat(i+1);
      indRange = nextBeatInd - prevBeatInd;
      prevBeat.set(beat, tempo);
      nextBeat.set(beats[nextBeatInd].beat, beats[nextBeatInd].rawTempo);
      range = PVector.sub(nextBeat, prevBeat);
    }
    else {
      progress = ((float)( i - prevBeatInd))/indRange;
      beat = prevBeat.x + Math.round(progress*range.x);
      tempo = prevBeat.y + Math.round(progress*range.y);
      thisBeat = createBeat(beat, tempo, false);
    }    
    thisBeat.init();
    beats[i] = thisBeat;
  }

  // Scale tSpeed so that it takes the full
  // duration of the piece to play all the beats
  numBeats = (lastBeatInd-firstBeatInd) + 1;
  tSpeed = (float) numBeats/totalFrames;
  sb.update(beats[firstBeatInd].tempo);
}

int findNextBeat(int _i) {
  for (int i = _i; i < lastBeatInd+1; i++) {
    if (beats[i].isUserCreated)
      return i;
  }
  // If there are no more beats
  // Send the last beat
  return lastBeatInd;
}


void calcTrans() {
  // Clear it out each time
  transitions = new ArrayList<Float>();
  float counter = 0;
  for (float i = 0; i <= lastBeatInd; i += tSpeed) {
    int b = (int) Math.round(i);
    counter += beats[b].tempo;
    if (counter > totalFrames) {
      transitions.add(new Float(b));
      counter = 0;
    }
  }
}

void drawTrans() {
  strokeWeight(1);
  stroke(128, 100);
  for (int i = 0; i < transitions.size(); i++) {
    float transition = transitions.get(i);
    line (transition, 0, transition, height);
    fill(255, 0, 0);
    textSize(12);
    text((int)Math.round(i)+1, transition, height-10);
  }
}

