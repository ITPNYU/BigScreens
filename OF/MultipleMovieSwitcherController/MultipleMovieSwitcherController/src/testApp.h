#pragma once

#include "ofMain.h"
#include "ofxOsc.h"
#include "ofxXmlSettings.h"

#define HOST "128.122.151.65"
#define PORT 12345
#define NUM_MOVIES 3

//--------------------------------------------------------
class testApp : public ofBaseApp {

	public:

	void setup();
	void update();
	void draw();
	
	void keyPressed(int key);
	void keyReleased(int key);
	void mouseMoved(int x, int y );
	void mouseDragged(int x, int y, int button);
	void mousePressed(int x, int y, int button);
	void mouseReleased(int x, int y, int button);
	void windowResized(int w, int h);
	void dragEvent(ofDragInfo dragInfo);
	void gotMessage(ofMessage msg);		
	
	ofTrueTypeFont		font;
    
	ofxOscSender senders[NUM_MOVIES];
    int movie;
};

