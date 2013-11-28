#pragma once

#include "ofMain.h"
#include "ofxOsc.h"
#include "ofxXmlSettings.h"

#define HOST "localhost"
#define PORT 12345

//--------------------------------------------------------
class testApp : public ofBaseApp {

	public:

		void setup();
		void update();
		void draw();

		void keyPressed(int key);
		void keyReleased(int key);
		void mouseMoved(int x, int y);
		void mouseDragged(int x, int y, int button);
		void mousePressed(int x, int y, int button);
		void mouseReleased(int x, int y, int button);
		void windowResized(int w, int h);
		void dragEvent(ofDragInfo dragInfo);
		void gotMessage(ofMessage msg);
		void loadSettings(string fileString);

	vector<ofVideoPlayer> movies;
    vector<char *> movieFiles;
    int numMovies;
	int state;

    ofxOscReceiver	receiver;
    int port;
	
	int count;
	int howOften;
	
	bool started;
	bool fullscreen;
	
	char * host1;
	char * host2;
	int port1;
	int port2;
	
	int movieWidth;
	int movieHeight;
	int movieX;
	int movieY;
	
	bool looping;
	
	bool frameByframe;
	
	
	ofxOscSender sender1;
	ofxOscSender sender2;
};

